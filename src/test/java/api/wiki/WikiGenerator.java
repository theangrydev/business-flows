/*
 * Copyright 2016-2017 Liam Williams <liam.williams@zoho.com>.
 *
 * This file is part of business-flows.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package api.wiki;

import api.usage.businessflow.GetTechnicalFailureTest;
import api.usage.happypath.*;
import api.usage.businessflow.IsTechnicalFailureTest;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import io.github.theangrydev.businessflows.Attempt;
import io.github.theangrydev.businessflows.BusinessFlow;
import io.github.theangrydev.businessflows.HappyPath;
import io.github.theangrydev.businessflows.Mapping;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static api.wiki.ApiDocumentation.apiDocumentation;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.text.WordUtils.uncapitalize;

//TODO: refactor before this gets out of hand
public class WikiGenerator {

    private static final Pattern SNAPSHOT_VERSION_PATTERN = Pattern.compile(".*(\\d+)-SNAPSHOT");

    private static final String INDEX_PAGE = "index.md";
    private static final String MARKDOWN_FILE_EXTENSION = ".md";

    private final String groupId;
    private final String artifactId;
    private final String version;

    public WikiGenerator(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public static void main(String[] args) throws URISyntaxException, IOException, ParseException, NoSuchMethodException, XmlPullParserException {
        MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
        Model model = mavenXpp3Reader.read(newBufferedReader(Paths.get("pom.xml"), UTF_8));
        WikiGenerator wikiGenerator = new WikiGenerator(model.getGroupId(), model.getArtifactId(), model.getVersion());
        if (wikiGenerator.weAreReleasing()) {
            wikiGenerator.copyDocsToReleaseDirectory();
        }
        wikiGenerator.generateCurrentDocumentation();
        wikiGenerator.generateVersionsIndexPage();
    }

    public boolean weAreReleasing() {
        return !SNAPSHOT_VERSION_PATTERN.matcher(version).matches();
    }

    public void copyDocsToReleaseDirectory() throws IOException {
        Path releaseDirectory = versionDocsDirectory();
        Files.createDirectories(releaseDirectory);
        Files.walk(masterDocsDirectory())
                .filter(WikiGenerator::isMarkdownFile)
                .forEach(path -> copyToReleaseDirectory(path, releaseDirectory));
    }

    public void generateVersionsIndexPage() throws IOException {
        Path indexPage = docsDirectory().resolve(INDEX_PAGE);
        String markup = versionsIndexMarkup();
        writePage(indexPage, markup);
    }

    public void generateCurrentDocumentation() throws IOException, ParseException, NoSuchMethodException {
        createDirectories(wikiDirectory());
        removeAllMarkdownFiles(wikiDirectory());
        List<ApiDocumentation> apiDocumentations = Arrays.asList(
                apiDocumentation(HappyPathAttemptTest.class, HappyPath.class.getMethod("happyPathAttempt", Attempt.class)),
                apiDocumentation(HappyAttemptTest.class, HappyPath.class.getMethod("happyAttempt", Attempt.class)),
                apiDocumentation(HappyAttemptWithFailureMappingTest.class, HappyPath.class.getMethod("happyAttempt", Attempt.class, Mapping.class)),
                apiDocumentation(HappyPathTest.class, HappyPath.class.getMethod("happyPath", Object.class)),
                apiDocumentation(SadPathTest.class, HappyPath.class.getMethod("sadPath", Object.class)),
                apiDocumentation(TechnicalFailureTest.class, HappyPath.class.getMethod("technicalFailure", Exception.class)),
                apiDocumentation(MapTest.class, HappyPath.class.getMethod("map", Mapping.class)),
                apiDocumentation(GetTechnicalFailureTest.class, BusinessFlow.class.getMethod("getTechnicalFailure")),
                apiDocumentation(IsTechnicalFailureTest.class, BusinessFlow.class.getMethod("isTechnicalFailure"))
        );
        for (ApiDocumentation apiDocumentation : apiDocumentations) {
            writeWikiPage(apiDocumentation);
        }
        writeIndexPage(apiDocumentations);
    }

    private String versionsIndexMarkup() throws IOException {
        return pageTitle("API Usage Examples") + "\n"
                + versionIndexPageLinks(versionsWithDocumentation());
    }

    private String versionIndexPageLinks(List<String> versionsWithDocumentation) {
        return versionsWithDocumentation.stream()
                .map(version -> hyperLink(version, version + "/index"))
                .collect(Collectors.joining("\n"));
    }

    private List<String> versionsWithDocumentation() throws IOException {
        return Files.list(docsDirectory())
                .map(Path::toFile)
                .filter(File::isDirectory)
                .map(File::getName)
                .filter(name -> !name.startsWith("_"))
                .collect(Collectors.toList());
    }

    private Path copyToReleaseDirectory(Path file, Path releaseDirectory) {
        try {
            Path target = releaseDirectory.resolve(file.subpath(wikiDirectory().getNameCount(), file.getNameCount()));
            Files.createDirectories(target.subpath(1, target.getNameCount() - 1));
            return Files.copy(file, target);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String latestReleasedVersion(String version) {
        Matcher matcher = SNAPSHOT_VERSION_PATTERN.matcher(version);
        if (matcher.matches()) {
            // assume that if e.g. we are 10.1.8-SNAPSHOT then the most recent release was 10.1.7
            // this works even for major bumps, e.g. 11.0.1-SNAPSHOT will mean 11.0.0 was just released
            int patchVersion = Integer.parseInt(matcher.group(1));
            String closestReleasePatch = String.valueOf(patchVersion - 1);
            return version.replace(patchVersion + "-SNAPSHOT", closestReleasePatch);
        } else {
            // no SNAPSHOT means we are in the middle of a release, so use that version
            return version;
        }
    }

    private void removeAllMarkdownFiles(Path wikiDirectory) throws IOException {
        Files.walk(wikiDirectory)
                .sorted(Comparator.reverseOrder())
                .filter(WikiGenerator::isMarkdownFile)
                .map(Path::toFile)
                .forEach(File::delete);
    }

    private static boolean isMarkdownFile(Path file) {
        return file.getFileName().toString().endsWith(MARKDOWN_FILE_EXTENSION);
    }

    private void writeIndexPage(List<ApiDocumentation> apiDocumentations) throws IOException {
        Path page = wikiDirectory().resolve(INDEX_PAGE);
        String markup = indexMarkup(apiDocumentations);
        writePage(page, markup);
    }

    private static String indexMarkup(List<ApiDocumentation> apiDocumentations) throws IOException {
        return pageTitle("API Usage Examples") + "\n"
                + "This is an index of usage examples of the API, with the aim of demonstrating what you can do as a learning aid.\n"
                + "All of these examples were generated from real tests, so you can be confident that the usage shown is up to date." + "\n\n"
                + apiLinksGroupedByApiClass(apiDocumentations);
    }

    private static String pageTitle(String title) {
        return "---\n" +
                "title: " + escapeHtml4(title) + "\n" +
                "layout: post\n" +
                "---";
    }

    private static String apiLinksGroupedByApiClass(List<ApiDocumentation> apiDocumentations) throws IOException {
        return apiDocumentations.stream()
                .sorted(comparing(apiDocumentation -> apiDocumentation.apiMethod.getDeclaringClass().getName()))
                .collect(groupingBy(apiDocumentation -> apiDocumentation.apiMethod.getDeclaringClass()))
                .entrySet()
                .stream()
                .map(entry -> apiClassMethodLinks(entry.getKey(), entry.getValue()))
                .collect(joining("\n\n"));
    }

    private static String apiClassMethodLinks(Class<?> declaringClass, List<ApiDocumentation> apiDocumentations) {
        return "## " + declaringClass.getSimpleName() + "\n" + apiDocumentations.stream()
                .map(apiDocumentation -> apiDocumentation.apiMethod)
                .sorted(comparing(Method::getName))
                .map(apiMethod -> "* " + hyperLink(apiMethod))
                .collect(joining("\n"));
    }

    private static String hyperLink(Method apiMethod) {
        String className = apiMethod.getDeclaringClass().getSimpleName();
        String pageLocation = className + "/" + pageName(apiMethod);
        return hyperLink(methodDisplayName(apiMethod), pageLocation);
    }

    private static String hyperLink(String displayName, String pageLocation) {
        return format("[%s](%s)", escapeHtml4(displayName), pageLocation);
    }

    private void writeWikiPage(ApiDocumentation apiDocumentation) throws IOException, ParseException {
        String pageDisplayName = methodDisplayName(apiDocumentation.apiMethod);
        String pageName = pageName(apiDocumentation.apiMethod);
        Path page = pageLocation(apiDocumentation.apiMethod).resolve(pageName + MARKDOWN_FILE_EXTENSION);
        String markup = pageTitle(pageDisplayName) + "\n"
                + "TODO: this is just a generated example\n\n" // TODO: remove once this is live properly
                + hyperLink("javadoc", javaDocLink(apiDocumentation.apiMethod)) + " "
                + hyperLink("usage tests", usageLink(apiDocumentation.apiTest)) + "\n\n"
                + "Added in version " + apiDocumentation.addedInVersion() + "\n\n"
                + apiMarkup(apiDocumentation.apiTest);
        writePage(page, markup);
    }

    private Path pageLocation(Method apiMethod) {
        String className = apiMethod.getDeclaringClass().getSimpleName();
        return wikiDirectory().resolve(className);
    }

    private String usageLink(Class<?> apiTestClass) {
        String packagePath = apiTestClass.getPackage().getName().replace('.', '/');
        // always master copy, since there is only ever one live copy of the wiki site
        return "https://github.com/theangrydev/business-flows/blob/" + blobBranch() + "/src/test/java/" + packagePath + "/" + apiTestClass.getSimpleName() + ".java";
    }

    private String blobBranch() {
        if (weAreReleasing()) {
            return "business-flows-" + version;
        } else {
            return "master";
        }
    }

    private static String pageName(Method apiMethod) {
        return methodDisplayName(apiMethod)
                .replaceAll("[<(>)]", "-")
                .replace(" ", "_");
    }

    private static String methodDisplayName(Method apiMethod) {
        String methodName = apiMethod.getName();
        String parameterTypes = stream(apiMethod.getGenericParameterTypes())
                .map(WikiGenerator::typeDisplayName)
                .collect(joining(", "));
        return typeDisplayName(apiMethod.getGenericReturnType()) + " " + methodName + "(" + parameterTypes +  ")";
    }

    private static String typeDisplayName(Type type) {
        return stripPackage(type.getTypeName());
    }

    private static String stripPackage(String name) {
        return name.replaceFirst("^.*\\.", "");
    }

    private static void writePage(Path page, String markup) throws IOException {
        Files.createDirectories(page.getParent());
        Files.write(page, markup.getBytes(UTF_8), CREATE, TRUNCATE_EXISTING);
    }

    private Path wikiDirectory() {
        if (weAreReleasing()) {
            return versionDocsDirectory();
        } else {
            return masterDocsDirectory();
        }
    }

    private Path versionDocsDirectory() {
        return docsDirectory().resolve(version);
    }

    private Path masterDocsDirectory() {
        return docsDirectory().resolve("master");
    }

    private Path docsDirectory() {
        return Paths.get("./docs");
    }

    private String apiMarkup(Class<?> apiTestClass) throws ParseException, IOException {
        TypeDeclaration typeDeclaration = JavaParser.parse(Paths.get("./src/test/java/" + pathToClass(apiTestClass) + ".java").toFile()).getTypes().get(0);
        String description = description(typeDeclaration.getJavaDoc());
        String examples = typeDeclaration.getMembers().stream()
                .filter(bodyDeclaration -> bodyDeclaration instanceof MethodDeclaration)
                .map(MethodDeclaration.class::cast)
                .filter(WikiGenerator::isExampleMethod)
                .map(WikiGenerator::renderExampleMarkup)
                .collect(joining("\n"));
        return description + "\n"
                + examples + "\n";
    }

    private String pathToClass(Class<?> testClass) {
        return testClass.getName().replace('.', '/');
    }

    private static String description(JavadocComment comment) {
        if (comment == null) {
            return "";
        }
        String[] lines = comment.toString().split("\n");
        return stream(lines).skip(1L).limit(lines.length - 2L).map(WikiGenerator::removeStars).collect(joining("\n", "", "\n"));
    }

    private static String removeStars(String line) {
        return line.trim().replaceFirst("^\\*", "").trim();
    }

    private static boolean isExampleMethod(MethodDeclaration methodDeclaration) {
        return !methodDeclaration.getAnnotations().isEmpty();
    }

    private static String renderExampleMarkup(MethodDeclaration methodDeclaration) {
        return "## " + camelCaseToSentence(methodDeclaration.getName()) + "\n"
                + "```java\n" + methodContents(methodDeclaration) + "\n```\n"
                + description(methodDeclaration.getJavaDoc());
    }

    private String javaDocLink(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        Package aPackage = declaringClass.getPackage();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String groupIdSlashes = groupId.replace('.', '/');
        String packageSlashes = aPackage.getName().replace('.', '/');
        String parameterSlashes = stream(parameterTypes).map(Class::getName).collect(joining("-", "", "-"));
        String closestReleasedVersion = latestReleasedVersion(version);
        return "https://oss.sonatype.org/service/local/repositories/releases/archive/"
                + groupIdSlashes + "/"
                + artifactId + "/"
                + closestReleasedVersion + "/"
                + artifactId + "-" + closestReleasedVersion + "-javadoc.jar/!/"
                + packageSlashes + "/"
                + declaringClass.getSimpleName() + ".html#"
                + method.getName() + "-" + parameterSlashes;
    }

    private static String methodContents(MethodDeclaration methodDeclaration) {
        DumpVisitor dumpVisitor = new DumpVisitor(true);
        dumpVisitor.visit(methodDeclaration.getBody(), null);
        return dumpVisitor.getSource().replace("( ", "("); //TODO: figure out what is causing this
    }

    private static String camelCaseToSentence(String camelCase) {
        return capitalize(uncapitalize(join(splitByCharacterTypeCamelCase(camelCase), ' ')));
    }
}
