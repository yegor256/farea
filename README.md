[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/yegor256/farea)](http://www.rultor.com/p/yegor256/farea)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/yegor256/farea/actions/workflows/mvn.yml/badge.svg)](https://github.com/yegor256/farea/actions/workflows/mvn.yml)
[![PDD status](http://www.0pdd.com/svg?name=yegor256/farea)](http://www.0pdd.com/p?name=yegor256/farea)
[![Maven Central](https://img.shields.io/maven-central/v/com.yegor256/farea.svg)](https://maven-badges.herokuapp.com/maven-central/com.yegor256/farea)
[![Javadoc](http://www.javadoc.io/badge/com.yegor256/farea.svg)](http://www.javadoc.io/doc/com.yegor256/farea)
[![codecov](https://codecov.io/gh/yegor256/farea/branch/master/graph/badge.svg)](https://codecov.io/gh/yegor256/farea)
[![Hits-of-Code](https://hitsofcode.com/github/yegor256/farea)](https://hitsofcode.com/view/github/yegor256/farea)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/yegor256/farea/blob/master/LICENSE.txt)

It's a fake Maven Reactor, helping you to unit test your custom Maven plugins.

First, you add this to your `pom.xml`:

```xml
<dependency>
  <groupId>com.yegor256</groupId>
  <artifactId>farea</artifactId>
  <version>0.0.4</version>
</dependency>
```

Then, you use it like this, in your JUnit5 test (obviously, you need to have `mvn` installed
and available on `$PATH`):

```java
import com.yegor256.farea.Farea;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JavaCompilationTest {
    @Test
    void worksAsExpected(@TempDir Path dir) {
        new Farea(dir).together(f -> {
            f.files()
                .file("src/test/java/Hello.java")
                .write("class Hello {}");
            f.dependencies().append("org.cactoos", "cactoos", "0.55.0");
            f.exec("compile");
            assert (f.log().contains("SUCCESS"));
        });
    }
}
```

You can add files to your project, configure `pom.xml`, execute some plugins,
and then assert on what is created.

You can also test the plugin that you are developing, inside the same reactor:

```java
new Farea(dir)
  .build()
  .plugins()
  .appendItself()
  .phase("test")
  .goal("my-custom-goal")
  .configuration()
  .set("message", "Hello, world!");
new Farea(dir).exec("test");
assert(Farea(dir).log().contains("SUCCESS"));
```

Here, a `.jar` with the entire classpath will be packaged and saved
into `~/.md/repository/farea/farea/farea-0.0.0.jar`. Then, this
synthetic plugin is used for testing.

## How to Contribute

Fork repository, make changes, send us a [pull request](https://www.yegor256.com/2014/04/15/github-guidelines.html).
We will review your changes and apply them to the `master` branch shortly,
provided they don't violate our quality standards. To avoid frustration,
before sending us your pull request please run full Maven build:

```bash
$ mvn clean install -Pqulice
```

You will need Maven 3.3+ and Java 8+.
