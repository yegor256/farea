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
package simple;

import com.yegor256.farea.Farea;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link SimpleMojo}.
 *
 * @since 0.1.0
 */
final class SimpleMojoTest {

    @Test
    void callsCustomPlugin(final @TempDir Path dir) throws IOException {
        final Path local = Paths.get(System.getProperty("maven.repo.local"));
        new Farea(dir).together(
            f -> {
                f.files().file("src/main/resources/hello.txt").write("Hello!");
                f.properties()
                    .set("project.build.sourceEncoding", "UTF-8")
                    .set("project.reporting.outputEncoding", "UTF-8");
                f.build()
                    .plugins()
                    .appendItself(local)
                    .execution("simple")
                    .phase("initialize")
                    .goals("simple")
                    .configuration()
                    .set("message", "Hello, друг!");
                f.exec(
                    String.format("-Dmaven.repo.local=%s", local),
                    "initialize"
                );
                final String log = f.log();
                Assertions.assertTrue(log.contains("project.name: test"));
                Assertions.assertTrue(log.contains("total goals: 1"));
                Assertions.assertTrue(log.contains("message: Hello, друг!"));
            }
        );
    }
}
