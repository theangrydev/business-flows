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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;
import static org.apache.commons.lang3.text.WordUtils.uncapitalize;

public class WikiGenerator {

    private static final String INDEX_PAGE = "index.md";

    private static final String INDEX_PAGE_HEADER = pageTitle("API Documentation");

    public static void main(String[] args) throws URISyntaxException, IOException, ParseException {
        createDirectories(wikiDirectory());
        writeWikiPage("HappyPath.happyAttempt", HappyAttemptApiTest.class);
        writeIndexPage();
    }

    private static void writeIndexPage() throws IOException {
        Path page = wikiDirectory().resolve(INDEX_PAGE);
        String markup = indexMarkup();
        writePage(page, markup);
    }

    private static String indexMarkup() throws IOException {
        return INDEX_PAGE_HEADER + "\n" + apiLinks();
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
                .filter(name -> !name.equals(INDEX_PAGE))
                .map(name -> name.replace(".md", ""))
                .map(name -> format("[%s](%s)", name, name))
                .collect(joining("\n"));
    }

    private static void writeWikiPage(String pageName, Class<?> apiTestClass) throws IOException, ParseException {
        Path page = wikiDirectory().resolve(pageName + ".md");
        String markup = pageTitle(pageName) + "\n" + apiMarkup(apiTestClass);
        writePage(page, markup);
    }

    private static void writePage(Path page, String markup) throws IOException {
        Files.write(page, markup.getBytes(UTF_8), CREATE, TRUNCATE_EXISTING);
    }

    private static Path wikiDirectory() {
        return Paths.get("./docs");
    }

    private static String apiMarkup(Class<?> apiTestClass) throws ParseException, IOException {
        String apiTestName = apiTestClass.getSimpleName();
        TypeDeclaration typeDeclaration = JavaParser.parse(Paths.get("./src/test/java/api/" + apiTestName + ".java").toFile()).getTypes().get(0);
        String description = description(typeDeclaration.getJavaDoc());
        String examples = typeDeclaration.getMembers().stream()
                .filter(bodyDeclaration -> bodyDeclaration instanceof MethodDeclaration)
                .map(MethodDeclaration.class::cast)
                .filter(WikiGenerator::isExampleMethod)
                .map(WikiGenerator::renderExampleMarkup)
                .collect(joining("\n"));
        return description + "\n" + examples;
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
                + "```java" + methodContents(methodDeclaration) + "\n```\n" +
                description(methodDeclaration.getJavaDoc());
    }

    private static String methodContents(MethodDeclaration methodDeclaration) {
        String body = methodDeclaration.getBody().toString()
                .replaceFirst("\\{", "")
                .replaceAll("}$", "");
        return stream(body.split("\n"))
                .map(String::trim)
                .collect(joining("\n"));
    }

    private static String camelCaseToSentence(String camelCase) {
        return capitalize(uncapitalize(join(splitByCharacterTypeCamelCase(camelCase), ' ')));
    }
}
