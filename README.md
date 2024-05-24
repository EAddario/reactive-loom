# reactive-loom
A simple program to compare the approach and performance of different concurrency models in Java, including [project reactor](https://projectreactor.io/) and [project loom](https://openjdk.org/projects/loom/).

It was partly inspired by [Ivan Vyazmitinov's](https://github.com/ivyazmitinov) excellent series: [Reactive’s Looming Doom](https://www.javacodegeeks.com/2022/09/reactives-looming-doom-part-i-evolution.html)

# Usage
Using maven:

```
mvn clean compile exec:exec
```

Using the command line:

```mvn clean compile exec:exec
mvn clean compile exec:exec
java --enable-preview -jar ./target/reactive-loom-full.jar
```

The above assumes you are in the root directory of the project.

# Release History
* v1.0.0 - Initial release

# Known Issues
None

# Motivation
TBA
