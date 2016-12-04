---
title: HappyPath.happyAttempt
layout: post
---
These tests exist to prevent the failed solution to <a href="https://github.com/theangrydev/business-flows/issues/12">#12</a>
from being attempted again in the future without realising it :)

## [Happy attempt can introduce sad type via then](https://oss.sonatype.org/service/local/repositories/releases/archive/io/github/theangrydev/business-flows/10.1.0/business-flows-10.1.0-javadoc.jar/!/io/github/theangrydev/businessflows/HappyPath.html#happyAttempt-io.github.theangrydev.businessflows.Attempt-)
```java
HappyPath.<Happy, Sad>happyAttempt(Happy::new)
    .then(happy -> HappyPath.sadPath(new Sad()));
```
An attempt that was once happy can be turned into a sad path.
The `happyAttempt` method is playing the role of the Try monad here.
