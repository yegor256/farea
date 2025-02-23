/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
                f.files().file("src/main/resources/hello.txt").write("Hello!".getBytes());
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
                final String log = f.files().log().content();
                Assertions.assertTrue(log.contains("project.name: test"));
                Assertions.assertTrue(log.contains("total goals: 1"));
                Assertions.assertTrue(log.contains("message: Hello, друг!"));
            }
        );
    }
}
