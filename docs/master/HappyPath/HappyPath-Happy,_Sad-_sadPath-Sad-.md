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

## Happy attempt can fail
```java
RuntimeException technicalFailure = new RuntimeException();
Attempt<Happy> attempt = () -> {throw technicalFailure;};

HappyPath<Happy, Sad> happyPath = HappyPath.happyAttempt(attempt);

assertThat(happyPath.getTechnicalFailure()).isEqualTo(technicalFailure);
```
An attempt can fail and turn into a technical failure.

## Happy attempt can fail and map technical failure to a sad path
```java
Attempt<Happy> attempt = () -> {throw new RuntimeException("message");};
Mapping<Exception, Sad> sadMapping =  technicalFailure -> new Sad(technicalFailure.getMessage());

HappyPath<Happy, Sad> happyPath = HappyPath.happyAttempt(attempt, sadMapping);

assertThat(happyPath.getSad()).hasToString("message");
```
In this case, the technical failure is mapped to a sad path that contains the exception message.

## Happy attempt can introduce sad type via then
```java
Sad sad = new Sad();

HappyPath<Happy, Sad> happyPath = HappyPath.<Happy, Sad>happyAttempt(Happy::new)
    .then(happy -> HappyPath.sadPath(sad));

assertThat(happyPath.getSad()).isEqualTo(sad);
```
An attempt that was once happy can be turned into a sad path.
The `happyAttempt` method is playing the role of the Try monad here, but is lifted into the Either monad immediately, which is why the `Sad` type has to be specified up front.

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

