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
package api.regression;

import io.github.theangrydev.businessflows.ApiVersionHistory;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import static io.github.theangrydev.businessflows.ApiVersionHistory.valueOf;

/**
 * Make sure the constants are not inadvertently renamed
 */
public class ApiVersionHistoryTest implements WithAssertions {

    @Test
    public void constants() {
        assertThat(valueOf("VERSION_1_0_0")).hasToString("1.0.0");
        assertThat(valueOf("VERSION_2_3_0")).hasToString("2.3.0");
        assertThat(valueOf("VERSION_2_5_0")).hasToString("2.5.0");
        assertThat(valueOf("VERSION_2_7_0")).hasToString("2.7.0");
        assertThat(valueOf("VERSION_3_0_0")).hasToString("3.0.0");
        assertThat(valueOf("VERSION_3_1_1")).hasToString("3.1.1");
        assertThat(valueOf("VERSION_4_0_0")).hasToString("4.0.0");
        assertThat(valueOf("VERSION_5_0_0")).hasToString("5.0.0");
        assertThat(valueOf("VERSION_5_1_0")).hasToString("5.1.0");
        assertThat(valueOf("VERSION_6_0_0")).hasToString("6.0.0");
        assertThat(valueOf("VERSION_6_1_0")).hasToString("6.1.0");
        assertThat(valueOf("VERSION_7_0_0")).hasToString("7.0.0");
        assertThat(valueOf("VERSION_7_2_0")).hasToString("7.2.0");
        assertThat(valueOf("VERSION_7_3_0")).hasToString("7.3.0");
        assertThat(valueOf("VERSION_7_4_0")).hasToString("7.4.0");
        assertThat(valueOf("VERSION_7_5_0")).hasToString("7.5.0");
        assertThat(valueOf("VERSION_7_6_0")).hasToString("7.6.0");
        assertThat(valueOf("VERSION_8_2_0")).hasToString("8.2.0");
        assertThat(valueOf("VERSION_8_3_0")).hasToString("8.3.0");
        assertThat(valueOf("VERSION_10_2_0")).hasToString("10.2.0");
        assertThat(valueOf("VERSION_10_3_0")).hasToString("10.3.0");
    }

    @Test
    public void className() {
        assertThat(ApiVersionHistory.class.getName()).isEqualTo("io.github.theangrydev.businessflows.ApiVersionHistory");
    }
}