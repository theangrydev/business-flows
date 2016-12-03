These tests exist to prevent the failed solution to https://github.com/theangrydev/business-flows/issues/12 from
being attempted again in the future without realising it :)

## Happy attempt return type can be used outside of the internal package
```java
usageOfTheReturnType();
```
Here, the thing is the doodad.

## Happy attempt can introduce sad type via then
```java
usageOfTheReturnType().then( happy -> HappyPath.sadPath(new Sad()));
```

## Hasaappy attempt return type can be used outside of the internal package
```java
usageOfTheReturnType();
```
Here, the thing is the doodad.
