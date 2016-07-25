package io.github.theangrydev.businessflows;

import java.util.Optional;
import java.util.function.Function;

public interface BusinessCase<Sad, Happy> {
    <Result> Result join(Function<Sad, Result> sadJoiner, Function<Happy, Result> happyJoiner, Function<Exception, Result> technicalFailureJoiner);
    Optional<Happy> happyOptional();
    Optional<Sad> sadOptional();
    Optional<Exception> technicalFailureOptional();
}
