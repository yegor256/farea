/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import com.jcabi.matchers.XhtmlMatchers;
import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import java.io.IOException;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link DtDependency}.
 *
 * @since 0.1.0
 */
@ExtendWith(MktmpResolver.class)
final class DtDependencyTest {

    @Test
    void setsDependencyScope(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-1.xml");
        final Pom pom = new Pom(xml);
        new DtDependencies(dir, pom).append("g", "a", "1.0-SNAPSHOT").scope("test");
        MatcherAssert.assertThat(
            "Sets dependency scope",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPaths(
                "//dependency[groupId='g']",
                "//dependency[artifactId='a']",
                "//dependency[version='1.0-SNAPSHOT']",
                "//dependency[scope='test']"
            )
        );
    }

    @Test
    void setsDependencyClassifier(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-2.xml");
        final Pom pom = new Pom(xml);
        new DtDependencies(dir, pom)
            .append("foo", "bar", "0.0.1")
            .scope("provided")
            .classifier("jar");
        MatcherAssert.assertThat(
            "Sets dependency classifier",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPaths(
                "//dependency[groupId='foo']",
                "//dependency[artifactId='bar']",
                "//dependency[version='0.0.1']",
                "//dependency[scope='provided']",
                "//dependency[classifier='jar']"
            )
        );
    }

}
