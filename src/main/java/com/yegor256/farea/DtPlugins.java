/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
final class DtPlugins implements Plugins {

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
    DtPlugins(final Path dir, final Pom file) {
        this.home = dir;
        this.pom = file;
    }

    @Override
    public Plugin append(final String artifact,
        final String version) throws IOException {
        return this.append("org.apache.maven.plugins", artifact, version);
    }

    @Override
    public Plugin append(final String group, final String artifact,
        final String version) throws IOException {
        this.pom.modify(
            new Directives()
                .xpath("/project")
                .addIf("build")
                .addIf("plugins")
                .xpath(
                    String.format(
                        "/project/build/plugins[not(plugin[groupId='%s' and artifactId='%s'])]",
                        group, artifact
                    )
                )
                .add("plugin")
                .add("groupId").set(group).up()
                .add("artifactId").set(artifact).up()
                .add("version").set(version).up()
        );
        return new DtPlugin(
            this.pom,
            Integer.parseInt(
                this.pom.xpath(
                    String.format(
                        "count(/project/build/plugins/plugin[artifactId='%s' and groupId='%s']/preceding-sibling::*)+1",
                        artifact, group
                    )
                ).get(0)
            )
        );
    }

    @Override
    public Plugin appendItself() throws IOException {
        return this.appendItself(new Local().path());
    }

    @Override
    public Plugin appendItself(final Path local) throws IOException {
        final Base base = new Base();
        return this.append(
            base.groupId(), base.artifactId(),
            new Itself(this.home, base).deploy(local)
        );
    }

}
