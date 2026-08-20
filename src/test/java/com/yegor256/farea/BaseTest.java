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
 * @since 0.1.0
 */
final class BaseTest {

    @Test
    void readsGroupId() throws IOException {
        MatcherAssert.assertThat(
            "groupId should be 'com.example'",
            new Base(BaseTest.pom("com.example", "demo", "1.2.3")).groupId(),
            Matchers.equalTo("com.example")
        );
    }

    @Test
    void readsArtifactId() throws IOException {
        MatcherAssert.assertThat(
            "artifactId should be 'demo'",
            new Base(BaseTest.pom("com.example", "demo", "1.2.3")).artifactId(),
            Matchers.equalTo("demo")
        );
    }

    @Test
    void readsVersion() throws IOException {
        MatcherAssert.assertThat(
            "version should be '1.2.3'",
            new Base(BaseTest.pom("com.example", "demo", "1.2.3")).version(),
            Matchers.equalTo("1.2.3")
        );
    }

    @Test
    void returnsXml() throws IOException {
        MatcherAssert.assertThat(
            "ArtifactId should be 'a'",
            new Base(BaseTest.pom("g", "a", "v")).xml()
                .xpath("/mvn:project/mvn:artifactId/text()").get(0),
            Matchers.equalTo("a")
        );
    }

    private static Path pom(final String group, final String artifact,
        final String version) throws IOException {
        final Path temp = Files.createTempFile("pom", ".xml");
        Files.write(
            temp,
            String.join(
                System.lineSeparator(),
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\">",
                "  <modelVersion>4.0.0</modelVersion>",
                String.format("  <groupId>%s</groupId>", group),
                String.format("  <artifactId>%s</artifactId>", artifact),
                String.format("  <version>%s</version>", version),
                "</project>"
            ).getBytes(StandardCharsets.UTF_8)
        );
        return temp;
    }
}
