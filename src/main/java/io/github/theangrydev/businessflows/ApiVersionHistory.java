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

import static io.github.theangrydev.businessflows.ApiFeatureStability.EXPERIMENTAL;
import static io.github.theangrydev.businessflows.ApiVersionHistory.VERSION_10_2_0;

@ApiFeature(stability = EXPERIMENTAL, since = VERSION_10_2_0)
public enum ApiVersionHistory {
    VERSION_10_2_0(10, 2, 0),
    VERSION_8_3_0(8, 3, 0),
    VERSION_8_2_0(8, 2, 0),
    VERSION_7_6_0(7, 6, 0),
    VERSION_7_5_0(7, 5, 0),
    VERSION_7_4_0(7, 4, 0),
    VERSION_7_3_0(7, 3, 0),
    VERSION_7_2_0(7, 2, 0),
    VERSION_7_0_0(7, 0, 0),
    VERSION_6_1_0(6, 1, 0),
    VERSION_6_0_0(6, 0, 0),
    VERSION_5_1_0(5, 1, 0),
    VERSION_5_0_0(5, 0, 0),
    VERSION_4_0_0(4, 0, 0),
    VERSION_3_1_1(3, 1, 1),
    VERSION_3_0_0(3, 0, 0),
    VERSION_2_7_0(2, 7, 0),
    VERSION_2_5_0(2, 5, 0),
    VERSION_2_3_0(2, 3, 0),
    VERSION_1_0_0(1, 0, 0)
    ;

    private final int major;
    private final int minor;
    private final int patch;

    ApiVersionHistory(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    @ApiFeature(stability = EXPERIMENTAL, since = VERSION_10_2_0)
    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
