/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package multi;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * Maven mojo.
 *
 * @since 0.1.0
 */
@Mojo(name = "multi", defaultPhase = LifecyclePhase.INITIALIZE)
public final class MultiMojo extends AbstractMojo {

    /**
     * Maven project.
     * @checkstyle VisibilityModifierCheck (5 lines)
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    /**
     * Maven session.
     * @checkstyle VisibilityModifierCheck (5 lines)
     */
    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession session;

    /**
     * Fake message to print in logs.
     * @checkstyle MemberNameCheck (7 lines)
     * @checkstyle VisibilityModifierCheck (5 lines)
     */
    @Parameter(property = "farea.message", required = true)
    private String message;

    @Override
    public void execute() {
        StaticLoggerBinder.getSingleton().setMavenLog(this.getLog());
        this.getLog().info(
            String.format(
                "project.name: %s", this.project.getName()
            )
        );
        this.getLog().info(
            String.format(
                "total goals: %d", this.session.getGoals().size()
            )
        );
        this.getLog().info(
            String.format(
                "message: %s", this.message
            )
        );
    }
}
