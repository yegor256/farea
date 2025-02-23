/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Plugins.
 *
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
