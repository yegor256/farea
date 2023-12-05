/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Yegor Bugayenko
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

import java.io.IOException;
import java.nio.file.Path;
import org.xembly.Directives;

/**
 * Plugins inside Build.
 *
 * @since 0.0.1
 */
public final class Plugins {

    /**
     * Location.
     */
    private final Pom pom;

    /**
     * Ctor.
     * @param file The POM
     */
    Plugins(final Pom file) {
        this.pom = file;
    }

    /**
     * Append Apache Maven plugin.
     * @param artifact The artifact ID
     * @param version The version
     * @return Plugin just added
     * @throws IOException If fails
     */
    public Plugin append(final String artifact,
        final String version) throws IOException {
        return this.append("org.apache.maven.plugins", artifact, version);
    }

    /**
     * Append custom plugin.
     * @param group The group ID
     * @param artifact The artifact ID
     * @param version The version
     * @return Plugin just added
     * @throws IOException If fails
     */
    public Plugin append(final String group, final String artifact,
        final String version) throws IOException {
        this.pom.modify(
            new Directives()
                .xpath("/project")
                .addIf("build")
                .addIf("plugins")
                .add("plugin")
                .add("groupId").set(group).up()
                .add("artifactId").set(artifact).up()
                .add("version").set(version).up()
        );
        return new Plugin(
            this.pom,
            Integer.parseInt(this.pom.xpath("count(/project/build/plugins/plugin)").get(0))
        );
    }

    /**
     * Add itself (the code in this classpath) to the Maven reactor.
     * @return Itself as a plugin
     * @throws IOException If fails
     */
    public Plugin appendItself() throws IOException {
        return this.appendItself(new Local().path());
    }

    /**
     * Add itself (the code in this classpath) to the Maven reactor.
     * @param local Path of local Maven repo, usually "~/.m2/repository"
     * @return Itself as a plugin
     * @throws IOException If fails
     */
    public Plugin appendItself(final Path local) throws IOException {
        final String group = "farea";
        final String artifact = "farea";
        final String version = "0.0.0";
        new Itself(group, artifact, version).deploy(local);
        return this.append(group, artifact, version);
    }

}
