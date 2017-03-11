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
package api;

import io.github.theangrydev.businessflows.ApiFeature;
import io.github.theangrydev.businessflows.ApiFeatureStability;
import io.github.theangrydev.businessflows.ApiVersionHistory;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.lang.reflect.Method;

public class ApiFeatureTest implements WithAssertions {

    @Test
    public void className() throws NoSuchMethodException {
        assertThat(ApiFeature.class.getName()).isEqualTo("io.github.theangrydev.businessflows.ApiFeature");
    }

    @Test
    public void sinceMethod() throws NoSuchMethodException {
        Method since = ApiFeature.class.getDeclaredMethod("since");
        assertThat(since.getReturnType()).isEqualTo(ApiVersionHistory.class);
    }

    @Test
    public void stabilityMethod() throws NoSuchMethodException {
        Method stability = ApiFeature.class.getDeclaredMethod("stability");
        assertThat(stability.getReturnType()).isEqualTo(ApiFeatureStability.class);
    }

    @Test
    public void commentsMethod() throws NoSuchMethodException {
        Method comments = ApiFeature.class.getDeclaredMethod("comments");
        assertThat(comments.getReturnType()).isEqualTo(String.class);
    }

    @Test
    public void newMethodsAreDocumented() {
        assertThat(ApiFeature.class.getDeclaredMethods()).hasSize(3);
    }
}
