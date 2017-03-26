---
title: HappyPath&lt;Happy, Sad&gt; happyPath(Happy)
layout: post
---
TODO: this is just a generated example

[javadoc](https://oss.sonatype.org/service/local/repositories/releases/archive/io/github/theangrydev/business-flows/10.3.0/business-flows-10.3.0-javadoc.jar/!/io/github/theangrydev/businessflows/HappyPath.html#happyPath-java.lang.Object-) [usage tests](https://github.com/theangrydev/business-flows/blob/master/src/test/java/api/usage/happypath/HappyPathTest.java)

Added in version 1.0.0


## Happy case happy path
```java
Happy happy = new Happy();

HappyPath<Happy, Sad> happyPath = HappyPath.happyPath(happy);

assertThat(happyPath.getHappy()).isEqualTo(happy);
```
This produces a happy path biased view of an underlying happy case.

