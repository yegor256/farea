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
