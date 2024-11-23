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

import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link DtRequisite}.
 *
 * @since 0.1.0
 */
@ExtendWith(MktmpResolver.class)
final class DtRequisiteTest {

    @Test
    void copiesDirectory(@Mktmp final Path src, @Mktmp final Path farea) throws IOException {
        Files.write(src.resolve("one.txt"), "".getBytes());
        src.resolve("a/b/c/d").toFile().mkdirs();
        Files.write(src.resolve("a/b/c/d/two.txt"), "".getBytes());
        new DtRequisite(farea, "new").save(src);
        MatcherAssert.assertThat(
            "copied two files",
            farea.resolve("new/a/b/c/d/two.txt").toFile().exists(),
            Matchers.is(true)
        );
    }

    @Test
    void copiesDirectoryIntoFile(@Mktmp final Path src,
        @Mktmp final Path farea) throws IOException {
        src.resolve("d").toFile().mkdirs();
        Files.write(src.resolve("d/foo.txt"), "".getBytes());
        new DtRequisite(farea, "boo.txt").write("hello".getBytes());
        Assertions.assertThrows(
            IOException.class,
            () -> new DtRequisite(farea, "boo.txt").save(src),
            "should not allow copying directory into file"
        );
    }

    @Test
    void copiesDirectoryIntoExistingDirectory(@Mktmp final Path src,
        @Mktmp final Path farea) throws IOException {
        src.resolve("a/b").toFile().mkdirs();
        Files.write(src.resolve("a/b/foo.txt"), "".getBytes());
        new DtRequisite(farea, "one/two").save(src);
        src.resolve("a/c").toFile().mkdirs();
        Files.write(src.resolve("a/c/bar.txt"), "".getBytes());
        new DtRequisite(farea, "one/two").save(src);
        MatcherAssert.assertThat(
            "copied second file too",
            farea.resolve("one/two/a/c/bar.txt").toFile().exists(),
            Matchers.is(true)
        );
    }

    @Test
    void copiesFile(@Mktmp final Path src, @Mktmp final Path farea) throws IOException {
        final Path path = src.resolve("one.txt");
        Files.write(path, "".getBytes());
        new DtRequisite(farea, "a/b/c/new.txt").save(path).show();
        MatcherAssert.assertThat(
            "copied one file only",
            farea.resolve("a/b/c/new.txt").toFile().exists(),
            Matchers.is(true)
        );
    }

    @Test
    void deletesOneFile(@Mktmp final Path dir) throws IOException {
        final String name = "foo.txt";
        Files.write(dir.resolve(name), "".getBytes());
        new DtRequisite(dir, name).delete();
        MatcherAssert.assertThat(
            "Deletes one file",
            dir.resolve(name).toFile().exists(),
            Matchers.is(false)
        );
    }

    @Test
    void deletesOneDirectory(@Mktmp final Path dir) throws IOException {
        final String name = "foo/bar/zzz";
        dir.resolve(name).toFile().mkdirs();
        Files.write(dir.resolve(name).resolve("file.txt"), "".getBytes());
        new DtRequisite(dir, name).delete();
        MatcherAssert.assertThat(
            "Deletes one directory",
            dir.resolve(name).toFile().exists(),
            Matchers.is(false)
        );
    }

}
