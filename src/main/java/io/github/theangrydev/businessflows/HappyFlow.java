package io.github.theangrydev.businessflows;

import io.github.theangrydev.businessflows.BusinessFlowProjection.SupplierThatMightThrowException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static io.github.theangrydev.businessflows.ValidationFlow.validationSuccess;
import static io.github.theangrydev.businessflows.ValidationFlow.validationFailed;
import static io.github.theangrydev.businessflows.ValidationFlow.technicalFailureDuringValidation;
import static java.lang.String.format;

public class HappyFlow<Happy> {

    private final Happy happy;
    private final Exception technicalFailure;

    private HappyFlow(Happy happy, Exception technicalFailure) {
        this.happy = happy;
        this.technicalFailure = technicalFailure;
    }

    public static <Happy> HappyFlow<Happy> happyAttempt(HappyAttempt<Happy> happyAttempt) {
        try {
            return happyPath(happyAttempt.happy());
        } catch (Exception technicalFailure) {
            return technicalFailure(technicalFailure);
        }
    }

    public static <Happy> HappyFlow<Happy> happyPath(Happy happyPath) {
        return new HappyFlow<>(happyPath, null);
    }

    public static <Happy> HappyFlow<Happy> technicalFailure(Exception technicalFailure) {
        return new HappyFlow<>(null, technicalFailure);
    }

    @SafeVarargs
    public final <Sad> ValidationFlow<Sad, Happy> validate(ActionThatMightFail<Sad, Happy>... validators) {
        return validate(Arrays.asList(validators));
    }

    public final <Sad> ValidationFlow<Sad, Happy> validate(List<ActionThatMightFail<Sad, Happy>>validators) {
        return join(happy -> validate(happy, validators), ValidationFlow::technicalFailureDuringValidation);
    }

    public <Result> Result join(Function<Happy, Result> happyJoiner, Function<Exception, Result> technicalFailureJoiner) {
        if (happy != null) {
            return happyJoiner.apply(happy);
        } else if (technicalFailure != null) {
            return technicalFailureJoiner.apply(technicalFailure);
        } else {
            throw new IllegalStateException("Impossible scenario. There must always be a happy value or technical failure.");
        }
    }

    public Optional<Exception> ifTechnicalFailure() {
        return Optional.ofNullable(technicalFailure);
    }

    private <Sad> ValidationFlow<Sad, Happy> validate(Happy happy, List<ActionThatMightFail<Sad, Happy>> validators) {
        List<Sad> validationFailures = new ArrayList<>();
        for (ActionThatMightFail<Sad, Happy> validator : validators) {
            BusinessFlow<Sad, Happy> attempt =  attempt(validator);
            if (attempt.technicalFailure != null) {
                return technicalFailureDuringValidation(attempt.technicalFailure);
            }
            attempt.ifSad().peek(validationFailures::add);
        }
        if (validationFailures.isEmpty()) {
            return validationSuccess(happy);
        } else {
            return validationFailed(validationFailures);
        }
    }

    public <Sad, NewHappy, Result extends BusinessFlow<Sad, NewHappy>> BusinessFlow<Sad, NewHappy> then(Mapping<Happy, Result> action) {
        return new BusinessFlow<Sad, Happy>(null, happy, technicalFailure).then(action);
    }

    //TODO: technical failure projection
//    public HappyFlow<Happy> recover(Mapping<Exception, Happy> recovery) {
//        return Optional.ofNullable(technicalFailure).map(exception -> tryRecovery(exception, recovery.andThen(HappyFlow::happyPath))).orElse(HappyFlow.technicalFailure(technicalFailure));
//    }
//
//    private <NewHappy> HappyFlow<NewHappy> tryRecovery(Exception exception, Mapping<Exception, HappyFlow<NewHappy>> recovery) {
//        try {
//            return recovery.map(exception);
//        } catch (Exception technicalFailureDuringMapping) {
//            return HappyFlow.technicalFailure(technicalFailureDuringMapping);
//        }
//    }



    public <NewHappy> HappyFlow<NewHappy> map(Mapping<Happy, NewHappy> mapping) {
        return flatMap(mapping.andThen(happy -> new HappyFlow<>(happy, technicalFailure)));
    }

    private <NewHappy> HappyFlow<NewHappy> flatMap(Mapping<Happy, HappyFlow<NewHappy>> action) {
        return happyPath().map(happy -> tryHappyAction(happy, action)).orElse(HappyFlow.technicalFailure(technicalFailure));
    }

    public <Sad> BusinessFlow<Sad, Happy> attempt(ActionThatMightFail<Sad, Happy> actionThatMightFail) {
        return new BusinessFlow<Sad, Happy>(null, happy, technicalFailure).attempt(actionThatMightFail);
    }

    public HappyFlow<Happy> peek(Peek<Happy> peek) {
        return flatMap(happy -> {
            peek.peek(happy);
            return this;
        });
    }

    public Happy get() {
        return happyPath().orElseThrow(() -> new RuntimeException(format("Happy path not present. Exception was '%s'", technicalFailure)));
    }

    private <NewHappy> HappyFlow<NewHappy> tryHappyAction(Happy happy, Mapping<Happy, HappyFlow<NewHappy>> action) {
        try {
            return action.map(happy);
        } catch (Exception technicalFailureDuringAction) {
            return HappyFlow.technicalFailure(technicalFailureDuringAction);
        }
    }

    private Optional<Happy> happyPath() {
        return Optional.ofNullable(happy);
    }

}
