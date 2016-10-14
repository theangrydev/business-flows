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
 * A {@link BusinessFlow} is a biased view of a {@link BusinessCase}.
 * This is the base {@link BusinessFlow} that contains operations that are common to all the biased views.
 *
 * @param <Happy> The type of happy object this {@link BusinessFlow} may represent
 * @param <Sad>   The type of sad object this {@link BusinessFlow} may represent
 * @param <Bias>  The type of bias (happy, sad or technical failure) this {@link BusinessFlow} has
 */
public interface BusinessFlow<Happy, Sad, Bias> extends BusinessCase<Happy, Sad>, WithOptional<Bias> {

    /**
     * A {@link TechnicalFailure} view of the {@link BusinessFlow}.
     * <p>
     * NOTE: The technical failure will only be present if the failure occurred inside of a {@link BusinessFlow}.
     * To ensure failures are caught inside a flow, use e.g. {@link HappyPath#happyPathAttempt(Attempt)} at the top of
     * the scope, before this method is called.
     * </p>
     *
     * @return A view of the underlying business case as a {@link TechnicalFailure}
     */
    TechnicalFailure<Happy, Sad> ifTechnicalFailure();

    /**
     * A {@link SadPath} view of the {@link BusinessFlow}.
     *
     * @return A view of the underlying business case as a {@link SadPath}
     */
    SadPath<Happy, Sad> ifSad();

    /**
     * A {@link HappyPath} view of the {@link BusinessFlow}.
     *
     * @return A view of the underlying business case as a {@link HappyPath}
     */
    HappyPath<Happy, Sad> ifHappy();
}
