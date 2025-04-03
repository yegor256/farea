/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;

/**
 * Requisites provides access to files and directories in the Maven project workspace.
 * 
 * <p>This interface acts as a factory for {@link Requisite} objects, allowing you
 * to access and manipulate files within the project structure. It also provides
 * utility methods to view all files in the workspace and access build logs.</p>
 * 
 * <p>Usage example:</p>
 * <pre>
 * // Create a Java file
 * farea.files().file("src/main/java/Hello.java")
 *     .write("public class Hello { }".getBytes());
 *     
 * // Access build log after execution
 * String log = farea.files().log().content();
 * 
 * // Show all files in the project directory
 * farea.files().show();
 * </pre>
 *
 * @since 0.2.0
 * @see Requisite
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
