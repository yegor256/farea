/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Plugins provides methods to add and manage Maven plugins in the project.
 *
 * <p>This interface acts as a factory for {@link Plugin} objects, allowing you
 * to add Maven plugins to the build section of the POM file. It includes
 * special functionality to add the current project as a plugin, which is
 * particularly useful when testing Maven plugins.</p>
 *
 * <p>Usage examples:</p>
 *
 * <p>1. Adding a standard Maven plugin:</p>
 * <pre>
 * Plugin plugin = farea.build()
 *   .plugins()
 *   .append("org.apache.maven.plugins", "maven-clean-plugin", "3.1.0");
 * </pre>
 *
 * <p>2. Adding an Apache Maven plugin (using the default group ID):</p>
 * <pre>
 * Plugin compiler = farea.build()
 *   .plugins()
 *   .append("maven-compiler-plugin", "3.8.1");
 * </pre>
 *
 * <p>3. Testing your own Maven plugin (the one in the current classpath):</p>
 * <pre>
 * Plugin myPlugin = farea.build()
 *   .plugins()
 *   .appendItself()
 *   .execution()
 *   .goals("my-goal")
 *   .phase("verify");
 * </pre>
 *
 * @see Plugin
 * @see Build#plugins()
 * @since 0.1.0
 */
public interface Plugins {
    /**
     * Append Apache Maven plugin.
     * @param artifact The artifact ID
     * @param version The version
     * @return Plugin just added
     * @throws IOException If fails
     */
    Plugin append(String artifact,
        String version) throws IOException;

    /**
     * Append custom plugin.
     * @param group The group ID
     * @param artifact The artifact ID
     * @param version The version
     * @return Plugin just added
     * @throws IOException If fails
     */
    Plugin append(String group, String artifact,
        String version) throws IOException;

    /**
     * Add itself (the code in this classpath) to the Maven reactor.
     * @return Itself as a plugin
     * @throws IOException If fails
     */
    Plugin appendItself() throws IOException;

    /**
     * Add itself (the code in this classpath) to the Maven reactor.
     * @param local Path of local Maven repo, usually "~/.m2/repository"
     * @return Itself as a plugin
     * @throws IOException If fails
     */
    Plugin appendItself(Path local) throws IOException;
}
