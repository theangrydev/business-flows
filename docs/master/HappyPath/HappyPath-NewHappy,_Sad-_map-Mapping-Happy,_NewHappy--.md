---
title: HappyPath&lt;NewHappy, Sad&gt; map(Mapping&lt;Happy, NewHappy&gt;)
layout: post
---
TODO: this is just a generated example

[javadoc](https://oss.sonatype.org/service/local/repositories/releases/archive/io/github/theangrydev/business-flows/10.3.0/business-flows-10.3.0-javadoc.jar/!/io/github/theangrydev/businessflows/HappyPath.html#map-io.github.theangrydev.businessflows.Mapping-) [usage tests](https://github.com/theangrydev/business-flows/blob/master/src/test/java/api/usage/happypath/MapTest.java)

Added in version 1.0.0


## If the underlying case is happy then the mapping is applied
```java
Happy happy = new Happy("name");
HappyPath<Happy, Sad> happyPath = HappyPath.happyPath(happy);

HappyPath<String, Sad> mapped = happyPath.map(Happy::toString);

assertThat(mapped.getHappy()).isEqualTo("name");
```

## Exceptions thrown during mapping are turned into technical failures
```java
Exception uncaught = new Exception("boom");
Mapping<Happy, String> mapping =  happy -> {throw uncaught;};
HappyPath<Happy, Sad> happyPath = HappyPath.happyPath(new Happy());

HappyPath<String, Sad> mapped = happyPath.map(mapping);

assertThat(mapped.getTechnicalFailure()).isEqualTo(uncaught);
```

## If the underlying case is sad then the mapping is not applied
```java
Sad sad = new Sad();
HappyPath<Happy, Sad> happyPath = HappyPath.sadPath(sad);

HappyPath<String, Sad> mapped = happyPath.map(Happy::toString);

assertThat(mapped.getSad()).isEqualTo(sad);
```

## If the underlying case is a technical failure then the mapping is not applied
```java
Exception technicalFailure = new Exception();
HappyPath<Happy, Sad> happyPath = HappyPath.technicalFailure(technicalFailure);

HappyPath<String, Sad> mapped = happyPath.map(Happy::toString);

assertThat(mapped.getTechnicalFailure()).isEqualTo(technicalFailure);
```

