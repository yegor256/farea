/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;

/**
 * Requisites.
 *
 * @since 0.2.0
 */
public interface Requisites {
    /**
     * Show them all, as a tree.
     * @throws IOException If fails
     * @since 0.7.0
     */
    void show() throws IOException;

    /**
     * Access to a log of the Maven build.
     * @return Log output
     */
    Requisite log();

    /**
     * Access to a single file.
     * @param name File name
     * @return File in home
     */
    Requisite file(String name);
}
