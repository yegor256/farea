/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;
import org.xembly.Directives;

/**
 * Dependency inside Dependencies.
 *
 * @since 0.0.1
 */
final class DtDependency implements Dependency {

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
    DtDependency(final Pom file, final String grp, final String art) {
        this.pom = file;
        this.group = grp;
        this.artifact = art;
    }

    @Override
    public Dependency scope(final String scp) throws IOException {
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

    @Override
    public Dependency classifier(final String classifier) throws IOException {
        this.pom.modify(
            new Directives().xpath(
                String.format(
                    "/project/dependencies/dependency[groupId='%s' and artifactId='%s']",
                    this.group, this.artifact
                )
            ).addIf("classifier").set(classifier)
        );
        return this;
    }

}
