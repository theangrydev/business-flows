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
import static io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_2_5_0;

/**
 * Attempt to perform an action that will either:
 * <ul>
 * <li>Succeed and return a {@link Result}</li>
 * <li>Result in a technical failure and throw any kind of {@link Exception}</li>
 * </ul>
 *
 * @param <Result> The type of happy object that will be produced in the successful case
 */
@FunctionalInterface
@ApiFeature(since = VERSION_2_5_0, stability = STABLE)
public interface Attempt<Result> {

    /**
     * Attempt the action.
     *
     * @return The happy object that the method attempts to produce
     * @throws Exception If there was a technical failure in producing
     */
    @ApiFeature(since = VERSION_2_5_0, stability = STABLE)
    Result attempt() throws Exception;
}
