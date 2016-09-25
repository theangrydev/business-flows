/*
 * Copyright 2016 Liam Williams <liam.williams@zoho.com>.
 *
 * This file is part of business-flows.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.theangrydev.businessflows;

/**
 * A {@link HappyPath} is a {@link BusinessFlow} that is biased towards the result being {@link Happy}.
 *
 * {@inheritDoc}
 */
public class HappyPath<Happy, Sad> extends BusinessFlow<Happy, Sad, Happy> {

    HappyPath(BusinessCase<Happy, Sad> businessCase) {
        super(BusinessCase::happyOptional, businessCase);
    }

    /**
     * @param happyPathAttempt The {@link Attempt} to execute
     * @param <Happy> The type of happy object this {@link HappyPath} may represent
     * @param <Sad> The type of sad object this {@link HappyPath} may represent
     * @return A {@link HappyPath} that is happy or sad or a technical failure on the inside
     */
    public static <Happy, Sad> HappyPath<Happy, Sad> happyPathAttempt(Attempt<HappyPath<Happy, Sad>> happyPathAttempt) {
        try {
            return happyPathAttempt.attempt();
        } catch (Exception technicalFailure) {
            return technicalFailure(technicalFailure);
        }
    }

    /**
     * @param attempt The {@link Attempt} to execute
     * @param <Happy> The type of happy object this {@link HappyPath} may represent
     * @param <Sad> The type of sad object this {@link HappyPath} may represent
     * @return A {@link HappyPath} that is either happy on the inside or a technical failure
     */
    public static <Happy, Sad> HappyPath<Happy, Sad> happyAttempt(Attempt<Happy> attempt) {
        try {
            return happyPath(attempt.attempt());
        } catch (Exception technicalFailure) {
            return technicalFailure(technicalFailure);
        }
    }

    /**
     * @param attempt The {@link Attempt} to execute
     * @param failureMapping What to do if there is a technical failure during the {@link Attempt}
     * @param <Happy> The type of happy object the resulting {@link HappyPath} may represent
     * @param <Sad> The type of sad object the resulting {@link HappyPath} may represent
     * @return A {@link HappyPath} that is either happy on the inside, sad on the inside or a technical failure
     */
    public static <Happy, Sad> HappyPath<Happy, Sad> happyAttempt(Attempt<Happy> attempt, Mapping<Exception, Sad> failureMapping) {
        try {
            return happyPath(attempt.attempt());
        } catch (Exception technicalFailure) {
            try {
                return sadPath(failureMapping.map(technicalFailure));
            } catch (Exception technicalFailureDuringFailureMapping) {
                return technicalFailure(technicalFailureDuringFailureMapping);
            }
        }
    }

    /**
     * @param happy The happy object to initiate the flow with
     * @param <Happy> The type of happy object the resulting {@link HappyPath} may represent
     * @param <Sad> The type of sad object the resulting {@link HappyPath} may represent
     * @return A {@link HappyPath} that is happy on the inside
     */
    public static <Happy, Sad> HappyPath<Happy, Sad> happyPath(Happy happy) {
        return new HappyPath<>(new HappyCase<>(happy));
    }

    /**
     * @param sad The sad object to initiate the flow with
     * @param <Happy> The type of happy object the resulting {@link HappyPath} may represent
     * @param <Sad> The type of sad object the resulting {@link HappyPath} may represent
     * @return A {@link HappyPath} that is sad on the inside
     */
    public static <Happy, Sad> HappyPath<Happy, Sad> sadPath(Sad sad) {
        return new HappyPath<>(new SadCase<>(sad));
    }

    /**
     * @param technicalFailure The technical failure object to initiate the flow with
     * @param <Happy> The type of happy object the resulting {@link HappyPath} may represent
     * @param <Sad> The type of sad object the resulting {@link HappyPath} may represent
     * @return A {@link HappyPath} that is a technical failure on the inside
     */
    public static <Happy, Sad> HappyPath<Happy, Sad> technicalFailure(Exception technicalFailure) {
        return new HappyPath<>(new TechnicalFailureCase<>(technicalFailure));
    }

    /**
     * If the underlying business case is happy, then apply the given action, otherwise do nothing to the underlying case.
     *
     * @param action The action to apply to an existing happy case
     * @param <NewHappy> The type of happy object that will be present after the action is applied to an existing happy object
     * @return The result of applying the action to the existing happy path, if applicable
     */
    public <NewHappy> HappyPath<NewHappy, Sad> then(Mapping<Happy, BusinessFlow<NewHappy, Sad, ?>> action) {
        return join(happy -> action.map(happy).ifHappy(), HappyPath::sadPath, HappyPath::technicalFailure);
    }

    /**
     * If the underlying business case is happy, then apply the given mapping, otherwise do nothing to the underlying case.
     *
     * @param mapping The action to apply to an existing happy case
     * @param <NewHappy> The type of happy object that will be present after the mapping is applied to an existing happy object
     * @return The result of applying the mapping to the existing happy path, if applicable
     */
    public <NewHappy> HappyPath<NewHappy, Sad> map(Mapping<Happy, NewHappy> mapping) {
        return then(mapping.andThen(HappyPath::happyPath));
    }

    /**
     * @param actionThatMightFail The {@link ActionThatMightFail} to apply if the underlying business case is happy
     * @return The same {@link HappyPath} if the action did not fail; if the action failure then a {@link HappyPath} that is now sad inside
     */
    public HappyPath<Happy, Sad> attempt(ActionThatMightFail<Happy, Sad> actionThatMightFail) {
        return then(actionThatMightFail::attemptHappyPath);
    }

    /**
     * Take a look at the happy case (if there really is one).
     *
     * @param peek What to do if the underlying business case is happy
     * @return The same {@link HappyPath}
     */
    public HappyPath<Happy, Sad> peek(Peek<Happy> peek) {
        return then(happy -> {
            peek.peek(happy);
            return this;
        });
    }
}
