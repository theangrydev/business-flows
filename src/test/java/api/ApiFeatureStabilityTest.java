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
package api;

import io.github.theangrydev.businessflows.ApiFeatureStability;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import static io.github.theangrydev.businessflows.ApiFeatureStability.*;

public class ApiFeatureStabilityTest implements WithAssertions {

    @Test
    public void constantNames() {
        assertThat(BETA).hasToString("BETA");
        assertThat(DEPRECATED).hasToString("DEPRECATED");
        assertThat(STABLE).hasToString("STABLE");
        assertThat(EXPERIMENTAL).hasToString("EXPERIMENTAL");
    }

    @Test
    public void className() {
        assertThat(ApiFeatureStability.class.getName()).isEqualTo("io.github.theangrydev.businessflows.ApiFeatureStability");
    }
}