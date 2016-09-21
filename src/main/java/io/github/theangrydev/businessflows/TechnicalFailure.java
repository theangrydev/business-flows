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
 * A {@link TechnicalFailure} is a {@link BusinessFlow} that is biased towards the result being an {@link Exception}.
 *
 * {@inheritDoc}
 */
public class TechnicalFailure<Happy, Sad> extends BusinessFlow<Happy, Sad, Exception> {
    TechnicalFailure(BusinessCase<Happy, Sad> businessCase) {
        super(BusinessCase::technicalFailureOptional, businessCase);
    }

    /**
     * @param technicalFailure The technical failure to initiate the flow with
     * @param <Happy> The type of happy object the resulting {@link TechnicalFailure} may represent
     * @param <Sad> The type of sad object the resulting {@link TechnicalFailure} may represent
     * @return A {@link TechnicalFailure} that is a technical failure on the inside
     */
    public static <Happy, Sad> TechnicalFailure<Happy, Sad> technicalFailure(Exception technicalFailure) {
        return new TechnicalFailure<>(new TechnicalFailureCase<>(technicalFailure));
    }

    /**
     * @param sad The sad object to initiate the flow with
     * @param <Happy> The type of happy object the resulting {@link TechnicalFailure} may represent
     * @param <Sad> The type of sad object the resulting {@link TechnicalFailure} may represent
     * @return A {@link TechnicalFailure} that is sad on the inside
     */
    public static <Happy, Sad> TechnicalFailure<Happy, Sad> sadPath(Sad sad) {
        return new TechnicalFailure<>(new SadCase<>(sad));
    }

    /**
     * @param happy The happy object to initiate the flow with
     * @param <Happy> The type of happy object the resulting {@link TechnicalFailure} may represent
     * @param <Sad> The type of sad object the resulting {@link TechnicalFailure} may represent
     * @return A {@link TechnicalFailure} that is happy on the inside
     */
    public static <Happy, Sad> TechnicalFailure<Happy, Sad> happyPath(Happy happy) {
        return new TechnicalFailure<>(new HappyCase<>(happy));
    }

    /**
     * If the underlying business case is a technical failure, then apply the given action, otherwise do nothing to the
     * underlying case.
     *
     * @param action The action to apply to an existing technical failure
     * @return The result of applying the action to the existing technical failure, if applicable
     */
    public TechnicalFailure<Happy, Sad> then(Mapping<Exception, TechnicalFailure<Happy, Sad>> action) {
        return join(TechnicalFailure::happyPath, TechnicalFailure::sadPath, technicalFailure1 -> {
            try {
                return action.map(technicalFailure1);
            } catch (Exception technicalFailureDuringAction) {
                return TechnicalFailure.technicalFailure(technicalFailureDuringAction);
            }
        });
    }

    /**
     * If the underlying business case is a technical failure, then apply the given mapping, otherwise do nothing to the
     * underlying case.
     *
     * @param mapping The mapping to apply to an existing technical failure
     * @return The result of applying the mapping to the existing technical failure, if applicable
     */
    public TechnicalFailure<Happy, Sad> map(Mapping<Exception, Exception> mapping) {
        return then(mapping.andThen(TechnicalFailure::technicalFailure));
    }

    /**
     * If the underlying business case is a technical failure, recover to a {@link Happy} path using the given recovery mapping.
     *
     * @param recovery The recovery to apply to an existing technical failure
     * @return The result of applying the recovery to the existing technical failure, if applicable
     */
    public HappyPath<Happy, Sad> recover(Mapping<Exception, Happy> recovery) {
        return then(technicalFailure -> happyPath(recovery.map(technicalFailure))).ifHappy();
    }

    /**
     * If the underlying business case is a technical failure, map to a {@link Sad} path using the given recovery mapping.
     *
     * @param mapping The mapping to apply to an existing technical failure
     * @return The result of applying the mapping to the existing technical failure, if applicable
     */
    public SadPath<Happy, Sad> mapToSadPath(Mapping<Exception, Sad> mapping) {
        return then(mapping.andThen(TechnicalFailure::sadPath)).ifSad();
    }

    /**
     * Take a look at the technical failure (if there really is one).
     *
     * @param peek What to do if the underlying business case is a technical failure
     * @return The same {@link TechnicalFailure}
     */
    public TechnicalFailure<Happy, Sad> peek(Peek<Exception> peek) {
        return then(technicalFailure -> {
            peek.peek(technicalFailure);
            return this;
        });
    }
}
