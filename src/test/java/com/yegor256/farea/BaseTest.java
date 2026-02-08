/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Base}.
 *
 * @since 0.1.0
 */
final class BaseTest {

    @Test
    void readsGroupId() throws IOException {
        final Path temp = Files.createTempFile("pom", ".xml");
        Files.write(
            temp,
            String.join(
                "\n",
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\">",
                "  <modelVersion>4.0.0</modelVersion>",
                "  <groupId>com.example</groupId>",
                "  <artifactId>demo</artifactId>",
                "  <version>1.2.3</version>",
                "</project>"
            ).getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "groupId should be 'com.example'",
            new Base(temp).groupId(),
            Matchers.equalTo("com.example")
        );
    }

    @Test
    void readsArtifactId() throws IOException {
        final Path temp = Files.createTempFile("pom", ".xml");
        Files.write(
            temp,
            String.join(
                "\n",
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\">",
                "  <modelVersion>4.0.0</modelVersion>",
                "  <groupId>com.example</groupId>",
                "  <artifactId>demo</artifactId>",
                "  <version>1.2.3</version>",
                "</project>"
            ).getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "artifactId should be 'demo'",
            new Base(temp).artifactId(),
            Matchers.equalTo("demo")
        );
    }

    @Test
    void readsVersion() throws IOException {
        final Path temp = Files.createTempFile("pom", ".xml");
        Files.write(
            temp,
            String.join(
                "\n",
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\">",
                "  <modelVersion>4.0.0</modelVersion>",
                "  <groupId>com.example</groupId>",
                "  <artifactId>demo</artifactId>",
                "  <version>1.2.3</version>",
                "</project>"
            ).getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "version should be '1.2.3'",
            new Base(temp).version(),
            Matchers.equalTo("1.2.3")
        );
    }

    @Test
    void returnsXml() throws IOException {
        final Path temp = Files.createTempFile("pom", ".xml");
        Files.write(
            temp,
            String.join(
                "\n",
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\">",
                "  <modelVersion>4.0.0</modelVersion>",
                "  <groupId>g</groupId>",
                "  <artifactId>a</artifactId>",
                "  <version>v</version>",
                "</project>"
            ).getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "ArtifactId should be 'a'",
            new Base(temp).xml().xpath("/mvn:project/mvn:artifactId/text()").get(0),
            Matchers.equalTo("a")
        );
    }
}
