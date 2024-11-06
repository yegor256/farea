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

/**
 * Requisite.
 *
 * @since 0.2.0
 */
public interface Requisite {
    /**
     * Write to file.
     * @param content The content to write
     * @return Itself
     * @throws IOException If fails
     * @since 0.2.0
     */
    Requisite write(byte[] content) throws IOException;

    /**
     * Write to file.
     * @param content The content to write
     * @return Itself
     * @throws IOException If fails
     * @deprecated This method will be deleted in future versions, better use
     *  the one that accepts byte array
     */
    @Deprecated
    Requisite write(String content) throws IOException;

    /**
     * Create requisite by copying it from an existing file (or directory).
     * @param src The file/directory to copy from
     * @return Itself
     * @throws IOException If fails
     */
    Requisite copy(Path src) throws IOException;

    /**
     * Read content.
     * @return The content of the file
     * @throws IOException If fails
     */
    String content() throws IOException;

    /**
     * Show it in the log.
     * @throws IOException If fails
     */
    void show() throws IOException;

    /**
     * Deletes it (recursively, if it is a directory).
     * @throws IOException If fails
     */
    void delete() throws IOException;

    /**
     * Check existence.
     * @return TRUE if file exists
     */
    boolean exists();

    /**
     * Get its path.
     * @return Absolute path of it
     * @since 0.5.0
     */
    Path path();
}
