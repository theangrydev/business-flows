---
title: boolean isTechnicalFailure()
layout: post
---
TODO: this is just a generated example

[javadoc](https://oss.sonatype.org/service/local/repositories/releases/archive/io/github/theangrydev/business-flows/10.3.0/business-flows-10.3.0-javadoc.jar/!/io/github/theangrydev/businessflows/BusinessFlow.html#isTechnicalFailure--) [usage tests](https://github.com/theangrydev/business-flows/blob/master/src/test/java/api/usage/businessflow/IsTechnicalFailureTest.java)

Added in version 10.3.0


## Returns true for technical failure
```java
TechnicalFailure<Happy, Sad> technicalFailure = TechnicalFailure.technicalFailure(new Exception());

assertThat(technicalFailure.isTechnicalFailure()).isTrue();
```

## Returns false for non technical failure
```java
HappyPath<Happy, Sad> happyPath = HappyPath.happyPath(new Happy());

assertThat(happyPath.isTechnicalFailure()).isFalse();
```

