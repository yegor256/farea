/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2025 Yegor Bugayenko
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
import com.jcabi.log.VerboseRunnable;
import com.yegor256.Jaxec;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Fake Maven Reactor.
 *
 * <p>Run it like this, to test a simple Java compilation
 * (here, the {@code dir} is a temporary directory where Maven
 * project will be created and executed):</p>
 *
 * <code><pre> new Farea(dir).together(f -> {
 *   f.files()
 *     .file("src/test/java/Hello.java")
 *     .write("class Hello {}".getBytes());
 *   f.exec("compile");
 *   assert(f.files().log().content().contains("SUCCESS"));
 * });</pre></code>
 *
 * <p>If you are developing/testing your own plugin, you should use
 * the {@link DtPlugins#appendItself()} method, which you access
 * through {@link Farea#build()} and then {@code .plugins()}:</p>
 *
 * <code><pre> new Farea(dir).together(f -> {
 *   f.build()
 *     .plugins()
 *     .appendItself()
 *     .goal("my-goal")
 *     .phase("test")
 *     .configuration()
 *     .set("message", "Hello, world!");
 *   f.exec("test");
 * });</pre></code>
 *
 * <p>The class is thread-safe, which means that you can use it
 * in many parallel threads. However, if you don't use the
 * {@link Farea#together(Farea.Script)}, your threads may conflict at the level
 * of files in your local Maven repository,
 * inside the {@code ~/.m2/repository} directory. Thus, it is strongly
 * recommended to use {@link Farea#together(Farea.Script)}.</p>
 *
 * @since 0.0.1
 */
@SuppressWarnings("PMD.TooManyMethods")
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
     * @since 0.1.0
     */
    public Farea(final File dir) {
        this(dir.toPath());
    }

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
     * @since 0.1.0
     */
    public Farea(final File dir, final Collection<String> mopts) {
        this(dir.toPath(), mopts);
    }

    /**
     * Ctor.
     * @param dir The home dir
     * @param mopts Maven opts
     * @since 0.1.0
     */
    public Farea(final File dir, final String... mopts) {
        this(dir, Arrays.asList(mopts));
    }

    /**
     * Ctor.
     * @param dir The home dir
     * @param mopts Maven opts
     * @since 0.1.0
     */
    public Farea(final Path dir, final String... mopts) {
        this(dir, Arrays.asList(mopts));
    }

    /**
     * Ctor.
     * @param dir The home dir
     * @param mopts Maven opts
     */
    public Farea(final Path dir, final Collection<String> mopts) {
        this.home = dir;
        this.opts = new ArrayList<>(mopts);
    }

    /**
     * Clean the reactor, remove all files from it.
     * @throws IOException If fails
     */
    public void clean() throws IOException {
        if (this.home.toFile().mkdirs()) {
            Logger.debug(this, "Directory created at %[file]s", this.home);
        }
        try (Stream<Path> dir = Files.walk(this.home)) {
            dir
                .map(Path::toFile)
                .sorted(Comparator.reverseOrder())
                .forEach(File::delete);
        }
    }

    /**
     * With an extra command-line option.
     * @param opt The option to add
     */
    public void withOpt(final String opt) {
        this.opts.add(opt);
    }

    /**
     * Run it all together.
     *
     * <p>This method doesn't guarantee thread-safety. If you run it
     * with the same directory, most probably there will be problems, because
     * of conflicts between running Maven processes.</p>
     *
     * @param script The script to run
     * @throws IOException If fails
     */
    public void together(final Farea.Script script) throws IOException {
        script.run(this);
    }

    /**
     * Access to files.
     * @return Files in home
     */
    public Requisites files() {
        return new DtRequisites(this.home);
    }

    /**
     * Access to properties.
     * @return Properties in the pom.xml
     * @throws IOException If fails
     */
    public Properties properties() throws IOException {
        return new DtProperties(this.pom());
    }

    /**
     * Get access to build.
     * @return Build
     * @throws IOException If fails
     */
    public Build build() throws IOException {
        return new DtBuild(this.home, this.pom());
    }

    /**
     * Ctor.
     * @return Deps
     * @throws IOException If fails
     */
    public Dependencies dependencies() throws IOException {
        return new DtDependencies(this.home, this.pom());
    }

    /**
     * Execute with command line arguments.
     *
     * <p>If Maven fails, this method will <b>NOT</b> throw any exceptions. Instead,
     * you should check the contents of the log printed by Maven, with the
     * help of the {@link #log()} method. Otherwise, use the {@link #exec(String...)}
     * method, it will throw in case of build failure. You can also check
     * the returned integer, which would be equal to the exit code of the Maven
     * process (zero means success, while anything else means an error).</p>
     *
     * @param args Command line arguments
     * @return Exit code of the Maven process
     * @throws IOException If fails
     */
    public int execQuiet(final String... args) throws IOException {
        int code = 0;
        try {
            this.exec(args);
        } catch (final Farea.BuildFailureException ex) {
            Logger.debug(this, "Build failed with exit code 0x%04x", ex.getCode());
            code = ex.getCode();
        }
        return code;
    }

    /**
     * Execute with command line arguments.
     *
     * <p>If Maven fails, this method will throw an exception.</p>
     *
     * @param args Command line arguments
     * @throws IOException If fails
     */
    public void exec(final String... args) throws IOException {
        this.pom().init();
        final Path log = this.home.resolve("log.txt");
        if (Logger.isDebugEnabled(Farea.class)) {
            Farea.log(
                Level.FINER,
                Logger.format("pom.xml at %[file]s", this.home),
                this.pom().xml()
            );
            Farea.log(
                Level.FINER,
                Logger.format("Files at %[file]s", this.home),
                this.walk()
            );
        }
        Logger.debug(this, "Log stream redirected to %[file]s", log);
        final AtomicBoolean finished = new AtomicBoolean(false);
        final Thread terminal = new Thread(
            new VerboseRunnable(
                () -> this.tail(log, finished)
            )
        );
        terminal.start();
        final int code;
        try {
            code = this.jaxec(args, log);
        } finally {
            finished.set(true);
            Farea.join(terminal);
        }
        if (Logger.isDebugEnabled(Farea.class)) {
            Farea.log(
                Level.FINER,
                "Maven stdout",
                new String(Files.readAllBytes(log), StandardCharsets.UTF_8)
            );
            Farea.log(
                Level.FINER,
                Logger.format("Files after execution at %[file]s", this.home),
                this.walk()
            );
        }
        if (code != 0) {
            Farea.log(
                Level.WARNING,
                Logger.format("The pom.xml at %[file]s after the failed build", this.home),
                this.pom().xml()
            );
            Farea.log(
                Level.WARNING, "The stdout of the failed Maven build",
                new String(Files.readAllBytes(log), StandardCharsets.UTF_8)
            );
            throw new Farea.BuildFailureException(code);
        }
    }

    /**
     * Log file.
     * @return Files in home
     * @throws IOException If fails
     */
    public Requisite log() throws IOException {
        return new DtRequisite(this.home, "log.txt");
    }

    /**
     * List of all files.
     * @return List of files in the dir
     * @throws IOException If fails
     */
    public String walk() throws IOException {
        return Files.walk(this.home)
            .map(this.home::relativize)
            .map(Path::toString)
            .map(s -> String.format("%s", s))
            .collect(Collectors.joining("\n"));
    }

    /**
     * Run Maven with these args, saving output to the log.
     * @param args The args for the "mvn" command
     * @param log The log file
     * @return Shell exit code
     */
    private int jaxec(final String[] args, final Path log) {
        return new Jaxec()
            .with(Farea.mvn())
            .with(this.opts)
            .with(args)
            .withHome(this.home)
            .withCheck(false)
            .withRedirect(true)
            .withStdout(ProcessBuilder.Redirect.to(log.toFile()))
            .exec()
            .code();
    }

    /**
     * Tail log file.
     * @param log The file
     * @param finished When to stop
     * @return Total number of bytes seen
     * @throws IOException If fails
     */
    private Long tail(final Path log, final AtomicBoolean finished) throws IOException {
        long pos = 0L;
        while (!finished.get()) {
            if (!log.toFile().exists()) {
                continue;
            }
            try (RandomAccessFile reader = new RandomAccessFile(log.toFile(), "r")) {
                final long length = log.toFile().length();
                if (length < pos) {
                    break;
                }
                if (length > pos) {
                    reader.seek(pos);
                    while (true) {
                        final String line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        Logger.debug(this, line);
                    }
                    pos = reader.getFilePointer();
                }
            }
            Farea.sleep();
        }
        return pos;
    }

    /**
     * Sleep a little bit.
     */
    private static void sleep() {
        try {
            Thread.sleep(1000L);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Join this thread.
     * @param thread The thread to join
     */
    private static void join(final Thread thread) {
        try {
            thread.join(10_000L, 0);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
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
     * @param level Logging level
     * @param intro The intro message
     * @param body The body
     */
    private static void log(final Level level, final String intro, final String body) {
        Logger.log(
            level,
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
     * When build fails.
     *
     * @since 0.9.0
     */
    public static final class BuildFailureException extends IOException {
        /**
         * Serialization marker.
         */
        private static final long serialVersionUID = 5188162404688529763L;

        /**
         * The exit code.
         */
        private final int exit;

        /**
         * Ctor.
         * @param code The exit code of Maven build
         */
        public BuildFailureException(final int code) {
            super(String.format("build failed with exit code 0x%04x", code));
            this.exit = code;
        }

        /**
         * Get the code.
         * @return The code
         */
        public int getCode() {
            return this.exit;
        }
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
