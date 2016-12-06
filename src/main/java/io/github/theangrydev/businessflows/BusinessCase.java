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

import java.util.function.Consumer;
import java.util.function.Function;

import static io.github.theangrydev.businessflows.ApiFeatureStability.STABLE;
import static io.github.theangrydev.businessflows.ApiVersionHistory.*;
import static java.lang.String.format;

/**
 * A {@link BusinessCase} is either a {@link HappyCase}, a {@link SadCase} or a {@link TechnicalFailure}.
 *
 * @param <Happy> The type of happy object this case may represent
 * @param <Sad>   The type of sad object this case may represent
 */
@ApiFeature(since = VERSION_1_0_0, stability = STABLE)
interface BusinessCase<Happy, Sad> {

    /**
     * Turn this {@link BusinessFlow} into a {@link PotentialFailure} that will be a success if the business case is a
     * success and otherwise will map the business case to a failure.
     *
     * @param technicalFailureMapping The mapping to apply to turn an existing technical failure into a {@link Sad}
     * @return A {@link PotentialFailure} that will be a failure if the business case is sad or a technical failure
     */
    @ApiFeature(since = VERSION_6_1_0, stability = STABLE)
    PotentialFailure<Sad> toPotentialFailure(Function<Exception, Sad> technicalFailureMapping);

    /**
     * Join to a common result type. No matter what the {@link BusinessCase} actually is, the result type is the same.
     *
     * @param happyJoiner            What to do if this is a {@link HappyCase}
     * @param sadJoiner              What to do if this is a {@link SadCase}
     * @param technicalFailureJoiner What to do if this is a {@link TechnicalFailureCase}
     * @param <Result>               The type of the result
     * @return The result after applying the joiner that corresponds to the underlying business case
     */
    @ApiFeature(since = VERSION_2_7_0, stability = STABLE)
    <Result> Result join(Mapping<Happy, Result> happyJoiner, Mapping<Sad, Result> sadJoiner, Function<Exception, Result> technicalFailureJoiner);

    /**
     * Same as {@link #join(Mapping, Mapping, Function)} but if the {@link BusinessCase} is a
     * {@link TechnicalFailureCase}, then the underlying exception will be thrown instead of joined.
     *
     * @param happyJoiner What to do if this is a {@link HappyCase}
     * @param sadJoiner   What to do if this is a {@link SadCase}
     * @param <Result>    The type of the result
     * @return The result after applying the joiner that corresponds to the underlying business case
     * @throws Exception If this is a {@link TechnicalFailureCase}.
     */
    @ApiFeature(since = VERSION_4_0_0, stability = STABLE)
    <Result> Result joinOrThrow(Mapping<Happy, Result> happyJoiner, Mapping<Sad, Result> sadJoiner) throws Exception;

    /**
     * Same as {@link #consume(Peek, Peek, Consumer)} but if the {@link BusinessCase} is a
     * {@link TechnicalFailureCase}, then the underlying exception will be thrown instead of joined.
     *
     * @param happyConsumer What to do if this is a {@link HappyCase}
     * @param sadConsumer   What to do if this is a {@link SadCase}
     * @throws Exception If this is a {@link TechnicalFailureCase}.
     */
    @ApiFeature(since = VERSION_8_3_0, stability = STABLE)
    void consumeOrThrow(Peek<Happy> happyConsumer, Peek<Sad> sadConsumer) throws Exception;

    /**
     * Consume the underlying {@link BusinessCase}.
     *
     * @param happyConsumer            What to do if this is a {@link HappyCase}
     * @param sadConsumer              What to do if this is a {@link SadCase}
     * @param technicalFailureConsumer What to do if this is a {@link TechnicalFailureCase}
     */
    @ApiFeature(since = VERSION_8_3_0, stability = STABLE)
    void consume(Peek<Happy> happyConsumer, Peek<Sad> sadConsumer, Consumer<Exception> technicalFailureConsumer);

    /**
     * Same as {@link #join(Mapping, Mapping, Function)} but if the {@link BusinessCase} is a {@link TechnicalFailureCase},
     * then the underlying exception will be thrown as a {@link RuntimeException} instead of joined.
     *
     * @param happyJoiner What to do if the underlying business case is a happy case
     * @param sadJoiner   What to do if the underlying business case is a sad case
     * @param <Result>    The type of the result
     * @return The result after applying the joiner that corresponds to the underlying business case
     * @throws IllegalStateException If this is a {@link TechnicalFailureCase} or there is a failure when joining.
     */
    @ApiFeature(since = VERSION_6_1_0, stability = STABLE)
    default <Result> Result join(Mapping<Happy, Result> happyJoiner, Mapping<Sad, Result> sadJoiner) throws IllegalStateException {
        try {
            return joinOrThrow(happyJoiner, sadJoiner);
        } catch (Exception technicalFailure) {
            throw new IllegalStateException(format("Exception caught when joining. Business case is: '%s'.", this), technicalFailure);
        }
    }
}
