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

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * A {@link BusinessFlow} is a biased view of a {@link BusinessCase}.
 * This is the base {@link BusinessFlow} that contains operations that are common to all the biased views.
 *
 * @param <Happy> The type of happy object this {@link BusinessFlow} may represent
 * @param <Sad> The type of sad object this {@link BusinessFlow} may represent
 * @param <Bias> The type of bias (happy, sad or technical failure) this {@link BusinessFlow} has
 */
public class BusinessFlow<Happy, Sad, Bias> {

    private final Function<BusinessCase<Happy, Sad>, Optional<Bias>> bias;
    private final BusinessCase<Happy, Sad> businessCase;

    BusinessFlow(Function<BusinessCase<Happy, Sad>, Optional<Bias>> bias, BusinessCase<Happy, Sad> businessCase) {
        this.bias = bias;
        this.businessCase = businessCase;
    }

    /**
     * Join to a common result type. No matter what the underlying business case actually is, the result type is the same.
     *
     * @param happyJoiner What to do if the underlying business case is a happy case
     * @param sadJoiner What to do if the underlying business case is a sad case
     * @param technicalFailureJoiner What to do if the underlying business case is a technical failure
     * @param <Result> The type of the result
     * @return The result after applying the joiner that corresponds to the underlying business case
     * @throws RuntimeException If there is a failure when joining
     */
    public <Result> Result join(Mapping<Happy, Result> happyJoiner, Mapping<Sad, Result> sadJoiner, Function<Exception, Result> technicalFailureJoiner) {
        return businessCase.join(happyJoiner, sadJoiner, technicalFailureJoiner);
    }

    /**
     * Same as {@link #join(Mapping, Mapping, Function)} but if the business case is a technical failure, then the
     * underlying exception will be thrown as the cause of a {@link RuntimeException} instead of joined.
     *
     * @param happyJoiner What to do if the underlying business case is a happy case
     * @param sadJoiner What to do if the underlying business case is a sad case
     * @param <Result> The type of the result
     * @return The result after applying the joiner that corresponds to the underlying business case
     * @throws RuntimeException If this is a {@link TechnicalFailureCase} or there is a failure when joining.
     */
    public <Result> Result join(Mapping<Happy, Result> happyJoiner, Mapping<Sad, Result> sadJoiner) throws RuntimeException {
        try {
            return businessCase.join(happyJoiner, sadJoiner);
        } catch (Exception e) {
            throw new RuntimeException(format("Exception caught when joining. Business case is: '%s'.", businessCase), e);
        }
    }

    /**
     * Same as {@link #join(Mapping, Mapping, Function)} but if the business case is a technical failure, then the
     * underlying exception will be thrown instead of joined.
     *
     * @param happyJoiner What to do if the underlying business case is a happy case
     * @param sadJoiner What to do if the underlying business case is a sad case
     * @param <Result> The type of the result
     * @return The result after applying the joiner that corresponds to the underlying business case
     * @throws Exception If this is a {@link TechnicalFailureCase}.
     * @throws RuntimeException If there is a failure when joining
     */
    public <Result> Result joinOrThrow(Mapping<Happy, Result> happyJoiner, Mapping<Sad, Result> sadJoiner) throws Exception {
        return businessCase.join(happyJoiner, sadJoiner);
    }

    /**
     * @return A view of the underlying business case an {@link Optional} in terms of the {@link Bias}
     */
    public Optional<Bias> toOptional() {
        return bias.apply(businessCase);
    }

    /**
     * @return If the underlying business case is the {@link Bias} then the {@link Bias}, else an {@link IllegalStateException}
     * @throws IllegalStateException If the underlying business case is not the {@link Bias}
     */
    public Bias get() {
        return orElseThrow(() -> new IllegalStateException(format("Not present. Business case is: '%s'.", businessCase)));
    }

    /**
     * @param alternative The result if the underlying business case is not the {@link Bias}
     * @return If the underlying business case is the {@link Bias} then the {@link Bias}, else the given alternative
     */
    public Bias orElse(Bias alternative) {
        return toOptional().orElse(alternative);
    }

    /**
     * @param alternativeSupplier The supplier of the alternative result if the underlying business case is not the {@link Bias}
     * @return If the underlying business case is the {@link Bias} then the {@link Bias}, else the given alternative
     */
    public Bias orElseGet(Supplier<Bias> alternativeSupplier) {
        return toOptional().orElseGet(alternativeSupplier);
    }

    /**
     * @param exceptionSupplier The supplier of the exception to be thrown if the underlying business case is not the {@link Bias}
     * @param <X> The type of {@link Exception} that will be thrown if the underlying business case is not the {@link Bias}
     * @return If the underlying business case is the {@link Bias} then the {@link Bias}, else the given alternative
     * @throws X If the underlying business case is not the {@link Bias}
     */
    public <X extends Exception> Bias orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return toOptional().orElseThrow(exceptionSupplier);
    }

    /**
     * @return A view of the underlying business case as a {@link TechnicalFailure}
     */
    public TechnicalFailure<Happy, Sad> ifTechnicalFailure() {
        return new TechnicalFailure<>(businessCase);
    }

    /**
     * @return A view of the underlying business case as a {@link SadPath}
     */
    public SadPath<Happy, Sad> ifSad() {
        return new SadPath<>(businessCase);
    }

    /**
     * @return A view of the underlying business case as a {@link HappyPath}
     */
    public HappyPath<Happy, Sad> ifHappy() {
        return new HappyPath<>(businessCase);
    }
}
