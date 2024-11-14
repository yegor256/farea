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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link Itself}.
 *
 * @since 0.1.0
 */
@ExtendWith(MktmpResolver.class)
final class ItselfTest {

    @Test
    void deploysJar(@Mktmp final Path dir, @Mktmp final Path repos) throws IOException {
        final String version = new Itself(
            dir,
            new Base(Paths.get("src/test/resources/fake-pom.xml")),
            false
        ).deploy(repos);
        MatcherAssert.assertThat(
            "Resolves",
            repos.resolve(
                String.format("g1/g2/a/%s/a-%1$s.jar", version)
            ).toFile().exists(),
            Matchers.is(true)
        );
    }

    @Test
    void deploysJarAndPom(@Mktmp final Path dir, @Mktmp final Path repos) throws IOException {
        final String version = new Itself(
            dir,
            new Base(Paths.get("src/test/resources/fake-pom.xml")),
            false
        ).deploy(repos);
        MatcherAssert.assertThat(
            "Deploys JAR and POM",
            XhtmlMatchers.xhtml(
                new String(
                    Files.readAllBytes(
                        repos.resolve(String.format("g1/g2/a/%s/a-%1$s.pom", version))
                    ),
                    StandardCharsets.UTF_8
                )
            ),
            XhtmlMatchers.hasXPath(
                "/ns1:project/ns1:groupId[.='g1.g2']",
                "http://maven.apache.org/POM/4.0.0"
            )
        );
    }

    @Test
    void deploysInManyThreads(@Mktmp final Path dir, @Mktmp final Path repos) {
        final Itself itself = new Itself(
            dir,
            new Base(Paths.get("src/test/resources/fake-pom.xml")),
            false
        );
        MatcherAssert.assertThat(
            "the JAR deploys in many threads",
            new Jointly<>(
                thread -> {
                    itself.deploy(repos);
                    return itself;
                }
            ).made(),
            Matchers.notNullValue()
        );
    }
}
