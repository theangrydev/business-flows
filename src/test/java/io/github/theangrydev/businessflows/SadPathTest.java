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

public class SadPathTest {

    private class Happy {

    }
    
    private class Sad {
        
    }
    
    private class Sad2 {
        
    }
    
    @Test
    public void happySadPath() {
        Happy expectedHappy = new Happy();
        Happy actualHappy = SadPath.happyPath(expectedHappy).ifHappy().get();

        assertThat(actualHappy).isEqualTo(expectedHappy);
    }

    @Test
    public void technicalFailureSadPath() {
        Exception expectedTechnicalFailure = new Exception();
        Exception actualTechnicalFailure = SadPath.technicalFailure(expectedTechnicalFailure).ifTechnicalFailure().get();

        assertThat(actualTechnicalFailure).isEqualTo(expectedTechnicalFailure);
    }


    @Test
    public void thenWithPossibleSadPathThatIsSad() {
        Sad expectedSad = new Sad();

        Sad actualSad = HappyPath.<Happy, Sad>happyPath(new Happy())
                .then(happy -> SadPath.sadPath(expectedSad))
                .ifSad().get();

        assertThat(actualSad).isSameAs(expectedSad);
    }

    @Test
    public void sadRecovery() {
        Happy expectedHappy = new Happy();

        Happy actualHappy = SadPath.<Happy, Sad>sadPath(new Sad())
                .recover(sad -> expectedHappy)
                .get();

        assertThat(actualHappy).isSameAs(expectedHappy);
    }

    @Test
    public void sadRecoveryTechnicalFailure() {
        Exception expectedTechnicalFailure = new Exception();

        Exception actualTechnicalFailure = SadPath.<Happy, Sad>sadPath(new Sad())
                .recover(sad -> {throw expectedTechnicalFailure;})
                .ifTechnicalFailure().get();

        assertThat(actualTechnicalFailure).isSameAs(expectedTechnicalFailure);
    }

    @Test
    public void sadRecoveryWithAttempt() {
        Happy expectedHappy = new Happy();

        Happy actualHappy = SadPath.<Happy, Sad>sadPath(new Sad())
                .recover(() -> expectedHappy)
                .get();

        assertThat(actualHappy).isSameAs(expectedHappy);
    }

    @Test
    public void sadRecoveryWithAttemptTechnicalFailure() {
        Exception expectedTechnicalFailure = new Exception();

        Exception actualTechnicalFailure = SadPath.<Happy, Sad>sadPath(new Sad())
                .recover(() -> {throw expectedTechnicalFailure;})
                .ifTechnicalFailure().get();

        assertThat(actualTechnicalFailure).isSameAs(expectedTechnicalFailure);
    }


    @Test
    public void sadMap() {
        Sad2 mappedSad = new Sad2();

        Sad2 actualSad = SadPath.<Happy, Sad>sadPath(new Sad())
                .map(sad -> mappedSad)
                .get();

        assertThat(actualSad).isSameAs(mappedSad);
    }


    @Test
    public void sadPeekWithUncaughtExceptionIsATechnicalFailure() {
        Exception uncaughtException = new Exception();

        Exception actualException = SadPath.sadPath(new Sad())
                .ifSad().peek(happy -> {throw uncaughtException;})
                .ifTechnicalFailure().get();

        assertThat(actualException).isSameAs(uncaughtException);
    }


    @Test
    public void joinSad() {
        Sad originalSad = new Sad();

        String join = SadPath.sadPath(originalSad)
                .join(sad -> sad.getClass().getSimpleName(), happy -> happy.getClass().getSimpleName(), e -> e.getClass().getSimpleName());

        assertThat(join).isEqualTo(originalSad.getClass().getSimpleName());
    }

    @Test
    public void joinSadThatBlowsUp() throws Exception {
        Sad originalSad = new Sad();

        String join = SadPath.sadPath(originalSad)
                .joinOrThrow(sad -> sad.getClass().getSimpleName(), happy -> happy.getClass().getSimpleName());

        assertThat(join).isEqualTo(originalSad.getClass().getSimpleName());
    }

    @Test
    public void sadPeek() {
        Sad originalSad = new Sad();
        AtomicReference<Sad> peekedSad = new AtomicReference<>();

        Sad actualSad = SadPath.sadPath(originalSad)
                .ifSad().peek(peekedSad::set)
                .get();

        assertThat(peekedSad.get()).isSameAs(originalSad);
        assertThat(actualSad).isSameAs(originalSad);
    }
}
