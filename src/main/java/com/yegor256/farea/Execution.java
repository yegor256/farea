/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;

/**
 * Execution represents a Maven plugin execution in the project's POM file.
 *
 * <p>This interface allows you to configure Maven plugin executions, including
 * the phase, goals, and execution-specific configuration. An Execution object
 * corresponds to a single execution entry within a plugin's configuration.</p>
 *
 * <p>Through this interface, you can:</p>
 * <ul>
 *   <li>Set the Maven lifecycle phase for the execution</li>
 *   <li>Set the goals to execute</li>
 *   <li>Configure execution-specific parameters</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>
 * // Create a plugin execution
 * Execution execution = farea.build()
 *   .plugins()
 *   .append("org.apache.maven.plugins", "maven-failsafe-plugin", "3.0.0")
 *   .execution("integration-tests")
 *   .goals("integration-test", "verify")
 *   .phase("integration-test");
 * // Add execution-specific configuration
 * execution.configuration()
 *   .set("includes", new String[] {"&#42;&#42;/&#42;IT.java"})
 *   .set(
 *     "systemPropertyVariables",
 *     Collections.singletonMap("server.port", "8081")
 *   );
 * </pre>
 *
 * @see Plugin#execution()
 * @see Plugin#execution(String)
 * @see Configuration
 * @since 0.1.0
 */
public interface Execution {
    /**
     * Set phase.
     * @param value The Maven execution
     * @return Itself
     * @throws IOException If fails
     */
    Execution phase(String value) throws IOException;

    /**
     * Set goals.
     * @param values The Maven goals (non-empty list)
     * @return Itself
     * @throws IOException If fails
     */
    Execution goals(String... values) throws IOException;

    /**
     * Get config.
     * @return Config
     */
    Configuration configuration();
}
