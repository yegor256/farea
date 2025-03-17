/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * An abstraction of the POM file ({@code pom.xml}) in the current directory,
 * that belongs to the current project under the test.
 *
 * <p>This is not the POM that is generated for the tests. Instead, it's
 * the POM that stays in the project being tested. This POM is used in order
 * to generate test POMs and even the "itself" JAR (by the {@link Itself}).</p>
 *
 * @since 0.0.1
 */
final class Base {

    /**
     * The location of pom.xml.
     */
    private final Path pom;

    /**
     * Ctor.
     */
    Base() {
        this(new File("pom.xml").toPath());
    }

    /**
     * Ctor.
     * @param src Original pom.xml
     */
    Base(final Path src) {
        this.pom = src;
    }

    /**
     * Get groupId.
     * @return Group ID
     * @throws IOException If fails
     */
    public String groupId() throws IOException {
        return this.inherit("groupId");
    }

    /**
     * Get artifactId.
     * @return Artifact ID
     * @throws IOException If fails
     */
    public String artifactId() throws IOException {
        return this.inherit("artifactId");
    }

    /**
     * Get version.
     * @return Version
     * @throws IOException If fails
     */
    public String version() throws IOException {
        return this.inherit("version");
    }

    /**
     * Get XML.
     * @return XML
     * @throws IOException If fails
     */
    public XML xml() throws IOException {
        if (!this.pom.toFile().exists()) {
            throw new IllegalStateException(
                String.format(
                    "The pom.xml is not found at this location: %s",
                    this.pom.toAbsolutePath()
                )
            );
        }
        return new XMLDocument(this.pom).registerNs(
            "mvn", "http://maven.apache.org/POM/4.0.0"
        );
    }

    /**
     * Take or inherit.
     * @param tag The tag in pom.xml
     * @return The value
     * @throws IOException If fails
     */
    private String inherit(final String tag) throws IOException {
        final List<String> vals = new LinkedList<>(
            this.xml().xpath(
                String.format("/mvn:project/mvn:%s/text()", tag)
            )
        );
        if (vals.isEmpty()) {
            vals.addAll(
                this.xml().xpath(
                    String.format("/mvn:project/mvn:parent/mvn:%s/text()", tag)
                )
            );
        }
        if (vals.isEmpty()) {
            throw new IllegalStateException(
                String.format("Neither project nor parent contains '%s' tag", tag)
            );
        }
        return vals.get(0);
    }

}
