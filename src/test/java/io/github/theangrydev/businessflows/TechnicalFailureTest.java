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

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TechnicalFailureTest {

    private class Happy {

    }

    private class Sad {

    }

    @Test
    public void happyTechnicalFailure() {
        Happy expectedHappy = new Happy();
        Happy actualHappy = TechnicalFailure.happyPath(expectedHappy).ifHappy().get();

        assertThat(actualHappy).isEqualTo(expectedHappy);
    }

    @Test
    public void sadTechnicalFailure() {
        Sad expectedSad = new Sad();
        Sad actualSad = TechnicalFailure.sadPath(expectedSad).ifSad().get();

        assertThat(actualSad).isEqualTo(expectedSad);
    }

    @Test
    public void technicalFailureRecovery() {
        Happy expectedHappy = new Happy();

        Happy actualHappy = TechnicalFailure.<Happy, Sad>technicalFailure(new Exception())
                .recover(sad -> expectedHappy)
                .get();

        assertThat(actualHappy).isSameAs(expectedHappy);
    }


    @Test
    public void technicalFailureRecoveryWithAttempt() {
        Happy expectedHappy = new Happy();

        Happy actualHappy = TechnicalFailure.<Happy, Sad>technicalFailure(new Exception())
                .recover(() -> expectedHappy)
                .get();

        assertThat(actualHappy).isSameAs(expectedHappy);
    }

    @Test
    public void technicalFailureMapToSadPath() {
        Sad sad = new Sad();

        Sad actualSad = TechnicalFailure.<Happy, Sad>technicalFailure(new Exception())
                .mapToSadPath(exception -> sad)
                .get();

        assertThat(actualSad).isSameAs(sad);
    }

    @Test
    public void technicalFailureMapToSadPathWithAttempt() {
        Sad sad = new Sad();

        Sad actualSad = TechnicalFailure.<Happy, Sad>technicalFailure(new Exception())
                .mapToSadPath(() -> sad)
                .get();

        assertThat(actualSad).isSameAs(sad);
    }

    @Test
    public void technicalFailureMap() {
        Exception mappedTechnicalFailure = new Exception();

        Exception actualTechnicalFailure = TechnicalFailure.technicalFailure(new Exception())
                .map(sad -> mappedTechnicalFailure)
                .get();

        assertThat(actualTechnicalFailure).isSameAs(mappedTechnicalFailure);
    }

    @Test
    public void joinTechnicalFailure() {
        IllegalStateException failure = new IllegalStateException();

        String join = TechnicalFailure.technicalFailure(failure)
                .join(sad -> sad.getClass().getSimpleName(), happy -> happy.getClass().getSimpleName(), e -> e.getClass().getSimpleName());

        assertThat(join).isEqualTo(failure.getClass().getSimpleName());
    }

    @Test
    public void joinTechnicalFailureWrapsFailureAndThrows() {
        Exception failure = new Exception("message");

        assertThatThrownBy(() -> technicalFailureJoinThatWrapsAndThrows(failure))
                .hasCause(failure)
                .hasMessage("Exception caught when joining. Business case is: 'Technical Failure: java.lang.Exception: message'.");
    }

    @Test
    public void joinOrThrowTechnicalFailureThrowsFailure() {
        Exception failure = new Exception();

        assertThatThrownBy(() -> technicalFailureJoinThatBlowsUpWith(failure)).isEqualTo(failure);
    }


    @Test
    public void technicalFailurePeek() {
        Exception originalTechnicalFailure = new Exception();
        AtomicReference<Exception> peekedTechnicalFailure = new AtomicReference<>();

        Exception actualTechnicalFailure = TechnicalFailure.technicalFailure(originalTechnicalFailure)
                .peek(peekedTechnicalFailure::set)
                .get();

        assertThat(peekedTechnicalFailure.get()).isSameAs(originalTechnicalFailure);
        assertThat(actualTechnicalFailure).isSameAs(originalTechnicalFailure);
    }

    @Test
    public void technicalFailurePeekWithUncaughtExceptionIsATechnicalFailure() {
        Exception uncaughtException = new Exception();

        Exception actualException = TechnicalFailure.technicalFailure(new Exception())
                .peek(happy -> {throw uncaughtException;})
                .ifTechnicalFailure().get();

        assertThat(actualException).isSameAs(uncaughtException);
    }
    
    private String technicalFailureJoinThatWrapsAndThrows(Exception failure) throws Exception {
        return TechnicalFailure.technicalFailure(failure)
                .join(sad -> sad.getClass().getSimpleName(), happy -> happy.getClass().getSimpleName());
    }

    private String technicalFailureJoinThatBlowsUpWith(Exception failure) throws Exception {
        return TechnicalFailure.technicalFailure(failure)
                .joinOrThrow(sad -> sad.getClass().getSimpleName(), happy -> happy.getClass().getSimpleName());
    }
}
