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

public class SadCaseHappyPathTest {

    class Sad {

    }

    private final Sad sad = new Sad();
    private final SadCaseHappyPath<Object, Sad> sadCaseHappyPath = new SadCaseHappyPath<>(sad);

    @Test
    public void toOptionalIsEmpty() {
        assertThat(sadCaseHappyPath.toOptional()).isEmpty();
    }

    @Test
    public void ifTechnicalFailureIsSadCase() {
        assertThat(sadCaseHappyPath.ifTechnicalFailure()).isInstanceOf(SadCaseTechnicalFailure.class);
        assertThat(sadCaseHappyPath.ifTechnicalFailure().ifSad().get()).isSameAs(sad);
    }

    @Test
    public void ifHappyReturnsThis() {
        assertThat(sadCaseHappyPath.ifHappy()).isSameAs(sadCaseHappyPath);
    }

    @Test
    public void ifSadIsSadCase() {
        assertThat(sadCaseHappyPath.ifSad()).isInstanceOf(SadCaseSadPath.class);
        assertThat(sadCaseHappyPath.ifSad().get()).isSameAs(sad);
    }

    @Test
    public void thenReturnsThis() {
        assertThat(sadCaseHappyPath.then(null)).isSameAs(sadCaseHappyPath);
    }

    @Test
    public void mapReturnsThis() {
        assertThat(sadCaseHappyPath.map(null)).isSameAs(sadCaseHappyPath);
    }

    @Test
    public void attemptReturnsThis() {
        assertThat(sadCaseHappyPath.attempt(null)).isSameAs(sadCaseHappyPath);
    }

    @Test
    public void attemptAllReturnsThis() {
        assertThat(sadCaseHappyPath.attemptAll(null)).isSameAs(sadCaseHappyPath);
    }

    @Test
    public void peekReturnsThis() {
        assertThat(sadCaseHappyPath.peek(null)).isSameAs(sadCaseHappyPath);
    }
}