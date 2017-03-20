---
title: HappyPath&lt;Happy, Sad&gt; happyPathAttempt(BusinessFlow&lt;Happy, Sad&gt;&gt;)
layout: post
---
TODO: this is just a generated example

[javadoc](https://oss.sonatype.org/service/local/repositories/releases/archive/io/github/theangrydev/business-flows/10.3.0/business-flows-10.3.0-javadoc.jar/!/io/github/theangrydev/businessflows/HappyPath.html#happyPathAttempt-io.github.theangrydev.businessflows.Attempt-) [usage tests](https://github.com/theangrydev/business-flows/blob/master/src/test/java/api/usage/happypath/HappyPathAttemptTest.java)

Added in version 2.5.0

Sometimes it makes sense to attempt an action that will either produce a happy path or throw an exception.

## Happy path attempt can fail
```java
RuntimeException technicalFailure = new RuntimeException();
Attempt<HappyPath<Happy, Sad>> attempt = () -> {throw technicalFailure;};

HappyPath<Happy, Sad> happyPath = HappyPath.happyPathAttempt(attempt);

assertThat(happyPath.getTechnicalFailure()).isEqualTo(technicalFailure);
```
An attempt can fail and turn into a technical failure.

## Happy path attempt can succeed
```java
Happy happy = new Happy();
Attempt<HappyPath<Happy, Sad>> attempt = () -> HappyPath.happyPath(happy);

HappyPath<Happy, Sad> happyPath = HappyPath.happyPathAttempt(attempt);

assertThat(happyPath.getHappy()).isSameAs(happy);
```
An attempt can succeed and produce a happy path

