package io.github.theangrydev.businessflows;

import java.util.Optional;

@FunctionalInterface
public interface ActionThatMightFail<Sad, Happy> {
    Optional<Sad> attempt(Happy happy) throws Exception;
}
