/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2024 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
 * POM file (pom.xml) in the current directory, that belongs to the current project
 * under test.
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
        return vals.get(0);
    }

}
