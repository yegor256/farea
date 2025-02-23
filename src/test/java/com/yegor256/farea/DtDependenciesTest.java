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
 * Test case for {@link DtDependencies}.
 *
 * @since 0.1.0
 */
@ExtendWith(MktmpResolver.class)
final class DtDependenciesTest {

    @Test
    void appendsItself(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-1.xml");
        final Pom pom = new Pom(xml);
        new DtDependencies(dir, pom).appendItself(dir);
        MatcherAssert.assertThat(
            "Appends itself",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPaths(
                "//dependencies/dependency[groupId='com.yegor256']",
                "//dependencies/dependency[artifactId='farea']"
            )
        );
    }

    @Test
    void appendsItselfInManyThreads(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-2.xml");
        final Pom pom = new Pom(xml);
        MatcherAssert.assertThat(
            "the pom.xml has all modifications",
            XhtmlMatchers.xhtml(
                new Jointly<>(
                    thread -> {
                        new DtDependencies(dir, pom).appendItself(dir);
                        return pom;
                    }
                ).made(10).xml()
            ),
            XhtmlMatchers.hasXPaths("/project/dependencies[count(dependency)=10]")
        );
    }
}
