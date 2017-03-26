---
title: HappyPath&lt;Happy, Sad&gt; sadPath(Sad)
layout: post
---
TODO: this is just a generated example

[javadoc](https://oss.sonatype.org/service/local/repositories/releases/archive/io/github/theangrydev/business-flows/10.3.0/business-flows-10.3.0-javadoc.jar/!/io/github/theangrydev/businessflows/HappyPath.html#sadPath-java.lang.Object-) [usage tests](https://github.com/theangrydev/business-flows/blob/master/src/test/java/api/usage/happypath/SadPathTest.java)

Added in version 2.3.0


## Sad case happy path
```java
Sad sad = new Sad();

HappyPath<Happy, Sad> happyPath = HappyPath.sadPath(sad);

assertThat(happyPath.getSad()).isEqualTo(sad);
```
This produces a happy path biased view of an underlying sad case.

