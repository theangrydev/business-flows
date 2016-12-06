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
import static io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_1_0_0;

/**
 * Similar to a {@link java.util.function.Function} but is allowed to throw an {@link Exception}.
 *
 * @param <Old> The old type (before mapping)
 * @param <New> The new type (after mapping)
 */
@FunctionalInterface
@ApiFeature(since = VERSION_1_0_0, stability = STABLE)
public interface Mapping<Old, New> {

    /**
     * Map the {@link Old} object to a {@link New} object.
     *
     * @param old The old argument
     * @return The new result
     * @throws Exception If there is a technical failure during the mapping
     */
    @ApiFeature(since = VERSION_1_0_0, stability = STABLE)
    New map(Old old) throws Exception;

    /**
     * Map the old type to itself unchanged
     *
     * @param <Old> The old type
     * @return The identity mapping
     */
    static <Old> Mapping<Old, Old> identity() {
        return old -> old;
    }
}
