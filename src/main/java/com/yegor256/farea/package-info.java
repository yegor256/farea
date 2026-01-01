/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * There is only one class {@link com.yegor256.farea.Farea} that helps
 * you run any Maven plugin(s).
 *
 * <p>It is as simple as the following:</p>
 *
 * <code><pre> new Farea(dir).together(f -> {
 *   f.files()
 *     .file("src/test/java/Hello.java")
 *     .write("class Hello {}".getBytes());
 *   f.dependencies()
 *     .append("org.cactoos", "cactoos", "0.55.0")
 *     .scope("test")
 *     .classifier("jar");
 *   f.build()
 *     .plugins()
 *     .append("com.qulice", "qulice-maven-plugin", "0.22.0")
 *     .configuration()
 *     .set("excludes", new String[] {"checkstyle:/src"});
 *   f.exec("install");
 *   assert(f.files().log().contains("SUCCESS"));
 * });</pre></code>
 *
 * <p>The logging is sent to the
 * <a href="https://www.slf4j.org/">Slf4j logging facility</a>,
 * which you can redirect to Log4j or any other
 * logging engine. Log events are sent to the
 * {@code com.yegor256.farea.Farea} and {@code com.jcabi.log}
 * packages.</p>
 *
 * @since 0.0.1
 */
package com.yegor256.farea;
