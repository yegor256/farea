/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

/**
 * Plugin represents a Maven plugin in the project's POM file.
 * 
 * <p>This interface provides access to configure a Maven plugin, including
 * adding executions and setting plugin-level configuration. A Plugin object 
 * represents a single plugin entry in the POM's build/plugins section.</p>
 * 
 * <p>Through this interface, you can:</p>
 * <ul>
 *   <li>Add or access plugin executions</li>
 *   <li>Configure plugin parameters</li>
 * </ul>
 * 
 * <p>Usage example:</p>
 * <pre>
 * // Configure a plugin with an execution
 * Plugin plugin = farea.build()
 *     .plugins()
 *     .append("org.apache.maven.plugins", "maven-surefire-plugin", "3.0.0");
 *     
 * // Add an execution
 * plugin.execution("unit-tests")
 *     .goals("test")
 *     .phase("test")
 *     .configuration()
 *     .set("skipTests", "false");
 *     
 * // Set plugin-level configuration
 * plugin.configuration()
 *     .set("argLine", "-Xmx1024m");
 * </pre>
 *
 * @since 0.0.1
 * @see Plugins
 * @see Execution
 * @see Configuration
 */
public interface Plugin {
    /**
     * Append new execution or get access to existing one (using default name).
     * @return Execution found or added
     */
    Execution execution();

    /**
     * Append new execution or get access to existing one.
     * @param name The "id" of it
     * @return Execution found or added
     */
    Execution execution(String name);

    /**
     * Get config.
     * @return Config
     */
    Configuration configuration();
}
