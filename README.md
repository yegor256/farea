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

It's a fake Maven Reactor, helping you to integration-test your custom Maven plugins.
There is a traditional way to do this: 
[Maven Invoker Plugin](https://maven.apache.org/plugins/maven-invoker-plugin/index.html).
It works perfectly, but it has two pretty annoying drawbacks:
1) It doesn't run from IDE (at least from IntelliJ IDEA),
and
2) It always starts the entire build from scratch, which makes it pretty slow.

Farea suggests an alternative way, which is way less flexible, but much
faster and JUnit-friendly.

First, you add this to your `pom.xml`:

```xml
<dependency>
  <groupId>com.yegor256</groupId>
  <artifactId>farea</artifactId>
  <version>0.0.12</version>
</dependency>
```

Then, you use it like this, in your JUnit5 test 
(obviously, you need to have `mvn` installed
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

This code will create a new `pom.xml` file in the temporary directory,
create a temporary `Hello.java` file, write simple content into it,
add a new `<dependency>` to the `pom.xml`, and then run `mvn test` in this
temporary directory. The output of the build will be saved to `log.txt`,
which is available through the call to `.log()` method.

You can also test the plugin that you are developing, inside the same reactor:

```java
class MyPluginTest {
    @Test
    void worksAsExpected(@TempDir Path dir) {
        new Farea(dir).together(f -> {
            f.build()
                .plugins()
                .appendItself()
                .phase("test")
                .goal("my-custom-goal")
                .configuration()
                .set("message", "Hello, world!");
            f.exec("test");
            assert (f.log().contains("SUCCESS"));
        });
    }
}
```

Here, a `.jar` with the entire classpath will be packaged and saved
into the `~/.m2/repository/` directory. This is almost exactly what 
the [`install`](https://maven.apache.org/plugins/maven-invoker-plugin/install-mojo.html) goal of the 
[invoker plugin](https://maven.apache.org/plugins/maven-invoker-plugin/) would do if you use it for
integration testing.

See how [antlr2ebnf-maven-plugin](https://github.com/yegor256/antlr2ebnf-maven-plugin)
is using Farea.

## How to Contribute

Fork repository, make changes, send us a 
[pull request](https://www.yegor256.com/2014/04/15/github-guidelines.html).
We will review your changes and apply them to the `master` branch shortly,
provided they don't violate our quality standards. To avoid frustration,
before sending us your pull request please run full Maven build:

```bash
$ mvn clean install -Pqulice
```

You will need Maven 3.3+ and Java 11+.
