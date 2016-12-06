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

import java.util.Arrays;
import java.util.List;

import static io.github.theangrydev.businessflows.ApiFeatureStability.STABLE;
import static io.github.theangrydev.businessflows.ApiVersionHistory.*;

/**
 * A {@link HappyPath} is a {@link BusinessFlow} that is biased towards the result being {@link Happy}.
 */
@ApiFeature(since = VERSION_1_0_0, stability = STABLE)
public interface HappyPath<Happy, Sad> extends BusinessFlow<Happy, Sad>, WithOptional<Happy> {

    /**
     * Attempt an action that produces a {@link HappyPath}.
     *
     * @param happyPathAttempt The {@link Attempt} to execute
     * @param <Happy>          The type of happy object this {@link HappyPath} may represent
     * @param <Sad>            The type of sad object this {@link HappyPath} may represent
     * @return A {@link HappyPath} that is happy or sad or a technical failure on the inside
     */
    @ApiFeature(since = VERSION_2_5_0, stability = STABLE)
    static <Happy, Sad> HappyPath<Happy, Sad> happyPathAttempt(Attempt<? extends BusinessFlow<Happy, Sad>> happyPathAttempt) {
        try {
            return happyPathAttempt.attempt().ifHappy();
        } catch (Exception technicalFailure) {
            return technicalFailure(technicalFailure);
        }
    }

    /**
     * Attempt an action that produces a {@link Happy}.
     *
     * @param attempt The {@link Attempt} to execute
     * @param <Happy> The type of happy object this {@link HappyPath} may represent
     * @param <Sad>   The type of sad object this {@link HappyPath} may represent
     * @return A {@link HappyPath} that is either happy on the inside or a technical failure
     */
    @ApiFeature(since = VERSION_2_5_0, stability = STABLE)
    static <Happy, Sad> HappyPath<Happy, Sad> happyAttempt(Attempt<Happy> attempt) {
        try {
            return happyPath(attempt.attempt());
        } catch (Exception technicalFailure) {
            return technicalFailure(technicalFailure);
        }
    }

    /**
     * Attempt an action that produces a {@link Happy}, mapping any technical failure to a {@link Sad}.
     *
     * @param attempt        The {@link Attempt} to execute
     * @param failureMapping What to do if there is a technical failure during the {@link Attempt}
     * @param <Happy>        The type of happy object the resulting {@link HappyPath} may represent
     * @param <Sad>          The type of sad object the resulting {@link HappyPath} may represent
     * @return A {@link HappyPath} that is either happy on the inside, sad on the inside or a technical failure
     */
    @ApiFeature(since = VERSION_2_5_0, stability = STABLE)
    static <Happy, Sad> HappyPath<Happy, Sad> happyAttempt(Attempt<Happy> attempt, Mapping<Exception, Sad> failureMapping) {
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
     * Provides a {@link HappyPath} view over a known {@link Happy} object.
     *
     * @param happy   The happy object to initiate the flow with
     * @param <Happy> The type of happy object the resulting {@link HappyPath} may represent
     * @param <Sad>   The type of sad object the resulting {@link HappyPath} may represent
     * @return A {@link HappyPath} that is happy on the inside
     */
    @ApiFeature(since = VERSION_1_0_0, stability = STABLE)
    static <Happy, Sad> HappyPath<Happy, Sad> happyPath(Happy happy) {
        return new HappyCaseHappyPath<>(happy);
    }

    /**
     * Provides a {@link HappyPath} view over a known {@link Sad} object.
     *
     * @param sad     The sad object to initiate the flow with
     * @param <Happy> The type of happy object the resulting {@link HappyPath} may represent
     * @param <Sad>   The type of sad object the resulting {@link HappyPath} may represent
     * @return A {@link HappyPath} that is sad on the inside
     */
    @ApiFeature(since = VERSION_2_3_0, stability = STABLE)
    static <Happy, Sad> HappyPath<Happy, Sad> sadPath(Sad sad) {
        return new SadCaseHappyPath<>(sad);
    }

    /**
     * Provides a {@link HappyPath} view over a known {@link Exception} object.
     *
     * @param technicalFailure The technical failure object to initiate the flow with
     * @param <Happy>          The type of happy object the resulting {@link HappyPath} may represent
     * @param <Sad>            The type of sad object the resulting {@link HappyPath} may represent
     * @return A {@link HappyPath} that is a technical failure on the inside
     */
    @ApiFeature(since = VERSION_2_3_0, stability = STABLE)
    static <Happy, Sad> HappyPath<Happy, Sad> technicalFailure(Exception technicalFailure) {
        return new TechnicalFailureCaseHappyPath<>(technicalFailure);
    }

    /**
     * If the underlying business case is happy, then apply the given action, otherwise do nothing to the underlying case.
     *
     * @param action     The action to apply to an existing happy case
     * @param <NewHappy> The type of happy object that will be present after the action is applied to an existing happy object
     * @return The result of applying the action to the existing happy path, if applicable
     */
    @ApiFeature(since = VERSION_1_0_0, stability = STABLE)
    <NewHappy> HappyPath<NewHappy, Sad> then(Mapping<Happy, ? extends BusinessFlow<NewHappy, Sad>> action);

    /**
     * If the underlying business case is happy, then apply the given mapping, otherwise do nothing to the underlying case.
     *
     * @param mapping    The action to apply to an existing happy case
     * @param <NewHappy> The type of happy object that will be present after the mapping is applied to an existing happy object
     * @return The result of applying the mapping to the existing happy path, if applicable
     */
    @ApiFeature(since = VERSION_1_0_0, stability = STABLE)
    <NewHappy> HappyPath<NewHappy, Sad> map(Mapping<Happy, NewHappy> mapping);

    /**
     * Attempt an action that might fail and be mapped to a {@link Sad} object.
     *
     * @param actionThatMightFail The {@link ActionThatMightFail} to apply if the underlying business case is happy
     * @return The same {@link HappyPath} if the action did not fail; if the action failure then a {@link HappyPath} that is now sad inside
     */
    @ApiFeature(since = VERSION_1_0_0, stability = STABLE)
    HappyPath<Happy, Sad> attempt(ActionThatMightFail<Happy, Sad> actionThatMightFail);

    /**
     * Attempt several actions that might fail and be mapped to a {@link Sad} object.
     * The first action that fails will result in an early exit with the underlying sad case.
     * The first technical failure encountered will result in a technical failure overall.
     *
     * @param actionsThatMightFail The actions to apply if the underlying business case is happy
     * @return The same {@link HappyPath} if all the actions did not fail; if an action fails then a {@link HappyPath} that is now sad inside
     */
    HappyPath<Happy, Sad> attemptAll(List<? extends ActionThatMightFail<Happy, Sad>> actionsThatMightFail);

    /**
     * Helper method to turn an array of {@link ActionThatMightFail} into a list of {@link ActionThatMightFail}.
     *
     * @param actionsThatMightFail The actions that might fail
     * @param <Happy>              The type of happy object the list of {@link Validator} can validate
     * @param <Sad>                The type of validation error the list of {@link Validator} can produce
     * @return A list of {@link ActionThatMightFail}
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    static <Happy, Sad> List<ActionThatMightFail<Happy, Sad>> actions(ActionThatMightFail<Happy, Sad>... actionsThatMightFail) {
        return Arrays.asList(actionsThatMightFail);
    }

    /**
     * Take a look at the happy case (if there really is one).
     *
     * @param peek What to do if the underlying business case is happy
     * @return The same {@link HappyPath}
     */
    @ApiFeature(since = VERSION_1_0_0, stability = STABLE)
    HappyPath<Happy, Sad> peek(Peek<Happy> peek);

    /**
     * {@inheritDoc}
     */
    @Override
    default HappyPath<Happy, Sad> ifHappy() {
        return this;
    }
}
