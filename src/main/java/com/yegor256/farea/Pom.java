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
 * @since 0.0.1
 */
public final class Pom {

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
     * Show its XML content.
     * @return The XML
     * @throws IOException If fails
     */
    public String xml() throws IOException {
        return this.before().toString();
    }

    /**
     * Ctor.
     * @return Itself
     * @throws IOException If fails
     */
    public Pom init() throws IOException {
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
            new Properties(this)
                .set("maven.compiler.source", "11")
                .set("maven.compiler.target", "11");
            Logger.debug(
                this, "Maven POM created at %s (%d bytes)",
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
     * Ctor.
     * @param dirs Directives
     * @throws IOException If fails
     */
    void modify(final Iterable<Directive> dirs) throws IOException {
        Files.write(
            this.path,
            new XMLDocument(
                new Xembler(dirs).applyQuietly(this.before().node())
            ).toString().getBytes(StandardCharsets.UTF_8)
        );
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
