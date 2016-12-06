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
import static io.github.theangrydev.businessflows.VersionHistory.VERSION_10_1_13;

@ApiFeature(stability = EXPERIMENTAL, since = VERSION_10_1_13)
public enum VersionHistory {
    VERSION_10_1_13(10, 1, 13),
    VERSION_1_0_0(1, 0, 0);

    private final int major;
    private final int minor;
    private final int patch;

    VersionHistory(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
