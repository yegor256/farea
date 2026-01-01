/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Files in Maven Reactor.
 *
 * @since 0.2.0
 */
final class DtRequisites implements Requisites {

    /**
     * Home.
     */
    private final Path home;

    /**
     * Ctor.
     * @param dir The home dir
     */
    DtRequisites(final Path dir) {
        this.home = dir;
    }

    @Override
    public void show() throws IOException {
        this.file(".").show();
    }

    @Override
    public Requisite log() {
        return this.file("log.txt");
    }

    @Override
    public Requisite file(final String name) {
        return new DtRequisite(this.home, name);
    }

}
