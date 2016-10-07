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

public class TechnicalFailureCaseHappyPathTest {

    private final Exception technicalFailure = new Exception();
    private final TechnicalFailureCaseHappyPath<Object, Object> technicalFailureCaseHappyPath = new TechnicalFailureCaseHappyPath<>(technicalFailure);

    @Test
    public void toOptionalIsEmpty() {
        assertThat(technicalFailureCaseHappyPath.toOptional()).isEmpty();
    }

    @Test
    public void ifTechnicalFailureIsTechnicalFailureCase() {
        assertThat(technicalFailureCaseHappyPath.ifTechnicalFailure()).isInstanceOf(TechnicalFailureCaseTechnicalFailure.class);
        assertThat(technicalFailureCaseHappyPath.ifTechnicalFailure().get()).isSameAs(technicalFailure);
    }

    @Test
    public void ifHappyReturnsThis() {
        assertThat(technicalFailureCaseHappyPath.ifHappy()).isSameAs(technicalFailureCaseHappyPath);
    }

    @Test
    public void ifSadIsTechnicalFailureCase() {
        assertThat(technicalFailureCaseHappyPath.ifSad()).isInstanceOf(TechnicalFailureCaseSadPath.class);
        assertThat(technicalFailureCaseHappyPath.ifSad().ifTechnicalFailure().get()).isSameAs(technicalFailure);
    }

    @Test
    public void thenReturnsThis() {
        assertThat(technicalFailureCaseHappyPath.then(null)).isSameAs(technicalFailureCaseHappyPath);
    }

    @Test
    public void mapReturnsThis() {
        assertThat(technicalFailureCaseHappyPath.map(null)).isSameAs(technicalFailureCaseHappyPath);
    }

    @Test
    public void attemptReturnsThis() {
        assertThat(technicalFailureCaseHappyPath.attempt(null)).isSameAs(technicalFailureCaseHappyPath);
    }

    @Test
    public void attemptAllReturnsThis() {
        assertThat(technicalFailureCaseHappyPath.attemptAll(null)).isSameAs(technicalFailureCaseHappyPath);
    }

    @Test
    public void peekReturnsThis() {
        assertThat(technicalFailureCaseHappyPath.peek(null)).isSameAs(technicalFailureCaseHappyPath);
    }
}