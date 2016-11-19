[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.theangrydev/business-flows/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.github.theangrydev/business-flows)
[![Javadoc](http://javadoc-badge.appspot.com/io.github.theangrydev/business-flows.svg?label=javadoc)](http://javadoc-badge.appspot.com/io.github.theangrydev/business-flows)
[![Gitter](https://badges.gitter.im/business-flows/Lobby.svg)](https://gitter.im/business-flows/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

[![Build Status](https://travis-ci.org/theangrydev/business-flows.svg?branch=master)](https://travis-ci.org/theangrydev/business-flows)
[![codecov](https://codecov.io/gh/theangrydev/business-flows/branch/master/graph/badge.svg)](https://codecov.io/gh/theangrydev/business-flows)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e6bc8f1d30bf43fd888014fcfa06302d)](https://www.codacy.com/app/liam-williams/business-flows?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=theangrydev/business-flows&amp;utm_campaign=Badge_Grade)
[![codebeat badge](https://codebeat.co/badges/6271d27e-7190-4e82-a6f5-660780e740f2)](https://codebeat.co/projects/github-com-theangrydev-business-flows)
[![Quality Gate](https://sonarqube.com/api/badges/gate?key=io.github.theangrydev:business-flows)](https://sonarqube.com/dashboard/index/io.github.theangrydev:business-flows)

# Business Flows

A combination of the Try monad and the Either monad, to help tame complex business flows

[Example Usage](https://github.com/tjheslin1/DogFoster/blob/master/src/main/java/io/github/tjheslin1/DogShelter.java) (thanks [@tjheslin1](https://github.com/tjheslin1))

## Frequently asked questions
### What is it?
A small Java 8 library that can help you to model a 3-part (happy/sad/exception) business flow. No more try-catch blocks if you don't want to use them!

### When should I use it?
If you have a flow that fits into the "two track" model, as spoken about by Scott W in his [Railyway Oriented Programming](https://fsharpforfunandprofit.com/rop) model, which this library owes a lot to. This library has 3 tracks instead of 2; the third is supposed to be reserved for "stuff that is never supposed to happen" in the form of uncaught exceptions.

### When should I not use it?
There is no point in using this if your flows are "all or nothing". If there are no expected sad paths and all failures are exceptional circumstances, then this is not a good fit. The Try monad is a better fit. You could use e.g. [better-java-monads](https://github.com/jasongoodwin/better-java-monads/blob/master/src/main/java/com/jasongoodwin/monads/Try.java) instead.

### How to get it?
```xml
<dependency>
    <groupId>io.github.theangrydev</groupId>
    <artifactId>business-flows</artifactId>
    <version>9.1.0</version>
</dependency>
```

## Releases
### 9.1.0
* Added the `HappyPath.happyAttempt` method that took an `Attempt<Happy>` parameter back again. I found a better solution than completely removing it, which is to just prevent using the `SadPath` methods in a flow created in this way, using a special `NoSad` sad path type that is package private (closes [#12](https://github.com/theangrydev/business-flows/issues/12)). See the migration advice [here](https://github.com/theangrydev/business-flows/wiki/9.1.0-Migration-Advice)
 
### 9.0.0
* Removed the `HappyPath.happyAttempt` method that took an `Attempt<Happy>` parameter (closes [#12](https://github.com/theangrydev/business-flows/issues/12)). This change is not backwards compatible. The recommended alternative is described in the migration advice, which can be found [here](https://github.com/theangrydev/business-flows/wiki/9.0.0-Migration-Advice)

### 8.3.0
* Add methods `consume` and `consumeOrThrow` to end a flow by performing some action that ends in a void (closes [#8](https://github.com/theangrydev/business-flows/issues/8))

### 8.2.0
* Add methods to help with multiple return style: `isHappy`, `isSad`, `getHappy` and `getSad` (closes [#10](https://github.com/theangrydev/business-flows/issues/10))

### 8.1.1
* `throwItAsARuntimeException` and `throwIt` should not be void (closes [#5](https://github.com/theangrydev/business-flows/issues/5))

### 8.0.0
* Removed the `Bias` parameter from `BusinessFlow` to make it more viable to use as an unbiased return type, instead of having to wildcard the types as `<Happy, Sad, ?>`. This is a breaking change.
* Made the `then` methods accept `? extends BusinessFlow`.

### 7.6.0
* Exposed some methods on `PotentialFailure` to make it easier to test when it is the return type of a method. This involved extracting a `WithOptional` interface that is used by `PotentialFailure` and `BusinessFlow`.

### 7.5.0
* Added helper method `HappyPath.actions` that turns an array of `ActionThatMightFail` into a list. This is a workaround for the fact that interfaces can't have `@SafeVarargs` and so some methods accept a list when they would ideally accept varargs

### 7.4.0
* Added `HappyPath.attemptAll` convenience method that is equivalent to multiple chained `attempt` calls

### 7.3.0
* Added method `TechnicalFailure.throwItAsRuntimeException` that will throw a `RuntimeException` if the business case is a technical failure

### 7.2.0
* Added method `TechnicalFailure.throwIt` that will throw an `Exception` if the business case is a technical failure

### 7.0.0
* Changed `Validator` so that it produces just one failure not a list of failures
* `FieldValidator` now adapts just one `Validator` not a list of them to match the `Validator` change
* Renamed `ValidationPath` methods from `validate*` to `validateAll*` to make it clear that they run all the validators not just until the first failure 

### 6.2.0
* `TechnicalFailureCase.toString` now includes the stack trace

### 6.1.0
* Added method `BusinessCase.toPotentialFalure` that turns a `BusinessCase` into a `PotentialFailure`

### 6.0.0
* `ValidationPath` now has a `SadAggregate` type parameter that defaults to `List<Sad>` and can be used to map validation errors into an aggregate. ValidationPath now has a SadAggregate type parameter that defaults to List<Sad> and can be used to map validation errors into an aggregate. There are corresponding `validateInto` methods in `ValidationPath` that allow specifying a `Mapping` to a `SadAggregate` and `validate` methods that default to `List<Sad>`. This change is not backwards compatible

### 5.1.2
* Helper methods for `FieldValidator` to cope with the case that a common field name should be passed to validators so they can use the name in the validation message

### 5.0.0
* Validation revamp. Helper `FieldValidator` for field validation, a new `Validator` type, `PotentialFailure.failures` helper method and `ValidationPath` helper methods. This change is not backwards compatible

### 4.0.3
* Updated javadoc and license headers

### 4.0.1
* Inline methods to make debugging easier (closes [#1](https://github.com/theangrydev/business-flows/issues/2))
* Removed varargs methods from `ValidationPath`. This change is not backwards compatible
* Removed `andThen` methods from `Attempt` and `Mapping`. This change is not backwards compatible

### 3.1.1
* Optimized overrides for ifHappy etc (closes [#2](https://github.com/theangrydev/business-flows/issues/2))

### 3.1.0
* Allow `? extends ActionThatMightFail<Happy, Sad>` in the `ValidationPath`

### 3.0.2
* Made `PotentialFailure.toHappyPath` package private since it is only supposed to be used internally

### 3.0.1
* License headers updated

### 3.0.0
* Using a new class `PotentialFailure` to represent the result of an `ActionThatMightFail` instead of an `Optional`. This change is not backwards compatible 

### 2.7.1
* Updated javadoc

### 2.7.0
* If a technical failure occurs while joining to happy or sad, it is joined to a technical failure instead

### 2.6.0
* Unchecked version of `join` with only happy and sad joiners has taken the `join` name. The checked version is called `joinOrThrow` now

### 2.5.0
* Recovery methods no longer need to be e.g. `exception -> sad`, they can choose to ignore the parameter and just supply a value, e.g. `() -> sad`

### 2.4.0
* Added `HappyPath.happyPathAttempt` to cover the case where the entry point to a flow is a method that could e.g. return either `HappyPath.happyPath` or `SadPath.sadPath` and may also throw an uncaught exception

### 2.3.0
* Made all the static factory methods for `HappyPath`, `SadPath` and `TechnicalFailure` public so that it is possible to do e.g. `HappyPath.sadPath(sad)` to get a happy biased view of a sad path without having to do `SadPath.sadPath(sad).ifHappy()`

### 2.2.0
* Updated javadoc

### 2.1.0
* `HappyAttempt` now has an exception to technical failure factory method and an exception to sad path factory method

### 2.0.0
* Switched order from `<Sad, Happy>` to `<Happy, Sad>` because it is more intuitive to users. This change is not backwards compatible

### 1.1.0
* Added another `join` method in which you can omit the technical failure mapping and allow it to be thrown as an `Exception`
* Reduced visibility of methods that should have been `private`

### 1.0.1
* Changed `orElseThrow` so that it throws `X extends Exception` rather than `X extends Throwable`
* Changed `get` failure message wording

### 1.0.0
* Initial stab at business-flows
