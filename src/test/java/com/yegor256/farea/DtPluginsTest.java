/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2024 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
        new DtPlugins(pom).append("g1", "a1", "0.0.0");
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
        new DtPlugins(pom).append("g", "a", "0.0.1");
        new DtPlugins(pom).append("g", "a", "0.0.1");
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
        new DtPlugins(pom)
            .append("g", "a1", "0.0.1")
            .execution("first")
            .phase("f");
        new DtPlugins(pom).append("g", "a2", "0.0.2")
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
