/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;
import java.nio.file.Path;
import org.xembly.Directives;

/**
 * Dependencies inside Build.
 *
 * @since 0.0.1
 */
final class DtDependencies implements Dependencies {

    /**
     * Home of the project.
     */
    private final Path home;

    /**
     * Location.
     */
    private final Pom pom;

    /**
     * Ctor.
     * @param dir The location of the project directory
     * @param file The POM
     */
    DtDependencies(final Path dir, final Pom file) {
        this.home = dir;
        this.pom = file;
    }

    @Override
    public Dependency append(final String group, final String artifact,
        final String version) throws IOException {
        this.pom.modify(
            new Directives()
                .xpath("/project")
                .addIf("dependencies")
                .add("dependency")
                .add("groupId").set(group).up()
                .add("artifactId").set(artifact).up()
                .add("version").set(version).up()
        );
        return new DtDependency(this.pom, group, artifact);
    }

    @Override
    public Dependency appendItself() throws IOException {
        return this.appendItself(new Local().path());
    }

    @Override
    public Dependency appendItself(final Path local) throws IOException {
        final Base base = new Base();
        return this.append(
            base.groupId(), base.artifactId(),
            new Itself(this.home, base, false).deploy(local)
        );
    }

}
