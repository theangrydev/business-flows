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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HappyCaseTechnicalFailureTest {

    class Happy {

    }

    private final Happy happy = new Happy();
    private final HappyCaseTechnicalFailure<Happy, Object> happyCaseTechnicalFailure = new HappyCaseTechnicalFailure<>(happy);

    @Test
    public void throwItReturnsThis() throws Exception {
        assertThat(happyCaseTechnicalFailure.throwIt()).isSameAs(happyCaseTechnicalFailure);
    }

    @Test
    public void throwItAsARuntimeExceptionReturnsThis()  {
        assertThat(happyCaseTechnicalFailure.throwItAsARuntimeException()).isSameAs(happyCaseTechnicalFailure);
    }

    @Test
    public void toOptionalIsEmpty() {
        assertThat(happyCaseTechnicalFailure.toOptional()).isEmpty();
    }

    @Test
    public void ifTechnicalFailureReturnsThis() {
        assertThat(happyCaseTechnicalFailure.ifTechnicalFailure()).isInstanceOf(HappyCaseTechnicalFailure.class);
        assertThat(happyCaseTechnicalFailure.ifTechnicalFailure().ifHappy().get()).isEqualTo(happy);
    }

    @Test
    public void ifHappyIsHappyCase() {
        assertThat(happyCaseTechnicalFailure.ifHappy()).isInstanceOf(HappyCaseHappyPath.class);
        assertThat(happyCaseTechnicalFailure.ifHappy().get()).isEqualTo(happy);
    }

    @Test
    public void ifSadIsHappyCase() {
        assertThat(happyCaseTechnicalFailure.ifSad()).isInstanceOf(HappyCaseSadPath.class);
        assertThat(happyCaseTechnicalFailure.ifSad().ifHappy().get()).isEqualTo(happy);
    }

    @Test
    public void thenReturnsThis() {
        assertThat(happyCaseTechnicalFailure.then(null)).isEqualTo(happyCaseTechnicalFailure);
    }

    @Test
    public void mapReturnsThis() {
        assertThat(happyCaseTechnicalFailure.map(null)).isEqualTo(happyCaseTechnicalFailure);
    }

    @Test
    public void recoverUsingAttemptReturnsHappyCase() {
        assertThat(happyCaseTechnicalFailure.recover((Attempt<Happy>) null)).isInstanceOf(HappyCaseHappyPath.class);
        assertThat(happyCaseTechnicalFailure.recover((Attempt<Happy>) null).get()).isEqualTo(happy);
    }

    @Test
    public void recoverUsingMappingAttemptReturnsHappyCase() {
        assertThat(happyCaseTechnicalFailure.recover((Mapping<Exception, Happy>) null)).isInstanceOf(HappyCaseHappyPath.class);
        assertThat(happyCaseTechnicalFailure.recover((Mapping<Exception, Happy>) null).get()).isEqualTo(happy);
    }

    @Test
    public void mapToSadPathUsingAttemptReturnsHappyCase() {
        assertThat(happyCaseTechnicalFailure.mapToSadPath((Attempt<Object>) null)).isInstanceOf(HappyCaseSadPath.class);
        assertThat(happyCaseTechnicalFailure.mapToSadPath((Attempt<Object>) null).ifHappy().get()).isSameAs(happy);
    }

    @Test
    public void mapToSadPathUsingMappingAttemptReturnsHappyCase() {
        assertThat(happyCaseTechnicalFailure.mapToSadPath((Mapping<Exception, Object>) null)).isInstanceOf(HappyCaseSadPath.class);
        assertThat(happyCaseTechnicalFailure.mapToSadPath((Mapping<Exception, Object>) null).ifHappy().get()).isSameAs(happy);
    }

    @Test
    public void peekReturnsThis() {
        assertThat(happyCaseTechnicalFailure.peek(null)).isEqualTo(happyCaseTechnicalFailure);
    }
}