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

import static io.github.theangrydev.businessflows.ApiFeatureStability.STABLE;
import static io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_3_0_0;
import static io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_7_6_0;

/**
 * This type represents a failure that may occur in e.g. {@link HappyPath#attempt(ActionThatMightFail)}.
 *
 * @param <Sad> The type of sad object that represents a failure
 */
@ApiFeature(since = VERSION_3_0_0, stability = STABLE)
public interface PotentialFailure<Sad> extends WithOptional<Sad> {

    /**
     * Construct a known failure that is mapped to a {@link Sad} object.
     *
     * @param sad   The sad object that represents a failure
     * @param <Sad> The type of sad object that represents a failure
     * @return A {@link PotentialFailure} that represents a failure
     */
    @ApiFeature(since = VERSION_3_0_0, stability = STABLE)
    static <Sad> PotentialFailure<Sad> failure(Sad sad) {
        return new PotentialFailureFailure<>(sad);
    }

    /**
     * Construct a known success.
     *
     * @param <Sad> The type of sad object that represents a failure
     * @return A {@link PotentialFailure} that represents a success
     */
    @ApiFeature(since = VERSION_3_0_0, stability = STABLE)
    static <Sad> PotentialFailure<Sad> success() {
        return new PotentialFailureSuccess<>();
    }

    /**
     * Convert the {@link PotentialFailure} to a {@link HappyPath}.
     *
     * @param happy   The happy object the {@link PotentialFailure} is about
     * @param <Happy> The type of the happy object the {@link PotentialFailure} is about
     * @return A {@link HappyPath} that is happy if the {@link PotentialFailure} is a {@link PotentialFailure#success()}
     * or sad inside if the {@link PotentialFailure} is a {@link PotentialFailure#failure(Object)}
     */
    @ApiFeature(since = VERSION_7_6_0, stability = STABLE)
    <Happy> HappyPath<Happy, Sad> toHappyPath(Happy happy);
}
