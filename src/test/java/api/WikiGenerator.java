/*
 * Copyright 2016 Liam Williams <liam.williams@zoho.com>.
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
package api;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import io.github.theangrydev.businessflows.Attempt;
import io.github.theangrydev.businessflows.HappyPath;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.text.WordUtils.uncapitalize;

//TODO: refactor before this gets out of hand
public class WikiGenerator {

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
        Model model = mavenXpp3Reader.read(new FileReader("pom.xml"));
        String closestReleasedVersion = closestReleaseVersion(model.getVersion());
        WikiGenerator wikiGenerator = new WikiGenerator(model.getGroupId(), model.getArtifactId(), closestReleasedVersion);
        wikiGenerator.generate();
    }

    public static String closestReleaseVersion(String version) {
        Pattern pattern = Pattern.compile(".*(\\d+)-SNAPSHOT");
        Matcher matcher = pattern.matcher(version);
        if (matcher.matches()) {
            int patchVersion = Integer.parseInt(matcher.group(1));
            String closestReleasePatch = String.valueOf(patchVersion - 1);
            return version.replace(patchVersion + "-SNAPSHOT", closestReleasePatch);
        } else {
            return version;
        }
    }

    public void generate() throws IOException, ParseException, NoSuchMethodException {
        createDirectories(wikiDirectory());
        removeAllMarkdownFiles();
        writeWikiPage(HappyAttemptApiTest.class, HappyPath.class.getMethod("happyAttempt", Attempt.class));
        writeIndexPage();
    }

    private static void removeAllMarkdownFiles() throws IOException {
        Files.walk(wikiDirectory())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .filter(file -> file.getName().endsWith(MARKDOWN_FILE_EXTENSION))
                .forEach(File::delete);
    }

    private static void writeIndexPage() throws IOException {
        Path page = wikiDirectory().resolve(INDEX_PAGE);
        String markup = indexMarkup();
        writePage(page, markup);
    }

    private static String indexMarkup() throws IOException {
        return pageTitle("API Usage Examples") + "\n"
                + "This is an index of usage examples of the API, with the aim of demonstrating what you can as a learning aid.\n"
                + "All of these examples were generated from real tests, so you can be confident that the usage shown is up to date." + "\n\n"
                + apiLinks();
    }

    private static String pageTitle(String title) {
        return "---\n" +
                "title: " + title + "\n" +
                "layout: post\n" +
                "---";
    }

    private static String apiLinks() throws IOException {
        return Files.list(wikiDirectory())
                .map(Path::toFile)
                .map(File::getName)
                .filter(name -> name.endsWith(MARKDOWN_FILE_EXTENSION))
                .filter(name -> !name.equals(INDEX_PAGE))
                .map(name -> name.replace(MARKDOWN_FILE_EXTENSION, ""))
                .map(name -> format("[%s](%s)", name, name))
                .collect(joining("\n"));
    }

    private void writeWikiPage(Class<?> apiTestClass, Method apiMethod) throws IOException, ParseException {
        String pageDisplayName = pageDisplayName(apiMethod);
        String pageName = pageName(apiMethod);
        Path page = wikiDirectory().resolve(pageName + MARKDOWN_FILE_EXTENSION);
        String markup = pageTitle(pageDisplayName) + "\n"
                + "[" + apiMethod.getName() + " javadoc](" + javaDocLink(apiMethod) + ")" + "\n\n"
                + apiMarkup(apiTestClass, apiMethod) + "\n";
        writePage(page, markup);
    }

    private static String pageName(Method apiMethod) {
        String methodName = apiMethod.getName();
        String className = apiMethod.getDeclaringClass().getSimpleName();
        return className + "." + methodName;
    }

    private static String pageDisplayName(Method apiMethod) {
        String methodName = apiMethod.getName();
        String className = apiMethod.getDeclaringClass().getSimpleName();
        String parameterTypes = stream(apiMethod.getGenericParameterTypes()).map(String::valueOf).collect(joining(", "));
        return className + "." + methodName + "(" + parameterTypes +  ")";
    }

    private static void writePage(Path page, String markup) throws IOException {
        Files.write(page, markup.getBytes(UTF_8), CREATE, TRUNCATE_EXISTING);
    }

    private static Path wikiDirectory() {
        return Paths.get("./docs");
    }

    private String apiMarkup(Class<?> apiTestClass, Method apiMethod) throws ParseException, IOException {
        String apiTestName = apiTestClass.getSimpleName();
        TypeDeclaration typeDeclaration = JavaParser.parse(Paths.get("./src/test/java/api/" + apiTestName + ".java").toFile()).getTypes().get(0);
        String description = description(typeDeclaration.getJavaDoc());
        String examples = typeDeclaration.getMembers().stream()
                .filter(bodyDeclaration -> bodyDeclaration instanceof MethodDeclaration)
                .map(MethodDeclaration.class::cast)
                .filter(WikiGenerator::isExampleMethod)
                .map(WikiGenerator::renderExampleMarkup)
                .collect(joining("\n"));
        return description + "\n"
                + examples + "\n"
                + "[test](" + javaDocLink(apiMethod) + ")";
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
        // TODO: parameterize (get from pom.xml)
        String groupIdSlashes = groupId.replace('.', '/');
        String packageSlashes = aPackage.getName().replace('.', '/');
        String parameterSlashes = stream(parameterTypes).map(Class::getName).collect(joining("-", "", "-"));
        return "https://oss.sonatype.org/service/local/repositories/releases/archive/"
                + groupIdSlashes + "/"
                + artifactId + "/"
                + version + "/"
                + artifactId + "-" + version + "-javadoc.jar/!/"
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
