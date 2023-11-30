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

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Plugins inside Build.
 *
 * @since 0.0.1
 */
final class Plugins {

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
     * Ctor.
     * @param group The group ID
     * @param artifact The artifact ID
     * @param version The version
     * @return Plugin just added
     * @throws IOException If fails
     */
    Plugin append(final String group, final String artifact,
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
    Plugin appendItself() throws IOException {
        final Path mhome = Paths.get(System.getenv("HOME")).resolve(".m2");
        if (!mhome.toFile().exists()) {
            throw new IllegalStateException(
                String.format(
                    "Maven home is not found at this location: %s",
                    mhome.toAbsolutePath()
                )
            );
        }
        final Path place = mhome.resolve("repository/farea/farea/0.0.0");
        Plugins.assembleJar(place.resolve("farea-0.0.0.jar"));
        Plugins.assemblePom(place.resolve("farea-0.0.0.pom"));
        return this.append("farea", "farea", "0.0.0");
    }

    /**
     * Create a fake POM for the module.
     * @param pom The location of the POM (pom.xml)
     * @throws IOException If fails
     */
    private static void assemblePom(final Path pom) throws IOException {
        final File xml = new File("pom.xml");
        if (!xml.exists()) {
            throw new IllegalStateException(
                String.format(
                    "The pom.xml is not found at this location: %s",
                    xml.getAbsolutePath()
                )
            );
        }
        final List<XML> deps = new XMLDocument(xml)
            .registerNs("mvn", "http://maven.apache.org/POM/4.0.0")
            .nodes("/mvn:project/mvn:dependencies/mvn:dependency");
        final Directives dirs = new Directives()
            .add("project")
            .add("modelVersion").set("4.0.0").up()
            .add("artifactId").set("farea").up()
            .add("groupId").set("farea").up()
            .add("version").set("0.0.0").up()
            .add("dependencies");
        for (final XML dep : deps) {
            dirs.xpath("/project/dependencies")
                .strict(1)
                .add("dependency")
                .append(dep.node());
        }
        Files.write(
            pom,
            new Xembler(dirs).xmlQuietly().getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Zip the entire module into a JAR.
     * @param zip The location of the JAR
     * @throws IOException If fails
     */
    private static void assembleJar(final Path zip) throws IOException {
        if (zip.toFile().exists()) {
            Files.delete(zip);
        }
        zip.toFile().getParentFile().mkdirs();
        Files.createFile(zip);
        final Set<String> seen = new HashSet<>();
        final String classpath = System.getProperty("java.class.path");
        final String[] jars = classpath.split(File.pathSeparator);
        try (ZipOutputStream stream = new ZipOutputStream(Files.newOutputStream(zip))) {
            for (final String path : jars) {
                final File jar = new File(path);
                if (!jar.isDirectory()) {
                    continue;
                }
                final Path src = jar.toPath();
                for (final Path file : Files.walk(src).collect(Collectors.toList())) {
                    if (file.toFile().isDirectory()) {
                        continue;
                    }
                    final String name = src.relativize(file).toString();
                    if (seen.contains(name)) {
                        continue;
                    }
                    final ZipEntry entry = new ZipEntry(name);
                    stream.putNextEntry(entry);
                    Files.copy(file, stream);
                    stream.closeEntry();
                    seen.add(name);
                }
            }
        }
    }

}
