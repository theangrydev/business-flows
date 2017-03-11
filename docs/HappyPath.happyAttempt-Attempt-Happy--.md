---
title: HappyPath.happyAttempt(Attempt&lt;Happy&gt;)
layout: post
---
TODO: this is just a generated example

[javadoc](https://oss.sonatype.org/service/local/repositories/releases/archive/io/github/theangrydev/business-flows/10.2.0/business-flows-10.2.0-javadoc.jar/!/io/github/theangrydev/businessflows/HappyPath.html#happyAttempt-io.github.theangrydev.businessflows.Attempt-) [usage tests](https://github.com/theangrydev/business-flows/blob/master/src/test/java/api/HappyAttemptApiTest.java)

Added in version 2.5.0


## Happy attempt can fail
```java
RuntimeException technicalFailure = new RuntimeException();
Attempt<Happy> attempt = () -> throw technicalFailure;;

HappyPath<Happy, Sad> happyPath = HappyPath.happyAttempt(attempt);

assertThat(happyPath.getTechnicalFailure()).isEqualTo(technicalFailure);
```
An attempt can fail and turn into a technical failure.

## Happy attempt can introduce sad type via then
```java
Sad sad = new Sad();

HappyPath<Happy, Sad> happyPath = HappyPath.<Happy, Sad>happyAttempt(Happy::new)
    .then(happy -> HappyPath.sadPath(sad));

assertThat(happyPath.getSad()).isEqualTo(sad);
```
An attempt that was once happy can be turned into a sad path.
The `happyAttempt` method is playing the role of the Try monad here, but is lifted into the Either monad immediately, which is why the `Sad` type has to be specified up front.

