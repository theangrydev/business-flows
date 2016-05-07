package io.github.theangrydev.businessflows;

import com.codepoetics.ambivalence.Either;

import java.util.function.Function;

abstract class Projection<Sad, Happy> {

    final Either<Sad, Happy> either;
    final UncaughtExceptionHandler uncaughtExceptionHandler;
    final TechnicalFailure<Sad> technicalFailure;

    Projection(Either<Sad, Happy> either, UncaughtExceptionHandler uncaughtExceptionHandler, TechnicalFailure<Sad> technicalFailure) {
        this.either = either;
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        this.technicalFailure = technicalFailure;
    }

    <Argument> Either<Sad, Happy> tryToConsume(Argument argument, Peek<Argument> consumer) {
        return tryCatch(Either::ofLeft, () -> {consumer.peek(argument); return either; });
    }

    @FunctionalInterface
    interface SupplierThatMightThrowException<Result> {
        Result supply() throws Exception;
    }

    <Result> Result tryCatch(Function<Sad, Result> onException, SupplierThatMightThrowException<Result> something) {
        try {
            return something.supply();
        } catch (Exception exception) {
            uncaughtExceptionHandler.handle(exception);
            return onException.apply(technicalFailure.wrap(exception));
        }
    }
}
