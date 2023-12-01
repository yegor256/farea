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

import com.yegor256.Farea;
import java.io.IOException;
import java.nio.file.Path;
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
        new Farea(dir).files().file("src/main/resources/hello.txt").write("Hello!");
        new Farea(dir)
            .build()
            .plugins()
            .appendItself()
            .phase("initialize")
            .goal("simple")
            .configuration()
            .set("message", "Hello, world!");
        new Farea(dir).exec("initialize");
        final String log = new Farea(dir).log();
        Assertions.assertTrue(log.contains("project.name: test"));
        Assertions.assertTrue(log.contains("total goals: 1"));
        Assertions.assertTrue(log.contains("Hello, world!"));
    }
}
