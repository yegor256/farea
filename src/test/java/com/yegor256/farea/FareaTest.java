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

import java.io.IOException;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link Farea}.
 *
 * @since 0.1.0
 */
final class FareaTest {

    @Test
    void compilesSimpleProject(@TempDir final Path dir) throws IOException {
        new Farea(dir).together(
            f -> {
                f.files()
                    .file("src/main/java/foo/Hello.java")
                    .write("package foo; import org.cactoos.Input; class Hello {}")
                    .show();
                f.dependencies()
                    .append("org.cactoos", "cactoos", "0.55.0");
                f.exec("compile");
                MatcherAssert.assertThat(
                    "Compiles simple project",
                    f.files().file("target/classes/foo/Hello.class").exists(),
                    Matchers.is(true)
                );
            }
        );
    }

    @Test
    void callsSimplePlugin(@TempDir final Path dir) throws IOException {
        new Farea(dir).together(
            f -> {
                f.build()
                    .plugins()
                    .append("maven-compiler-plugin", "3.11.0")
                    .configuration()
                    .set("skip", "true");
                f.withOpt("--debug").exec("compile");
                MatcherAssert.assertThat(
                    "Calls simple plugin",
                    f.log(),
                    Matchers.containsString("BUILD SUCCESS")
                );
            }
        );
    }

}
