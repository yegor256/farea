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

import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * File in Maven Reactor.
 *
 * @since 0.0.1
 */
public final class Requisite {

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
    Requisite(final Path dir, final String file) {
        this.home = dir;
        this.name = file;
    }

    /**
     * Write to file.
     * @param content The content to write
     * @return Itself
     * @throws IOException If fails
     */
    public Requisite write(final String content) throws IOException {
        final File parent = this.path().toFile().getParentFile();
        if (parent.mkdirs()) {
            Logger.debug(this, "Directory created at %s", parent);
        }
        final boolean existed = this.path().toFile().exists();
        Files.write(
            this.path(),
            content.getBytes(StandardCharsets.UTF_8)
        );
        if (existed) {
            Logger.debug(
                this, "File replaced at %s (%d bytes)",
                this.path(), this.path().toFile().length()
            );
        } else {
            Logger.debug(
                this, "File created at %s (%d bytes)",
                this.path(), this.path().toFile().length()
            );
        }
        return this;
    }

    /**
     * Read content.
     * @return The content of the file
     * @throws IOException If fails
     */
    public String content() throws IOException {
        return new String(
            Files.readAllBytes(this.path()),
            StandardCharsets.UTF_8
        );
    }

    /**
     * Show it in the log.
     * @throws IOException If fails
     */
    public void show() throws IOException {
        Logger.debug(
            this, "The content of %s:%n  %s",
            this.name,
            this.content().replace(
                System.lineSeparator(),
                String.format("%s  ", System.lineSeparator())
            )
        );
    }

    /**
     * Check existence.
     * @return TRUE if file exists
     */
    public boolean exists() {
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
