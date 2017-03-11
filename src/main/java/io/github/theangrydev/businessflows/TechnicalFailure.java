/*
 * Copyright 2016-2017 Liam Williams <liam.williams@zoho.com>.
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

import static io.github.theangrydev.businessflows.ApiFeatureStability.STABLE;
import static io.github.theangrydev.businessflows.ApiVersionHistory.*;

/**
 * A {@link TechnicalFailure} is a {@link BusinessFlow} that is biased towards the result being an {@link Exception}.
 */
@ApiFeature(since = VERSION_1_0_0, stability = STABLE)
public interface TechnicalFailure<Happy, Sad> extends BusinessFlow<Happy, Sad>, WithOptional<Exception> {

    /**
     * Provides a {@link TechnicalFailure} view over a known {@link Exception} object.
     *
     * @param technicalFailure The technical failure to initiate the flow with
     * @param <Happy>          The type of happy object the resulting {@link TechnicalFailure} may represent
     * @param <Sad>            The type of sad object the resulting {@link TechnicalFailure} may represent
     * @return A {@link TechnicalFailure} that is a technical failure on the inside
     */
    @ApiFeature(since = VERSION_1_0_0, stability = STABLE)
    static <Happy, Sad> TechnicalFailure<Happy, Sad> technicalFailure(Exception technicalFailure) {
        return new TechnicalFailureCaseTechnicalFailure<>(technicalFailure);
    }

    /**
     * Provides a {@link TechnicalFailure} view over a known {@link Sad} object.
     *
     * @param sad     The sad object to initiate the flow with
     * @param <Happy> The type of happy object the resulting {@link TechnicalFailure} may represent
     * @param <Sad>   The type of sad object the resulting {@link TechnicalFailure} may represent
     * @return A {@link TechnicalFailure} that is sad on the inside
     */
    @ApiFeature(since = VERSION_2_3_0, stability = STABLE)
    static <Happy, Sad> TechnicalFailure<Happy, Sad> sadPath(Sad sad) {
        return new SadCaseTechnicalFailure<>(sad);
    }

    /**
     * Provides a {@link TechnicalFailure} view over a known {@link Happy} object.
     *
     * @param happy   The happy object to initiate the flow with
     * @param <Happy> The type of happy object the resulting {@link TechnicalFailure} may represent
     * @param <Sad>   The type of sad object the resulting {@link TechnicalFailure} may represent
     * @return A {@link TechnicalFailure} that is happy on the inside
     */
    @ApiFeature(since = VERSION_2_3_0, stability = STABLE)
    static <Happy, Sad> TechnicalFailure<Happy, Sad> happyPath(Happy happy) {
        return new HappyCaseTechnicalFailure<>(happy);
    }

    /**
     * If the underlying business case is a technical failure, then apply the given action, otherwise do nothing to the
     * underlying case.
     *
     * @param action The action to apply to an existing technical failure
     * @return The result of applying the action to the existing technical failure, if applicable
     */
    @ApiFeature(since = VERSION_1_0_0, stability = STABLE)
    TechnicalFailure<Happy, Sad> then(Mapping<Exception, TechnicalFailure<Happy, Sad>> action);

    /**
     * If the underlying business case is a technical failure, then apply the given mapping, otherwise do nothing to the
     * underlying case.
     *
     * @param mapping The mapping to apply to an existing technical failure
     * @return The result of applying the mapping to the existing technical failure, if applicable
     */
    @ApiFeature(since = VERSION_1_0_0, stability = STABLE)
    TechnicalFailure<Happy, Sad> map(Mapping<Exception, Exception> mapping);

    /**
     * If the underlying business case is a technical failure, recover to a {@link Happy} path using the given recovery mapping.
     *
     * @param recovery The recovery to apply to an existing technical failure
     * @return The result of applying the recovery to the existing technical failure, if applicable
     */
    @ApiFeature(since = VERSION_1_0_0, stability = STABLE)
    HappyPath<Happy, Sad> recover(Mapping<Exception, Happy> recovery);

    /**
     * If the underlying business case is a technical failure, recover to a {@link Happy} path using the given {@link Attempt}.
     *
     * @param recovery The recovery to apply to an existing technical failure
     * @return The result of applying {@link Attempt}, if applicable
     */
    @ApiFeature(since = VERSION_2_5_0, stability = STABLE)
    HappyPath<Happy, Sad> recover(Attempt<Happy> recovery);

    /**
     * If the underlying business case is a technical failure, map to a {@link Sad} path using the given recovery mapping.
     *
     * @param mapping The mapping to apply to an existing technical failure
     * @return The result of applying the mapping to the existing technical failure, if applicable
     */
    @ApiFeature(since = VERSION_1_0_0, stability = STABLE)
    SadPath<Happy, Sad> mapToSadPath(Mapping<Exception, Sad> mapping);

    /**
     * If the underlying business case is a technical failure, map to a {@link Sad} path using the given {@link Attempt}
     *
     * @param mapping The mapping to apply to an existing technical failure
     * @return The result of applying {@link Attempt}, if applicable
     */
    @ApiFeature(since = VERSION_2_5_0, stability = STABLE)
    SadPath<Happy, Sad> mapToSadPath(Attempt<Sad> mapping);

    /**
     * Take a look at the technical failure (if there really is one).
     *
     * @param peek What to do if the underlying business case is a technical failure
     * @return The same {@link TechnicalFailure}
     */
    @ApiFeature(since = VERSION_1_0_0, stability = STABLE)
    TechnicalFailure<Happy, Sad> peek(Peek<Exception> peek);

    /**
     * Throw the technical failure, if it is present.
     *
     * @throws Exception if the underlying business case is a technical failure
     * @return This {@link TechnicalFailure} if there is not an {@link Exception} to throw
     */
    @ApiFeature(since = VERSION_7_2_0, stability = STABLE)
    TechnicalFailure<Happy, Sad> throwIt() throws Exception;

    /**
     * Throw the technical failure, if it is present, wrapped in a {@link RuntimeException}.
     *
     * @throws RuntimeException if the underlying business case is a technical failure
     * @return This {@link TechnicalFailure} if there is not an {@link RuntimeException} to throw
     */
    @ApiFeature(since = VERSION_7_3_0, stability = STABLE)
    TechnicalFailure<Happy, Sad> throwItAsARuntimeException() throws RuntimeException;

    /**
     * {@inheritDoc}
     */
    @ApiFeature(since = VERSION_3_1_1, stability = STABLE)
    @Override
    default TechnicalFailure<Happy, Sad> ifTechnicalFailure() {
        return this;
    }
}
