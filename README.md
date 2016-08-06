# business-flows
A combination of the Try monad and the Either monad, to help tame complex business flows

## Frequently asked questions
### What is it?
A small Java 8 library that can help you to model a 3-part (happy/sad/exception) business flow. No more try-catch blocks if you don't want to use them!

### When should I use it?
If you have a flow that fits into the "two track" model, as spoken about by Scott W in his [Railyway Oriented Programming](https://fsharpforfunandprofit.com/rop) model, which this library owes a lot to.

### When should I not use it?
There is no point in using this if your flows are "all or nothing". If there are no expected sad paths and all failures are exceptional circumstances, then this is not a good fit. The Try monad is a better fit. You could use e.g. [better-java-monads](https://github.com/jasongoodwin/better-java-monads/blob/master/src/main/java/com/jasongoodwin/monads/Try.java) instead.

### How to get it?
```xml
<dependency>
    <groupId>io.github.theangrydev</groupId>
    <artifactId>business-flows</artifactId>
    <version>2.2.0</version>
</dependency>
```
