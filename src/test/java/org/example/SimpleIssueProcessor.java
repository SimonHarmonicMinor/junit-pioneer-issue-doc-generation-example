package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import javassist.ClassClassPath;
import javassist.ClassPool;

import org.junitpioneer.jupiter.IssueProcessor;
import org.junitpioneer.jupiter.IssueTestSuite;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;

public class SimpleIssueProcessor implements IssueProcessor {
    @Override
    @SneakyThrows
    public void processTestResults(List<IssueTestSuite> issueTestSuites) {
        writeFileToBuildFolder(
            "test-issues-info.json",
            new ObjectMapper().writeValueAsString(
                issueTestSuites.stream()
                    .map(issueTestSuite -> Map.of(
                        "issueId", issueTestSuite.issueId(),
                        "tests", issueTestSuite.tests()
                                     .stream()
                                     .map(test -> parseTestId(test.testId()))
                                     .toList()
                    ))
                    .toList()
            )
        );
    }

    // [engine:junit-jupiter]/[class:org.example.TestExample]/[method:testSum()]
    @SneakyThrows
    private static Map<String, Object> parseTestId(String testId) {
        final var split = testId.split("/");

        // [class:org.example.TestExample] => org.example.TestExample
        final var className = split[1].substring(7, split[1].length() - 1);

        // [method:testSum()] => testSum
        final var method = split[2].substring(8, split[2].length() - 1).replaceAll("\\(.*\\)", "");

        // Load test class
        final Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);

        final var classPool = ClassPool.getDefault();
        classPool.appendClassPath(new ClassClassPath(clazz));

        final var methodLineNumber = classPool.get(className)
                                         .getDeclaredMethod(method)
                                         .getMethodInfo()
                                         .getLineNumber(0);
        return Map.of(
            // TestExample.testSum
            "testId", lastArrayElement(className.split("\\.")) + "." + method,
            // org/example/TestExample.java#L11
            "urlPath", className.replace(".", "/") + ".java#L" + methodLineNumber
        );
    }

    private static void writeFileToBuildFolder(String filename, String content) throws Exception {
        final var uri = SimpleIssueProcessor.class.getResource("/").toURI();
        final var pathString = Path.of(uri).toString();
        Files.writeString(
            Path.of(pathString, filename),
            content
        );
    }

    private static <T> T lastArrayElement(T[] arr) {
        return arr[arr.length - 1];
    }
}
