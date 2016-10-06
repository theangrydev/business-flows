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

public class SadCaseTechnicalFailureTest {

    class Sad {

    }

    private final Sad sad = new Sad();
    private final SadCaseTechnicalFailure<Object, Sad> sadCaseTechnicalFailure = new SadCaseTechnicalFailure<>(sad);


    @Test
    public void throwItDoesNothing() throws Exception {
        sadCaseTechnicalFailure.throwIt();
    }

    @Test
    public void throwItAsARuntimeExceptionDoesNothing()  {
        sadCaseTechnicalFailure.throwItAsARuntimeException();
    }

    @Test
    public void toOptionalIsEmpty() {
        assertThat(sadCaseTechnicalFailure.toOptional()).isEmpty();
    }

    @Test
    public void ifTechnicalFailureReturnsThis() {
        assertThat(sadCaseTechnicalFailure.ifTechnicalFailure()).isSameAs(sadCaseTechnicalFailure);
    }

    @Test
    public void ifHappyIsTechnicalFailureCase() {
        assertThat(sadCaseTechnicalFailure.ifHappy()).isInstanceOf(SadCaseHappyPath.class);
        assertThat(sadCaseTechnicalFailure.ifHappy().ifSad().get()).isSameAs(sad);
    }

    @Test
    public void ifSadIsSadCase() {
        assertThat(sadCaseTechnicalFailure.ifSad()).isInstanceOf(SadCaseSadPath.class);
        assertThat(sadCaseTechnicalFailure.ifSad().get()).isSameAs(sad);
    }

    @Test
    public void thenReturnsThis() {
        assertThat(sadCaseTechnicalFailure.then(null)).isSameAs(sadCaseTechnicalFailure);
    }

    @Test
    public void mapReturnsThis() {
        assertThat(sadCaseTechnicalFailure.map(null)).isSameAs(sadCaseTechnicalFailure);
    }

    @Test
    public void recoverUsingAttemptReturnsSadCase() {
        assertThat(sadCaseTechnicalFailure.recover((Attempt<Object>) null)).isInstanceOf(SadCaseHappyPath.class);
        assertThat(sadCaseTechnicalFailure.recover((Attempt<Object>) null).ifSad().get()).isEqualTo(sad);
    }

    @Test
    public void recoverUsingMappingAttemptReturnsSadCase() {
        assertThat(sadCaseTechnicalFailure.recover((Mapping<Exception, Object>) null)).isInstanceOf(SadCaseHappyPath.class);
        assertThat(sadCaseTechnicalFailure.recover((Mapping<Exception, Object>) null).ifSad().get()).isEqualTo(sad);
    }

    @Test
    public void mapToSadPathUsingAttemptReturnsSadCase() {
        assertThat(sadCaseTechnicalFailure.mapToSadPath((Attempt<Sad>) null)).isInstanceOf(SadCaseSadPath.class);
        assertThat(sadCaseTechnicalFailure.mapToSadPath((Attempt<Sad>) null).get()).isSameAs(sad);
    }

    @Test
    public void mapToSadPathUsingMappingAttemptReturnsSadCase() {
        assertThat(sadCaseTechnicalFailure.mapToSadPath((Mapping<Exception, Sad>) null)).isInstanceOf(SadCaseSadPath.class);
        assertThat(sadCaseTechnicalFailure.mapToSadPath((Mapping<Exception, Sad>) null).get()).isSameAs(sad);
    }

    @Test
    public void peekReturnsThis() {
        assertThat(sadCaseTechnicalFailure.peek(null)).isSameAs(sadCaseTechnicalFailure);
    }
}