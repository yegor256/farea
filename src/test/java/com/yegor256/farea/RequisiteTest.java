/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Yegor Bugayenko
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
import java.nio.file.Files;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link Requisite}.
 *
 * @since 0.1.0
 */
final class RequisiteTest {

    @Test
    void deletesOneFile(final @TempDir Path dir) throws IOException {
        final String name = "foo.txt";
        Files.write(dir.resolve(name), "".getBytes());
        new Requisite(dir, name).delete();
        MatcherAssert.assertThat(
            dir.resolve(name).toFile().exists(),
            Matchers.is(false)
        );
    }

    @Test
    void deletesOneDirectory(final @TempDir Path dir) throws IOException {
        final String name = "foo/bar/zzz";
        dir.resolve(name).toFile().mkdirs();
        Files.write(dir.resolve(name).resolve("file.txt"), "".getBytes());
        new Requisite(dir, name).delete();
        MatcherAssert.assertThat(
            dir.resolve(name).toFile().exists(),
            Matchers.is(false)
        );
    }

}
