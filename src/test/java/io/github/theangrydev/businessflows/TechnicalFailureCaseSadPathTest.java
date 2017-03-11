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

public class TechnicalFailureCaseSadPathTest {

    private final Exception technicalFailure = new Exception();
    private final TechnicalFailureCaseSadPath<Object, Object> happyCaseSadPath = new TechnicalFailureCaseSadPath<>(technicalFailure);

    @Test
    public void toOptionalIsEmpty() {
        assertThat(happyCaseSadPath.toOptional()).isEmpty();
    }

    @Test
    public void ifTechnicalFailureIsTechnicalFailureCase() {
        assertThat(happyCaseSadPath.ifTechnicalFailure()).isInstanceOf(TechnicalFailureCaseTechnicalFailure.class);
        assertThat(happyCaseSadPath.ifTechnicalFailure().get()).isSameAs(technicalFailure);
    }

    @Test
    public void ifHappyIsTechnicalFailureCase() {
        assertThat(happyCaseSadPath.ifHappy()).isInstanceOf(TechnicalFailureCaseHappyPath.class);
        assertThat(happyCaseSadPath.ifHappy().ifTechnicalFailure().get()).isSameAs(technicalFailure);
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
    public void recoverUsingAttemptReturnsTechnicalFailureCase() {
        assertThat(happyCaseSadPath.recover((Attempt<Object>) null)).isInstanceOf(TechnicalFailureCaseHappyPath.class);
        assertThat(happyCaseSadPath.recover((Attempt<Object>) null).ifTechnicalFailure().get()).isSameAs(technicalFailure);
    }

    @Test
    public void recoverUsingMappingAttemptReturnsTechnicalFailureCase() {
        assertThat(happyCaseSadPath.recover((Mapping<Object, Object>) null)).isInstanceOf(TechnicalFailureCaseHappyPath.class);
        assertThat(happyCaseSadPath.recover((Mapping<Object, Object>) null).ifTechnicalFailure().get()).isSameAs(technicalFailure);
    }

    @Test
    public void peekReturnsThis() {
        assertThat(happyCaseSadPath.peek(null)).isSameAs(happyCaseSadPath);
    }
}