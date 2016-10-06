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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TechnicalFailureCaseTechnicalFailureTest {

    class Sad {

    }
    class Happy {

    }

    private final Exception technicalFailure = new Exception();
    private final TechnicalFailureCaseTechnicalFailure<Happy, Sad> technicalFailureCaseTechnicalFailure = new TechnicalFailureCaseTechnicalFailure<>(technicalFailure);

    @Test
    public void throwsTechnicalFailure() {
        assertThatThrownBy(technicalFailureCaseTechnicalFailure::throwIt).isEqualTo(technicalFailure);
    }

    @Test
    public void throwsTechnicalFailureAsRuntimeException() {
        assertThatThrownBy(technicalFailureCaseTechnicalFailure::throwItAsARuntimeException)
                .isInstanceOf(RuntimeException.class)
                .hasCause(technicalFailure);
    }

    @Test
    public void toOptionalIsPresent() {
        assertThat(technicalFailureCaseTechnicalFailure.toOptional()).contains(technicalFailure);
    }

    @Test
    public void ifTechnicalFailureReturnsThis() {
        assertThat(technicalFailureCaseTechnicalFailure.ifTechnicalFailure()).isSameAs(technicalFailureCaseTechnicalFailure);
    }

    @Test
    public void ifHappyIsTechnicalFailureCase() {
        assertThat(technicalFailureCaseTechnicalFailure.ifHappy()).isInstanceOf(TechnicalFailureCaseHappyPath.class);
        assertThat(technicalFailureCaseTechnicalFailure.ifHappy().ifTechnicalFailure().get()).isSameAs(technicalFailure);
    }

    @Test
    public void ifSadIsSadCase() {
        assertThat(technicalFailureCaseTechnicalFailure.ifSad()).isInstanceOf(TechnicalFailureCaseSadPath.class);
        assertThat(technicalFailureCaseTechnicalFailure.ifSad().ifTechnicalFailure().get()).isSameAs(technicalFailure);
    }

    @Test
    public void thenMapsTechnicalFailureToNewTechnicalFailure() {
        TechnicalFailure<Happy, Sad> newSadPath = TechnicalFailure.technicalFailure(new Exception());
        assertThat(technicalFailureCaseTechnicalFailure.then(sad -> newSadPath)).isSameAs(newSadPath);
    }

    @Test
    public void thenTechnicalFailure() {
        Exception expectedTechnicalFailure = new Exception();
        assertThat(technicalFailureCaseTechnicalFailure.then(sad -> {throw expectedTechnicalFailure;})).isInstanceOf(TechnicalFailureCaseTechnicalFailure.class);
        assertThat(technicalFailureCaseTechnicalFailure.then(sad -> {throw expectedTechnicalFailure;}).ifTechnicalFailure().get()).isSameAs(expectedTechnicalFailure);
    }

    @Test
    public void mapMapsSadToNewTechnicalFailure() {
        Exception newTechnicalFailure = new Exception();
        assertThat(technicalFailureCaseTechnicalFailure.map(technicalFailure -> newTechnicalFailure)).isInstanceOf(TechnicalFailureCaseTechnicalFailure.class);
        assertThat(technicalFailureCaseTechnicalFailure.map(technicalFailure -> newTechnicalFailure).get()).isSameAs(newTechnicalFailure);
    }

    @Test
    public void mapTechnicalFailure() {
        Exception expectedTechnicalFailure = new Exception();
        assertThat(technicalFailureCaseTechnicalFailure.map(sad -> {throw expectedTechnicalFailure;})).isInstanceOf(TechnicalFailureCaseTechnicalFailure.class);
        assertThat(technicalFailureCaseTechnicalFailure.map(sad -> {throw expectedTechnicalFailure;}).get()).isSameAs(expectedTechnicalFailure);
    }

    @Test
    public void recoverMapsSadToHappyPath() {
        Happy expectedHappy = new Happy();
        assertThat(technicalFailureCaseTechnicalFailure.recover(sad -> expectedHappy)).isInstanceOf(HappyCaseHappyPath.class);
        assertThat(technicalFailureCaseTechnicalFailure.recover(sad -> expectedHappy).ifHappy().get()).isSameAs(expectedHappy);
    }


    @Test
    public void recoverTechnicalFailure() {
        Exception expectedTechnicalFailure = new Exception();
        assertThat(technicalFailureCaseTechnicalFailure.recover(sad -> {throw expectedTechnicalFailure;})).isInstanceOf(TechnicalFailureCaseHappyPath.class);
        assertThat(technicalFailureCaseTechnicalFailure.recover(sad -> {throw expectedTechnicalFailure;}).ifTechnicalFailure().get()).isSameAs(expectedTechnicalFailure);
    }

    @Test
    public void recoverAttemptMapsSadToHappyPath() {
        Happy expectedHappy = new Happy();
        assertThat(technicalFailureCaseTechnicalFailure.recover(() -> expectedHappy)).isInstanceOf(HappyCaseHappyPath.class);
        assertThat(technicalFailureCaseTechnicalFailure.recover(() -> expectedHappy).ifHappy().get()).isSameAs(expectedHappy);
    }

    @Test
    public void recoverAttemptTechnicalFailure() {
        Exception expectedTechnicalFailure = new Exception();
        assertThat(technicalFailureCaseTechnicalFailure.recover(() -> {throw expectedTechnicalFailure;})).isInstanceOf(TechnicalFailureCaseHappyPath.class);
        assertThat(technicalFailureCaseTechnicalFailure.recover(() -> {throw expectedTechnicalFailure;}).ifTechnicalFailure().get()).isSameAs(expectedTechnicalFailure);
    }

    @Test
    public void mapToSadPathMapsTechnicalFailureToSadTo() {
        Sad expectedSad = new Sad();
        assertThat(technicalFailureCaseTechnicalFailure.mapToSadPath(sad -> expectedSad)).isInstanceOf(SadCaseSadPath.class);
        assertThat(technicalFailureCaseTechnicalFailure.mapToSadPath(sad -> expectedSad).get()).isSameAs(expectedSad);
    }

    @Test
    public void mapToSadPathTechnicalFailure() {
        Exception expectedTechnicalFailure = new Exception();
        assertThat(technicalFailureCaseTechnicalFailure.mapToSadPath(sad -> {throw expectedTechnicalFailure;})).isInstanceOf(TechnicalFailureCaseSadPath.class);
        assertThat(technicalFailureCaseTechnicalFailure.mapToSadPath(sad -> {throw expectedTechnicalFailure;}).ifTechnicalFailure().get()).isSameAs(expectedTechnicalFailure);
    }


    @Test
    public void mapToSadPathAttemptProvidesSad() {
        Sad expectedSad = new Sad();
        assertThat(technicalFailureCaseTechnicalFailure.mapToSadPath(() -> expectedSad)).isInstanceOf(SadCaseSadPath.class);
        assertThat(technicalFailureCaseTechnicalFailure.mapToSadPath(() -> expectedSad).get()).isSameAs(expectedSad);
    }

    @Test
    public void mapToSadPathAttemptTechnicalFailure() {
        Exception expectedTechnicalFailure = new Exception();
        assertThat(technicalFailureCaseTechnicalFailure.mapToSadPath(() -> {throw expectedTechnicalFailure;})).isInstanceOf(TechnicalFailureCaseSadPath.class);
        assertThat(technicalFailureCaseTechnicalFailure.mapToSadPath(() -> {throw expectedTechnicalFailure;}).ifTechnicalFailure().get()).isSameAs(expectedTechnicalFailure);
    }
}