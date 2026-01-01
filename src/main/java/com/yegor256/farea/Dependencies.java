/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Dependencies provides methods to add and manage Maven dependencies in the project.
 *
 * <p>This interface allows you to add Maven dependencies to the POM file, enabling
 * you to test how your project interacts with various libraries. It includes
 * special functionality to add the current project as a dependency, which is
 * particularly useful when testing libraries.</p>
 *
 * <p>Usage examples:</p>
 *
 * <p>1. Adding a standard dependency:</p>
 * <pre>
 * Dependency junit = farea.dependencies()
 *   .append("org.junit.jupiter", "junit-jupiter-api", "5.7.0")
 *   .scope("test");
 * </pre>
 *
 * <p>2. Adding the current project as a dependency (useful for testing libraries):</p>
 * <pre>
 * Dependency self = farea.dependencies()
 *   .appendItself()
 *   .scope("compile");
 * </pre>
 *
 * <p>3. Adding a dependency with classifier:</p>
 * <pre>
 * farea.dependencies()
 *   .append("org.hibernate", "hibernate-core", "5.4.30.Final")
 *   .classifier("sources");
 * </pre>
 *
 * @see Dependency
 * @see Farea#dependencies()
 * @since 0.1.0
 */
public interface Dependencies {
    /**
     * Append a dependency.
     * @param group The group ID
     * @param artifact The artifact ID
     * @param version The version
     * @return Deps
     * @throws IOException If fails
     */
    Dependency append(String group, String artifact,
        String version) throws IOException;

    /**
     * Add itself (the code in this classpath) to the Maven reactor.
     * @return Itself as a dependency
     * @throws IOException If fails
     */
    Dependency appendItself() throws IOException;

    /**
     * Add itself (the code in this classpath) to the Maven reactor.
     * @param local Path of local Maven repo, usually "~/.m2/repository"
     * @return Itself as a plugin
     * @throws IOException If fails
     */
    Dependency appendItself(Path local) throws IOException;
}
