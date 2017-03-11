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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HappyCaseSadPathTest {

    class Happy {

    }

    private final Happy happy = new Happy();
    private final HappyCaseSadPath<Happy, Object> happyCaseSadPath = new HappyCaseSadPath<>(happy);

    @Test
    public void toOptionalIsEmpty() {
        assertThat(happyCaseSadPath.toOptional()).isEmpty();
    }

    @Test
    public void ifTechnicalFailureIsHappyCase() {
        assertThat(happyCaseSadPath.ifTechnicalFailure()).isInstanceOf(HappyCaseTechnicalFailure.class);
        assertThat(happyCaseSadPath.ifTechnicalFailure().ifHappy().get()).isSameAs(happy);
    }

    @Test
    public void ifHappyIsHappyCase() {
        assertThat(happyCaseSadPath.ifHappy()).isInstanceOf(HappyCaseHappyPath.class);
        assertThat(happyCaseSadPath.ifHappy().get()).isSameAs(happy);
    }

    @Test
    public void ifSadReturnsThis() {
        assertThat(happyCaseSadPath.ifSad()).isSameAs(happyCaseSadPath);
    }

    @Test
    public void thenReturnsThis() {
        assertThat(happyCaseSadPath.then(null)).isSameAs(happyCaseSadPath);
    }

    @Test
    public void mapReturnsThis() {
        assertThat(happyCaseSadPath.map(null)).isSameAs(happyCaseSadPath);
    }

    @Test
    public void recoverUsingAttemptReturnsHappyCase() {
        assertThat(happyCaseSadPath.recover((Attempt<Happy>) null)).isInstanceOf(HappyCaseHappyPath.class);
        assertThat(happyCaseSadPath.recover((Attempt<Happy>) null).get()).isSameAs(happy);
    }

    @Test
    public void recoverUsingMappingAttemptReturnsHappyCase() {
        assertThat(happyCaseSadPath.recover((Mapping<Object, Happy>) null)).isInstanceOf(HappyCaseHappyPath.class);
        assertThat(happyCaseSadPath.recover((Mapping<Object, Happy>) null).get()).isSameAs(happy);
    }

    @Test
    public void peekReturnsThis() {
        assertThat(happyCaseSadPath.peek(null)).isSameAs(happyCaseSadPath);
    }
}