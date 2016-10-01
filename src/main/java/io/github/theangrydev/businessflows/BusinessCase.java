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

import java.util.function.Function;

import static java.lang.String.format;

/**
 * A {@link BusinessCase} is either a {@link HappyCase}, a {@link SadCase} or a {@link TechnicalFailure}.
 *
 * @param <Happy> The type of happy object this case may represent
 * @param <Sad> The type of sad object this case may represent
 */
interface BusinessCase<Happy, Sad> {

    /**
     * Join to a common result type. No matter what the {@link BusinessCase} actually is, the result type is the same.
     *
     * @param happyJoiner What to do if this is a {@link HappyCase}
     * @param sadJoiner What to do if this is a {@link SadCase}
     * @param technicalFailureJoiner What to do if this is a {@link TechnicalFailureCase}
     * @param <Result> The type of the result
     * @return The result after applying the joiner that corresponds to the underlying business case
     */
    <Result> Result join(Mapping<Happy, Result> happyJoiner, Mapping<Sad, Result> sadJoiner, Function<Exception, Result> technicalFailureJoiner);

    /**
     * Same as {@link #join(Mapping, Mapping, Function)} but if the {@link BusinessCase} is a
     * {@link TechnicalFailureCase}, then the underlying exception will be thrown instead of joined.
     *
     * @param happyJoiner What to do if this is a {@link HappyCase}
     * @param sadJoiner What to do if this is a {@link SadCase}
     * @param <Result> The type of the result
     * @return The result after applying the joiner that corresponds to the underlying business case
     * @throws Exception If this is a {@link TechnicalFailureCase}.
     */
    <Result> Result joinOrThrow(Mapping<Happy, Result> happyJoiner, Mapping<Sad, Result> sadJoiner) throws Exception;

    /**
     * Same as {@link #join(Mapping, Mapping, Function)} but if the {@link BusinessCase} is a {@link TechnicalFailureCase},
     * then the underlying exception will be thrown as a {@link RuntimeException} instead of joined.
     *
     * @param happyJoiner What to do if the underlying business case is a happy case
     * @param sadJoiner What to do if the underlying business case is a sad case
     * @param <Result> The type of the result
     * @return The result after applying the joiner that corresponds to the underlying business case
     * @throws RuntimeException If this is a {@link TechnicalFailureCase} or there is a failure when joining.
     */
    default <Result> Result join(Mapping<Happy, Result> happyJoiner, Mapping<Sad, Result> sadJoiner) throws RuntimeException {
        try {
            return joinOrThrow(happyJoiner, sadJoiner);
        } catch (Exception e) {
            throw new RuntimeException(format("Exception caught when joining. Business case is: '%s'.", this), e);
        }
    }
}
