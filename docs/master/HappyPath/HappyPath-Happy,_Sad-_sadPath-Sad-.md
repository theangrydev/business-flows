---
title: HappyPath&lt;Happy, Sad&gt; sadPath(Sad)
layout: post
---
TODO: this is just a generated example

[javadoc](https://oss.sonatype.org/service/local/repositories/releases/archive/io/github/theangrydev/business-flows/10.3.0/business-flows-10.3.0-javadoc.jar/!/io/github/theangrydev/businessflows/HappyPath.html#sadPath-java.lang.Object-) [usage tests](https://github.com/theangrydev/business-flows/blob/master/src/test/java/api/usage/happypath/HappyPathBaseCasesTest.java)

Added in version 2.3.0

These are the happy path biased views of the three possible underlying cases (happy, sad or technical failure).

## Happy case happy path
```java
Happy happy = new Happy();

HappyPath<Happy, Sad> happyPath = HappyPath.happyPath(happy);

assertThat(happyPath.getHappy()).isEqualTo(happy);
```

## Sad case happy path
```java
Sad sad = new Sad();

HappyPath<Happy, Sad> happyPath = HappyPath.sadPath(sad);

assertThat(happyPath.getSad()).isEqualTo(sad);
```

## Technical failure case happy path
```java
Exception technicalFailure = new Exception();

HappyPath<Happy, Sad> happyPath = HappyPath.technicalFailure(technicalFailure);

assertThat(happyPath.getTechnicalFailure()).isEqualTo(technicalFailure);
```

