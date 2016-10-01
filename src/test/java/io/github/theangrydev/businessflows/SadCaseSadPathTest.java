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

public class SadCaseSadPathTest {

    class Sad {

    }

    class Happy {

    }

    private final Sad sad = new Sad();
    private final SadCaseSadPath<Object, Sad> sadSadCaseSadPath = new SadCaseSadPath<>(sad);

    @Test
    public void toOptionalIsPresent() {
        assertThat(sadSadCaseSadPath.toOptional()).contains(sad);
    }

    @Test
    public void ifTechnicalFailureIsSadCase() {
        assertThat(sadSadCaseSadPath.ifTechnicalFailure()).isInstanceOf(SadCaseTechnicalFailure.class);
        assertThat(sadSadCaseSadPath.ifTechnicalFailure().ifSad().get()).isSameAs(sad);
    }

    @Test
    public void ifSadReturnsThis() {
        assertThat(sadSadCaseSadPath.ifSad()).isSameAs(sadSadCaseSadPath);
    }

    @Test
    public void ifHappyIsSadCase() {
        assertThat(sadSadCaseSadPath.ifHappy()).isInstanceOf(SadCaseHappyPath.class);
        assertThat(sadSadCaseSadPath.ifHappy().ifSad().get()).isSameAs(sad);
    }

    @Test
    public void thenMapsSadToNewSadPath() {
        SadPath<Object, Sad> newSadPath = SadPath.sadPath(new Sad());
        assertThat(sadSadCaseSadPath.then(sad -> newSadPath)).isSameAs(newSadPath);
    }

    @Test
    public void thenTechnicalFailure() {
        Exception expectedTechnicalFailure = new Exception();
        assertThat(sadSadCaseSadPath.then(sad -> {throw expectedTechnicalFailure;})).isInstanceOf(TechnicalFailureCaseSadPath.class);
        assertThat(sadSadCaseSadPath.then(sad -> {throw expectedTechnicalFailure;}).ifTechnicalFailure().get()).isSameAs(expectedTechnicalFailure);
    }

    @Test
    public void mapMapsSadToNewSad() {
        Sad newSad = new Sad();
        assertThat(sadSadCaseSadPath.map(sad -> newSad)).isInstanceOf(SadCaseSadPath.class);
        assertThat(sadSadCaseSadPath.map(sad -> newSad).get()).isSameAs(newSad);
    }

    @Test
    public void mapTechnicalFailure() {
        Exception expectedTechnicalFailure = new Exception();
        assertThat(sadSadCaseSadPath.map(sad -> {throw expectedTechnicalFailure;})).isInstanceOf(TechnicalFailureCaseSadPath.class);
        assertThat(sadSadCaseSadPath.map(sad -> {throw expectedTechnicalFailure;}).ifTechnicalFailure().get()).isSameAs(expectedTechnicalFailure);
    }

    @Test
    public void recoverMapsSadToHappyPath() {
        Happy expectedHappy = new Happy();
        assertThat(sadSadCaseSadPath.recover(sad -> expectedHappy)).isInstanceOf(HappyCaseHappyPath.class);
        assertThat(sadSadCaseSadPath.recover(sad -> expectedHappy).ifHappy().get()).isSameAs(expectedHappy);
    }

    @Test
    public void recoverTechnicalFailure() {
        Exception expectedTechnicalFailure = new Exception();
        assertThat(sadSadCaseSadPath.recover(sad -> {throw expectedTechnicalFailure;})).isInstanceOf(TechnicalFailureCaseHappyPath.class);
        assertThat(sadSadCaseSadPath.recover(sad -> {throw expectedTechnicalFailure;}).ifTechnicalFailure().get()).isSameAs(expectedTechnicalFailure);
    }
}