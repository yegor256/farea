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

import com.jcabi.log.Logger;
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
 * This classpath classes packaged as a plugin.
 *
 * @since 0.0.1
 */
final class Itself {

    /**
     * Fake groupId.
     */
    private final String group;

    /**
     * Fake artifactId.
     */
    private final String artifact;

    /**
     * Fake version.
     */
    private final String version;

    /**
     * Ctor.
     * @param grp The group
     * @param art The artifact
     * @param ver The version
     */
    Itself(final String grp, final String art, final String ver) {
        this.group = grp;
        this.artifact = art;
        this.version = ver;
    }

    /**
     * Deploy it to local Maven repository.
     * @throws IOException If fails
     */
    public void deploy() throws IOException {
        final Path mhome = Paths.get(System.getProperty("user.home")).resolve(".m2");
        if (!mhome.toFile().exists()) {
            throw new IllegalStateException(
                String.format(
                    "Maven home is not found at this location: %s",
                    mhome.toAbsolutePath()
                )
            );
        }
        final Path place = mhome.resolve(
            String.format(
                "repository/%s/%s/%s",
                this.group, this.artifact, this.version
            )
        );
        this.assembleJar(place.resolve(String.format("%s-0.0.0.jar", this.artifact)));
        this.assemblePom(place.resolve(String.format("%s-0.0.0.pom", this.artifact)));
    }

    /**
     * Create a fake POM for the module.
     * @param pom The location of the POM (pom.xml)
     * @throws IOException If fails
     */
    private void assemblePom(final Path pom) throws IOException {
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
            .add("artifactId").set(this.artifact).up()
            .add("groupId").set(this.group).up()
            .add("version").set(this.version).up()
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
        Logger.debug(
            Itself.class,
            "POM saved to %s (%d bytes)",
            pom, pom.toFile().length()
        );
    }

    /**
     * Zip the entire module into a JAR.
     * @param zip The location of the JAR
     * @throws IOException If fails
     */
    private void assembleJar(final Path zip) throws IOException {
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
                    final String name = src.relativize(file).toString().replace("\\", "/");
                    if (seen.contains(name)) {
                        continue;
                    }
                    final ZipEntry entry = new ZipEntry(name);
                    stream.putNextEntry(entry);
                    if ("META-INF/maven/plugin.xml".equals(name)) {
                        Files.copy(
                            this.update(
                                file,
                                zip.getParent().resolve("plugin.origin.xml")
                            ),
                            stream
                        );
                    } else {
                        Files.copy(file, stream);
                    }
                    stream.closeEntry();
                    seen.add(name);
                }
            }
        }
        Logger.debug(
            Itself.class,
            "JAR saved to %s (%d bytes)",
            zip, zip.toFile().length()
        );
    }

    /**
     * Take the plugin.xml and create a new one, with updated info inside.
     * @param desc The location of the original XML file
     * @param temp The destination where a new version will be created
     * @return The TEMP path
     * @throws IOException If fails
     */
    private Path update(final Path desc, final Path temp) throws IOException {
        Files.write(
            temp,
            new XMLDocument(
                new Xembler(
                    new Directives()
                        .xpath("/plugin/groupId").strict(1).set(this.group)
                        .xpath("/plugin/artifactId").strict(1).set(this.artifact)
                        .xpath("/plugin/version").strict(1).set(this.version)
                ).applyQuietly(new XMLDocument(desc).node())
            ).toString().getBytes(StandardCharsets.UTF_8)
        );
        return temp;
    }

}
