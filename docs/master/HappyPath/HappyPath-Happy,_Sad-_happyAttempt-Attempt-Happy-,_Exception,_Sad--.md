---
title: HappyPath&lt;Happy, Sad&gt; happyAttempt(Attempt&lt;Happy&gt;, Exception, Sad&gt;)
layout: post
---
TODO: this is just a generated example

[javadoc](https://oss.sonatype.org/service/local/repositories/releases/archive/io/github/theangrydev/business-flows/10.3.0/business-flows-10.3.0-javadoc.jar/!/io/github/theangrydev/businessflows/HappyPath.html#happyAttempt-io.github.theangrydev.businessflows.Attempt-io.github.theangrydev.businessflows.Mapping-) [usage tests](https://github.com/theangrydev/business-flows/blob/master/src/test/java/api/usage/happypath/HappyAttemptWithFailureMappingTest.java)

Added in version 2.5.0


## Happy attempt can fail and map technical failure to a sad path
```java
Attempt<Happy> attempt = () -> {throw new RuntimeException("message");};
Mapping<Exception, Sad> sadMapping =  technicalFailure -> new Sad(technicalFailure.getMessage());

HappyPath<Happy, Sad> happyPath = HappyPath.happyAttempt(attempt, sadMapping);

assertThat(happyPath.getSad()).hasToString("message");
```
In this case, the technical failure is mapped to a sad path that contains the exception message.

