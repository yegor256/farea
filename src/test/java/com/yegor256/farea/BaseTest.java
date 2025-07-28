/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import com.jcabi.xml.XML;
import java.io.IOException;
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
    void readsGroupIdArtifactIdAndVersion() throws IOException {
        final String pom = String.join(
            "\n",
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\">",
            "  <modelVersion>4.0.0</modelVersion>",
            "  <groupId>com.example</groupId>",
            "  <artifactId>demo</artifactId>",
            "  <version>1.2.3</version>",
            "</project>"
        );
        final Path temp = Files.createTempFile("pom", ".xml");
        Files.write(temp, pom.getBytes("UTF-8"));
        final Base base = new Base(temp);
        MatcherAssert.assertThat(
            "groupId should be 'com.example'",
            base.groupId(),
            Matchers.equalTo("com.example")
        );
        MatcherAssert.assertThat(
            "artifactId should be 'demo'",
            base.artifactId(),
            Matchers.equalTo("demo")
        );
        MatcherAssert.assertThat(
            "version should be '1.2.3'",
            base.version(),
            Matchers.equalTo("1.2.3")
        );
    }

    @Test
    void returnsXml() throws IOException {
        final String pom = String.join(
            "\n",
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\">",
            "  <modelVersion>4.0.0</modelVersion>",
            "  <groupId>g</groupId>",
            "  <artifactId>a</artifactId>",
            "  <version>v</version>",
            "</project>"
        );
        final Path temp = Files.createTempFile("pom", ".xml");
        Files.write(temp, pom.getBytes("UTF-8"));
        final Base base = new Base(temp);
        final XML xml = base.xml();
        MatcherAssert.assertThat(
            "ArtifactId should be 'a'",
            xml.xpath("/mvn:project/mvn:artifactId/text()").get(0),
            Matchers.equalTo("a")
        );
    }
}
