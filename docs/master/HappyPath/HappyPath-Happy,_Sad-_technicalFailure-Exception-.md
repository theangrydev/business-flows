---
title: HappyPath&lt;Happy, Sad&gt; technicalFailure(Exception)
layout: post
---
TODO: this is just a generated example

[javadoc](https://oss.sonatype.org/service/local/repositories/releases/archive/io/github/theangrydev/business-flows/10.3.0/business-flows-10.3.0-javadoc.jar/!/io/github/theangrydev/businessflows/HappyPath.html#technicalFailure-java.lang.Exception-) [usage tests](https://github.com/theangrydev/business-flows/blob/master/src/test/java/api/usage/happypath/TechnicalFailureTest.java)

Added in version 2.3.0


## Technical failure case happy path
```java
Exception technicalFailure = new Exception();

HappyPath<Happy, Sad> happyPath = HappyPath.technicalFailure(technicalFailure);

assertThat(happyPath.getTechnicalFailure()).isEqualTo(technicalFailure);
```
This produces a happy path biased view of an underlying technical failure case.

