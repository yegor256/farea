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
package com.yegor256;

import java.io.IOException;
import org.xembly.Directives;

/**
 * Dependency inside Dependencies.
 *
 * @since 0.0.1
 */
final class Dependency {

    /**
     * Location.
     */
    private final Pom pom;

    /**
     * Group.
     */
    private final String group;

    /**
     * Artifact.
     */
    private final String artifact;

    /**
     * Ctor.
     * @param file The POM
     * @param grp GroupId
     * @param art ArtifactId
     */
    Dependency(final Pom file, final String grp, final String art) {
        this.pom = file;
        this.group = grp;
        this.artifact = art;
    }

    /**
     * Ctor.
     * @param scp The scope
     * @return Itself
     * @throws IOException If fails
     */
    Dependency scope(final String scp) throws IOException {
        this.pom.modify(
            new Directives().xpath(
                String.format(
                    "/project/dependencies/dependency[groupId='%s' and artifactId='%s']",
                    this.group, this.artifact
                )
            ).addIf("scope").set(scp)
        );
        return this;
    }

}
