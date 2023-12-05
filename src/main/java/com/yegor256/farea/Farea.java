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

import com.jcabi.log.Logger;
import com.yegor256.Jaxec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Fake Maven Reactor.
 *
 * <p>Run it like this to test a simple Java compilation
 * (here, the {@code dir} is a temporary directory where Maven
 * project will be created and executed):</p>
 *
 * <code><pre> new Farea(dir).together(f -> {
 *   f.files()
 *     .file("src/test/java/Hello.java")
 *     .write("class Hello {}");
 *   f.exec("compile");
 *   assert(f.log().contains("SUCCESS"));
 * });</pre></code>
 *
 * <p>If you are developing/testing your own plugin, you should use
 * the {@link Plugins#appendItself()} method, which you access
 * through {@link Farea#build()} and then {@code .plugins()}:</p>
 *
 * <code><pre> new Farea(dir).together(f -> {
 *   f.build()
 *     .plugins()
 *     .appendItself()
 *     .goal("my-goal")
 *     .phase("test")
 *     .configuration("message", "Hello, world!");
 *   f.exec("test");
 * });</pre></code>
 *
 * <p>The class is thread-safe, which means that you can use it
 * in many parallel threads. However, if you don't use the
 * {@link Farea#together(Script)}, your threads may conflict at the level
 * of files in your local Maven repository,
 * inside the {@code ~/.m2/repository} directory. Thus, it is strongly
 * recommended to use {@link Farea#together(Script)}.</p>
 *
 * @since 0.0.1
 */
public final class Farea {

    /**
     * Home.
     */
    private final Path home;

    /**
     * Maven opts.
     */
    private final Collection<String> opts;

    /**
     * Ctor.
     * @param dir The home dir
     */
    public Farea(final Path dir) {
        this(
            dir,
            Arrays.asList(
                "--update-snapshots",
                "--batch-mode",
                "--fail-fast",
                "--errors"
            )
        );
    }

    /**
     * Ctor.
     * @param dir The home dir
     * @param mopts Maven opts
     */
    public Farea(final Path dir, final Collection<String> mopts) {
        this.home = dir;
        this.opts = Collections.unmodifiableCollection(mopts);
    }

    /**
     * With an extra option.
     * @param opt The option to add
     * @return Itself
     */
    public Farea withOpt(final String opt) {
        final Collection<String> after = new ArrayList<>(this.opts.size() + 1);
        after.addAll(this.opts);
        after.add(opt);
        return new Farea(this.home, after);
    }

    /**
     * Run it all together in one thread-safe script.
     * @param script The script to run
     * @throws IOException If fails
     */
    public void together(final Farea.Script script) throws IOException {
        synchronized (Farea.class) {
            script.run(this);
        }
    }

    /**
     * Access to files.
     * @return Files in home
     */
    public Requisites files() {
        return new Requisites(this.home);
    }

    /**
     * Get access to build.
     * @return Build
     * @throws IOException If fails
     */
    public Build build() throws IOException {
        return new Build(this.pom());
    }

    /**
     * Ctor.
     * @return Deps
     * @throws IOException If fails
     */
    public Dependencies dependencies() throws IOException {
        return new Dependencies(this.pom());
    }

    /**
     * Execute with command line arguments.
     * @param args Command line arguments
     * @throws IOException If fails
     */
    public void exec(final String... args) throws IOException {
        this.pom().init();
        final Path log = this.home.resolve("log.txt");
        Farea.log("pom.xml", this.pom().xml());
        new Jaxec()
            .with(Farea.mvn())
            .with(this.opts)
            .with(args)
            .withHome(this.home)
            .withCheck(false)
            .withRedirect(true)
            .withStdout(ProcessBuilder.Redirect.to(log.toFile()))
            .exec();
        Farea.log("Maven stdout", new String(Files.readAllBytes(log), StandardCharsets.UTF_8));
    }

    /**
     * Log file.
     * @return Files in home
     * @throws IOException If fails
     */
    public String log() throws IOException {
        return this.files().file("log.txt").content();
    }

    /**
     * Execute with command line arguments.
     * @return POM
     * @throws IOException If fails
     */
    private Pom pom() throws IOException {
        return new Pom(this.home.resolve("pom.xml")).init();
    }

    /**
     * Name of Maven executable, specific for an operating system.
     * @return The command
     */
    private static Collection<String> mvn() {
        final Collection<String> cmd = new LinkedList<>();
        if (System.getProperty("os.name").toLowerCase(Locale.getDefault()).contains("windows")) {
            cmd.add("cmd");
            cmd.add("/c");
            cmd.add("mvn");
        } else {
            cmd.add("mvn");
        }
        return cmd;
    }

    /**
     * Log with indentation.
     * @param intro The intro message
     * @param body The body
     */
    private static void log(final String intro, final String body) {
        Logger.debug(
            Farea.class,
            "%s:%n  %s",
            intro,
            body.replace(
                System.lineSeparator(),
                String.format("%s  ", System.lineSeparator())
            )
        );
    }

    /**
     * Script to run.
     *
     * @since 0.0.4
     */
    public interface Script {
        /**
         * Run it.
         * @param farea Instance of itself
         * @throws IOException If fails
         */
        void run(Farea farea) throws IOException;
    }

}
