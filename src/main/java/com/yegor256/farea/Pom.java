/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.xembly.Directive;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * POM file.
 *
 * <p>The class is thread-safe.</p>
 *
 * @since 0.0.1
 */
final class Pom {

    /**
     * Location.
     */
    private final Path path;

    /**
     * Ctor.
     * @param file Location of it
     */
    Pom(final Path file) {
        this.path = file;
    }

    /**
     * Read and return its XML content.
     *
     * @return The XML
     * @throws IOException If fails
     */
    String xml() throws IOException {
        return this.before().toString();
    }

    /**
     * Initialize it, making sure it looks like a default {@code pom.xml}
     * for Apache Maven.
     *
     * @return Itself
     * @throws IOException If fails
     */
    Pom init() throws IOException {
        if (!this.path.toFile().exists()) {
            this.modify(
                new Directives()
                    .xpath("/")
                    .addIf("project")
                    .addIf("modelVersion").set("4.0.0").up()
                    .addIf("groupId").set("test").up()
                    .addIf("artifactId").set("test").up()
                    .addIf("version").set("0.0.0").up()
                    .addIf("name")
                    .set("test")
            );
            new DtProperties(this)
                .set("maven.compiler.source", "11")
                .set("maven.compiler.target", "11");
            Logger.debug(
                this, "Maven POM created at %[file]s (%[size]s)",
                this.path.toAbsolutePath(), this.path.toFile().length()
            );
        }
        return this;
    }

    /**
     * Get by XPath.
     * @param expr XPath expression
     * @return List of values
     * @throws IOException If fails
     */
    List<String> xpath(final String expr) throws IOException {
        return this.before().xpath(expr);
    }

    /**
     * Modify POM with the provided Xembly directives.
     *
     * <p>This method is thread-safe.</p>
     *
     * @param dirs Directives
     * @throws IOException If fails
     */
    void modify(final Iterable<Directive> dirs) throws IOException {
        if (this.path.toFile().getParentFile().mkdirs()) {
            Logger.debug(
                this, "Directory created at %[file]s",
                this.path.toFile().getParentFile()
            );
        }
        synchronized (this.path) {
            Files.write(
                this.path,
                new XMLDocument(
                    new Xembler(dirs).applyQuietly(this.before().node())
                ).toString().getBytes(StandardCharsets.UTF_8)
            );
        }
    }

    /**
     * Get content as is now.
     * @return XML
     * @throws IOException If fails
     */
    private XML before() throws IOException {
        final XML before;
        if (this.path.toFile().exists()) {
            before = new XMLDocument(this.path);
        } else {
            before = new XMLDocument("<project/>");
        }
        return before;
    }
}
