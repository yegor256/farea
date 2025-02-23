/*
 * The MIT License (MIT)
 *
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
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link DtPlugins}.
 *
 * @since 0.1.0
 */
@ExtendWith(MktmpResolver.class)
final class DtPluginsTest {

    @Test
    void appendsOnce(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom.xml");
        final Pom pom = new Pom(xml).init();
        new DtPlugins(dir, pom).append("g1", "a1", "0.0.0");
        MatcherAssert.assertThat(
            "Appends one plugin",
            pom.xpath(
                "/project/build/plugins/plugin[groupId='g1' and artifactId='a1']/groupId/text()"
            ).size(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void avoidsDuplicates(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom.xml");
        final Pom pom = new Pom(xml).init();
        new DtPlugins(dir, pom).append("g", "a", "0.0.1");
        new DtPlugins(dir, pom).append("g", "a", "0.0.1");
        MatcherAssert.assertThat(
            "No duplicates",
            pom.xpath("/project/build/plugins/plugin/groupId/text()").size(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void addsTwoPluginsWithExecutions(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom.xml");
        final Pom pom = new Pom(xml).init();
        new DtPlugins(dir, pom)
            .append("g", "a1", "0.0.1")
            .execution("first")
            .phase("f");
        new DtPlugins(dir, pom).append("g", "a2", "0.0.2")
            .execution("second")
            .phase("s");
        MatcherAssert.assertThat(
            "Two plugins with executions",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPaths(
                "/project/build/plugins/plugin[artifactId='a1']/executions/execution[id='first']",
                "/project/build/plugins/plugin[artifactId='a2']/executions/execution[id='second']"
            )
        );
    }
}
