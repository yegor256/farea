# Fake Maven Reactor, for Quick Unit Tests

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](https://www.rultor.com/b/yegor256/farea)](https://www.rultor.com/p/yegor256/farea)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/yegor256/farea/actions/workflows/mvn.yml/badge.svg)](https://github.com/yegor256/farea/actions/workflows/mvn.yml)
[![PDD status](https://www.0pdd.com/svg?name=yegor256/farea)](https://www.0pdd.com/p?name=yegor256/farea)
[![Maven Central](https://img.shields.io/maven-central/v/com.yegor256/farea.svg)](https://maven-badges.herokuapp.com/maven-central/com.yegor256/farea)
[![Javadoc](https://www.javadoc.io/badge/com.yegor256/farea.svg)](https://www.javadoc.io/doc/com.yegor256/farea)
[![codecov](https://codecov.io/gh/yegor256/farea/branch/master/graph/badge.svg)](https://codecov.io/gh/yegor256/farea)
[![Hits-of-Code](https://hitsofcode.com/github/yegor256/farea)](https://hitsofcode.com/view/github/yegor256/farea)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/yegor256/farea/blob/master/LICENSE.txt)

It's a fake Maven Reactor, helping you to integration-test
your custom Maven plugins.
There is a traditional way to do this:
[Maven Invoker Plugin][invoker].
It works perfectly, but it has two pretty annoying
drawbacks: 1) It doesn't run from IDE (at least from IntelliJ IDEA),
and 2) It always starts the entire build from scratch,
which makes 3) it pretty slow.

Farea suggests an alternative way, which is way less flexible, but much
faster and JUnit-friendly.

First, you add this to your `pom.xml`:

```xml
<dependency>
  <groupId>com.yegor256</groupId>
  <artifactId>farea</artifactId>
  <version>0.15.4</version>
</dependency>
```

Then, you use it like this, in your JUnit5 test
(obviously, you need to have `mvn` installed
and available on `$PATH`):

```java
import com.yegor256.farea.Farea;
import com.yegor256.farea.RequisiteMatcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JavaCompilationTest {
  @Test
  void worksAsExpected(@Mktmp Path dir) {
    new Farea(dir).together(f -> {
      f.files()
        .file("src/test/java/Hello.java")
        .write("class Hello {}".getBytes());
      f.dependencies().append("org.cactoos", "cactoos", "0.55.0");
      f.exec("compile");
      MatcherAssert.assertThat(
        "Compiles without any issues",
        f.files().log(),
        RequisiteMatcher.SUCCESS
      );
    });
  }
}
```

This code will create a new `pom.xml` file in the temporary directory,
create a temporary `Hello.java` file, write simple content into it,
add a new `<dependency>` to the `pom.xml`, and then run `mvn test` in this
temporary directory. The output of the build will be saved to `log.txt`,
which is available through the call to `.log()` method.

You can also test the plugin that you are developing, inside the same reactor:

```java
class MyPluginTest {
  @Test
  void worksAsExpected(@Mktmp Path dir) {
    new Farea(dir).together(f -> {
      f.build()
        .plugins()
        .appendItself()
        .execution()
        .phase("test")
        .goals("my-custom-goal")
        .configuration()
        .set("message", "Hello, world!");
      f.exec("test");
      assert f.files().log().contains("SUCCESS");
    });
  }
}
```

Here, a `.jar` with the entire classpath will be packaged and saved
into the `~/.m2/repository/` directory. This is almost exactly what
the [`install`][install-mojo] goal of the
[invoker plugin][invoker] would do if you use it for
integration testing.

It is recommended to add this to your `pom.xml`, in order
to enable interactive test runs right from the IDE:

```xml
<project>
  <build>
    <plugins>
      [...]
      <plugin>
        <artifactId>maven-resources-plugin</artifactId>
        <executions>
          <execution>
            <id>copy-descriptor</id>
            <phase>process-classes</phase>
            <goals>
              <goal>copy-resources</goal>
            </goals>
            <configuration>
              <outputDirectory>${project.build.directory}</outputDirectory>
              <resources>
                <resource>
                  <directory>${project.build.outputDirectory}/META-INF/maven</directory>
                  <includes>
                    <include>plugin.xml</include>
                  </includes>
                </resource>
              </resources>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>
```

This will make sure the `META-INF/maven/plugin.xml` is not destroyed
in the `target/classes` by the IDE before the next test run.

See how
[antlr2ebnf-maven-plugin](https://github.com/yegor256/antlr2ebnf-maven-plugin)
is using Farea.

## How to Contribute

Fork repository, make changes, send us a
[pull request](https://www.yegor256.com/2014/04/15/github-guidelines.html).
We will review your changes and apply them to the `master` branch shortly,
provided they don't violate our quality standards. To avoid frustration,
before sending us your pull request please run full Maven build:

```bash
mvn clean install -Pqulice
```

You will need Maven 3.3+ and Java 11+.

[invoker]: https://maven.apache.org/plugins/maven-invoker-plugin/index.html
[install-mojo]: https://maven.apache.org/plugins/maven-invoker-plugin/install-mojo.html
