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

import static io.github.theangrydev.businessflows.ApiFeatureStability.EXPERIMENTAL;
import static io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_10_2_0;

/**
 * This is a description of how stable part of the API is considered to be.
 */
@ApiFeature(stability = EXPERIMENTAL, since = VERSION_10_2_0)
public enum ApiFeatureStability {

    /**
     * Unstable. Could change at any time for any reason.
     */
    @ApiFeature(stability = EXPERIMENTAL, since = VERSION_10_2_0)
    EXPERIMENTAL,

    /**
     * Considered stable.
     * Backwards compatibility will always be preserved.
     */
    @ApiFeature(stability = EXPERIMENTAL, since = VERSION_10_2_0)
    STABLE,

    /**
     * Considered stable, but there is a better way of doing this now that should be used instead.
     * Backwards compatibility will always be preserved while this feature exists.
     * If the maintenance cost is too high or this feature clashes with a new feature, this feature may be removed.
     */
    @ApiFeature(stability = EXPERIMENTAL, since = VERSION_10_2_0)
    DEPRECATED,

    /**
     * In the beta testing phase. Could change based on user feedback.
     */
    @ApiFeature(stability = EXPERIMENTAL, since = VERSION_10_2_0)
    BETA
}
