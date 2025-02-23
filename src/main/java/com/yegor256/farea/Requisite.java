/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
    Requisite save(Path src) throws IOException;

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
