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
 * A {@link SadPath} is a {@link BusinessFlow} that is biased towards the result being {@link Sad}.
 *
 * {@inheritDoc}
 */
public interface SadPath<Happy, Sad> extends BusinessFlow<Happy, Sad, Sad> {

    /**
     * @param sad The sad object to initiate the flow with
     * @param <Happy> The type of happy object the resulting {@link SadPath} may represent
     * @param <Sad> The type of sad object the resulting {@link SadPath} may represent
     * @return A {@link SadPath} that is sad on the inside
     */
    static <Happy, Sad> SadPath<Happy, Sad> sadPath(Sad sad) {
        return new SadCaseSadPath<>(sad);
    }

    /**
     * @param happy The happy object to initiate the flow with
     * @param <Happy> The type of happy object the resulting {@link SadPath} may represent
     * @param <Sad> The type of sad object the resulting {@link SadPath} may represent
     * @return A {@link SadPath} that is happy on the inside
     */
    static <Happy, Sad> SadPath<Happy, Sad> happyPath(Happy happy) {
        return new HappyCaseSadPath<>(happy);
    }

    /**
     * @param technicalFailure The technical failure object to initiate the flow with
     * @param <Happy> The type of happy object the resulting {@link SadPath} may represent
     * @param <Sad> The type of sad object the resulting {@link SadPath} may represent
     * @return A {@link SadPath} that is a technical failure on the inside
     */
    static <Happy, Sad> SadPath<Happy, Sad> technicalFailure(Exception technicalFailure) {
        return new TechnicalFailureCaseSadPath<>(technicalFailure);
    }

    /**
     * If the underlying business case is sad, then apply the given action, otherwise do nothing to the underlying case.
     *
     * @param action The action to apply to an existing sad case
     * @param <NewSad> The type of sad object that will be present after the action is applied to an existing sad object
     * @return The result of applying the action to the existing sad path, if applicable
     */
    <NewSad> SadPath<Happy, NewSad> then(Mapping<Sad, SadPath<Happy, NewSad>> action);

    /**
     * If the underlying business case is sad, then apply the given mapping, otherwise do nothing to the underlying case.
     *
     * @param mapping The action to apply to an existing sad case
     * @param <NewSad> The type of sad object that will be present after the mapping is applied to an existing sad object
     * @return The result of applying the mapping to the existing sad path, if applicable
     */
    <NewSad> SadPath<Happy, NewSad> map(Mapping<Sad, NewSad> mapping);

    /**
     * If the underlying business case is sad, recover to a happy path using the given recovery mapping.
     *
     * @param recovery The recovery to apply to an existing sad case
     * @return The result of applying the recovery to the existing sad path, if applicable
     */
    HappyPath<Happy, Sad> recover(Mapping<Sad, Happy> recovery);

    /**
     * If the underlying business case is sad, recover to a happy path using the {@link Attempt}.
     *
     * @param recovery The recovery to apply to an existing sad case
     * @return The result of applying {@link Attempt}, if applicable
     */
    HappyPath<Happy, Sad> recover(Attempt<Happy> recovery);

    /**
     * Take a look at the sad case (if there really is one).
     *
     * @param peek What to do if the underlying business case is sad
     * @return The same {@link SadPath}
     */
    SadPath<Happy, Sad> peek(Peek<Sad> peek);

    /**
     * {@inheritDoc}
     */
    @Override
    default SadPath<Happy, Sad> ifSad() {
        return this;
    }
}
