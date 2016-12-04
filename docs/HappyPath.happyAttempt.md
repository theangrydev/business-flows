---
title: HappyPath.happyAttempt(io.github.theangrydev.businessflows.Attempt<Happy>)
layout: post
---
[happyAttempt javadoc](https://oss.sonatype.org/service/local/repositories/releases/archive/io/github/theangrydev/business-flows/10.1.6/business-flows-10.1.6-javadoc.jar/!/io/github/theangrydev/businessflows/HappyPath.html#happyAttempt-io.github.theangrydev.businessflows.Attempt-)

[happyAttempt usage tests](https://github.com/theangrydev/business-flows/blob/business-flows-10.1.6/src/test/java/api/HappyAttemptApiTest.java)

These tests exist to prevent the failed solution to <a href="https://github.com/theangrydev/business-flows/issues/12">#12</a>
from being attempted again in the future without realising it :)

## Happy attempt can introduce sad type via then
```java
Sad sad = new Sad();

HappyPath<Happy, Sad> happyPath = HappyPath.<Happy, Sad>happyAttempt(Happy::new)
    .then(happy -> HappyPath.sadPath(sad));

assertThat(happyPath.getSad()).isEqualTo(sad);
```
An attempt that was once happy can be turned into a sad path.
The `happyAttempt` method is playing the role of the Try monad here, but is lifted into the Either monad immediately, which is why the `Sad` type has to be specified up front.

