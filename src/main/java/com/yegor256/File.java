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
package com.yegor256;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * File in Maven Reactor.
 *
 * @since 0.0.1
 */
final class File {

    /**
     * Home.
     */
    private final Path home;

    /**
     * Name.
     */
    private final String name;

    /**
     * Ctor.
     * @param dir The home dir
     * @param file The name of it
     */
    File(final Path dir, final String file) {
        this.home = dir;
        this.name = file;
    }

    /**
     * Write to file.
     * @param content The content to write
     * @throws IOException If fails
     */
    void write(final String content) throws IOException {
        this.path().toFile().getParentFile().mkdirs();
        java.nio.file.Files.write(
            this.path(), content.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Read content.
     * @return The content of the file
     * @throws IOException If fails
     */
    String content() throws IOException {
        return new String(
            java.nio.file.Files.readAllBytes(this.path()),
            StandardCharsets.UTF_8
        );
    }

    /**
     * Check existence.
     * @return TRUE if file exists
     */
    boolean exists() {
        return this.path().toFile().exists();
    }

    /**
     * Absolute path of it.
     * @return The path
     */
    private Path path() {
        return this.home.resolve(this.name);
    }

}
