/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * File in Maven Reactor.
 *
 * @since 0.0.1
 */
final class DtRequisite implements Requisite {

    /**
     * Home.
     */
    private final Path home;

    /**
     * Name.
     */
    private final String name;

    /**
     * Ctor.
     * @param dir The home dir
     * @param file The name of it
     */
    DtRequisite(final Path dir, final String file) {
        this.home = dir;
        this.name = file;
    }

    @Override
    @Deprecated
    public Requisite write(final String content) throws IOException {
        return this.write(content.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Requisite write(final byte[] content) throws IOException {
        final File parent = this.path().toFile().getParentFile();
        if (parent.mkdirs()) {
            Logger.debug(this, "Directory created at %[file]s", parent);
        }
        final boolean existed = this.path().toFile().exists();
        Files.write(
            this.path(),
            content
        );
        if (existed) {
            Logger.debug(
                this, "File replaced at %[file]s (%[size]s)",
                this.path(), this.path().toFile().length()
            );
        } else {
            Logger.debug(
                this, "File created at %[file]s (%[size]s)",
                this.path(), this.path().toFile().length()
            );
        }
        return this;
    }

    @Override
    public Requisite save(final Path src) throws IOException {
        if (src.toFile().isDirectory()) {
            final Path target = this.path();
            final Collection<Path> sources = Files.walk(src)
                .filter(file -> !file.toFile().isDirectory())
                .collect(Collectors.toList());
            final Requisites reqs = new DtRequisites(this.home);
            int total = 0;
            for (final Path file : sources) {
                reqs
                    .file(target.resolve(src.relativize(file)).toString())
                    .write(Files.readAllBytes(file));
                ++total;
            }
            Logger.debug(
                this, "Copied %d file(s) to %[file]s",
                total, target
            );
        } else {
            this.write(Files.readAllBytes(src));
        }
        return this;
    }

    @Override
    public String content() throws IOException {
        return new String(
            Files.readAllBytes(this.path()),
            StandardCharsets.UTF_8
        );
    }

    @Override
    public void show() throws IOException {
        if (this.path().toFile().isDirectory()) {
            Logger.info(
                this, "The content of the %[file]s directory:%n  %s",
                this.name,
                Files.walk(this.path())
                    .map(this.home::relativize)
                    .map(Path::toString)
                    .map(s -> String.format("%s", s))
                    .collect(Collectors.joining("\n  "))
            );
        } else {
            Logger.info(
                this, "The content of %[file]s:%n  %s",
                this.name,
                this.content().replace(
                    System.lineSeparator(),
                    String.format("%s  ", System.lineSeparator())
                )
            );
        }
    }

    @Override
    public void delete() throws IOException {
        final Path pth = this.path();
        if (pth.toFile().isDirectory()) {
            Files.walkFileTree(
                pth,
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(final Path file,
                        final BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(final Path dir,
                        final IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                }
            );
            Logger.debug(this, "Directory deleted at %[file]s", pth);
        } else {
            if (!pth.toFile().delete()) {
                throw new IOException(
                    String.format(
                        "Failed to delete %s",
                        pth
                    )
                );
            }
            Logger.debug(this, "File deleted at %[file]s", pth);
        }
    }

    @Override
    public boolean exists() {
        return this.path().toFile().exists();
    }

    @Override
    public Path path() {
        return this.home.resolve(this.name);
    }

}
