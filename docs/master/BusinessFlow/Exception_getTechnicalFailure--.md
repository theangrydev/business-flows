---
title: Exception getTechnicalFailure()
layout: post
---
TODO: this is just a generated example

[javadoc](https://oss.sonatype.org/service/local/repositories/releases/archive/io/github/theangrydev/business-flows/10.3.0/business-flows-10.3.0-javadoc.jar/!/io/github/theangrydev/businessflows/BusinessFlow.html#getTechnicalFailure--) [usage tests](https://github.com/theangrydev/business-flows/blob/master/src/test/java/api/usage/businessflow/GetTechnicalFailureTest.java)

Added in version 10.3.0


## Can get technical failure
```java
Exception exception = new Exception();

TechnicalFailure<Happy, Sad> technicalFailure = TechnicalFailure.technicalFailure(exception);

assertThat(technicalFailure.getTechnicalFailure()).isEqualTo(exception);
```

## Attempting to get technical failure that is not there throws an illegal state exception
```java
HappyPath<Happy, Sad> happyPath = HappyPath.happyPath(new Happy("name"));

assertThatThrownBy(happyPath::getTechnicalFailure)
    .hasMessage("Not present. This is: 'Happy: name'.")
    .isInstanceOf(IllegalStateException.class);
```

