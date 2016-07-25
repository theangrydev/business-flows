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
package io.github.theangrydev.businessflows.example;

import io.github.theangrydev.businessflows.HappyPath;
import io.github.theangrydev.businessflows.ValidationPath;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;



public class HappyPathTest implements WithAssertions {

    private class Sad {
    }

    private class Happy {

    }

    private class Happy2 {

    }

    @Test
    public void happyAttemptThatSucceedsWithFailureMappingResultsInHappy() {
        Happy originalHappy = new Happy();

        Happy actualHappy = HappyPath.happyAttempt(() -> originalHappy).get();

        assertThat(actualHappy).isSameAs(originalHappy);
    }

    @Test
    public void happyAttemptThatSucceedsWithNoFailureMappingResultsInHappy() {
        Happy originalHappy = new Happy();

        Happy actualHappy = HappyPath.happyAttempt(() -> originalHappy).get();

        assertThat(actualHappy).isSameAs(originalHappy);
    }

    @Test
    public void happyAttemptThatFailsWithUncaughtExceptionIsATechnicalFailure() {
        Exception exceptionDuringAttempt = new Exception();

        Exception actualException = HappyPath.happyAttempt(() -> {throw exceptionDuringAttempt;})
                .ifTechnicalFailure().get();

        assertThat(actualException).isSameAs(exceptionDuringAttempt);
    }

    @Test
    public void thenWithUncaughtExceptionIsATechnicalFailure() {
        Exception exceptionDuringThen = new Exception();

        Exception actualException = HappyPath.happyPath(new Happy())
                .then(happy -> {throw exceptionDuringThen;})
                .ifTechnicalFailure().get();

        assertThat(actualException).isSameAs(exceptionDuringThen);
    }

    @Test
    public void thenWithPossibleSadPathThatIsSad() {
        Sad expectedSad = new Sad();

        Sad actualSad = HappyPath.<Sad,Happy>happyPath(new Happy())
                .then(happy -> HappyPath.sadPath(expectedSad))
                .ifSad().get();

        assertThat(actualSad).isSameAs(expectedSad);
    }

    @Test
    public void thenWithPossibleHappyPathThatIsHappy() {
        Happy2 expectedHappy = new Happy2();

        Happy2 actualHappy = HappyPath.happyPath(new Happy())
                .then(happy -> HappyPath.happyPath(expectedHappy))
                .get();

        assertThat(actualHappy).isSameAs(expectedHappy);
    }

    @Test
    public void mapWithUncaughtExceptionIsATechnicalFailure() {
        Exception uncaughtException = new Exception();

        Exception actualException = HappyPath.happyPath(new Happy())
                .map(happy -> {throw uncaughtException;})
                .ifTechnicalFailure().get();

        assertThat(actualException).isSameAs(uncaughtException);
    }

    @Test
    public void mapThatIsHappy() {
        Happy2 expectedHappy = new Happy2();

        Happy2 actualHappy = HappyPath.happyPath(new Happy())
                .map(happy -> expectedHappy)
                .get();

        assertThat(actualHappy).isSameAs(expectedHappy);
    }

    @Test
    public void attemptWithNoFailureRemainsHappy() {
        Happy originalHappy = new Happy();

        Happy actualHappy = HappyPath.happyPath(originalHappy)
                .attempt(happy -> Optional.empty())
                .get();

        assertThat(actualHappy).isSameAs(originalHappy);
    }

    @Test
    public void attemptWithUncaughtExceptionTurnsSad() {
        Exception uncaughtException = new Exception();

        Exception actualException = HappyPath.happyPath(new Happy())
                .attempt(happy -> {throw uncaughtException;})
                .ifTechnicalFailure().get();

        assertThat(actualException).isSameAs(uncaughtException);
    }

    @Test
    public void attemptWithFailureTurnsSad() {
        Sad expectedSad = new Sad();

        Sad actualSad = HappyPath.<Sad, Happy>happyPath(new Happy())
                .attempt(happy -> Optional.of(expectedSad))
                .ifSad().get();

        assertThat(actualSad).isSameAs(expectedSad);
    }

    @Test
    public void validateWithMultipleFailureTurnsSad() {
        Sad firstSad = new Sad();
        Sad secondSad = new Sad();

        List<Sad> actualSads = ValidationPath.validate(new Happy(), happy -> Optional.of(firstSad), happy -> Optional.of(secondSad))
                .ifSad().get();

        assertThat(actualSads).containsExactly(firstSad, secondSad);
    }

    @Test
    public void validateWithMultiplePassesStaysHappy() {
        Happy originalHappy = new Happy();

        Happy actualHappy = ValidationPath.validate(originalHappy, happy -> Optional.empty(), happy -> Optional.empty())
                .get();

        assertThat(actualHappy).isSameAs(originalHappy);
    }

    @Test
    public void sadToTechnicalFailure() {
        Sad originalSad = new Sad();
        Exception failure = new Exception();

        Exception actualFailure = HappyPath.sadPath(originalSad)
                .ifSad().technicalFailure(sad -> failure)
                .get();

        assertThat(actualFailure).isSameAs(failure);
    }

    @Test
    public void sadPeek() {
        Sad originalSad = new Sad();
        AtomicReference<Sad> peekedSad = new AtomicReference<>();

        Sad actualSad = HappyPath.sadPath(originalSad)
                .ifSad().peek(peekedSad::set)
                .get();

        assertThat(peekedSad.get()).isSameAs(originalSad);
        assertThat(actualSad).isSameAs(originalSad);
    }

    @Test
    public void sadPeekWithUncaughtExceptionIsATechnicalFailure() {
        Exception uncaughtException = new Exception();

        Exception actualException = HappyPath.sadPath(new Sad())
                .ifSad().peek(happy -> {throw uncaughtException;})
                .ifTechnicalFailure().get();

        assertThat(actualException).isSameAs(uncaughtException);
    }

    @Test
    public void peekWithNoFailureRemainsHappy() {
        Happy originalHappy = new Happy();
        AtomicReference<Happy> peekedHappy = new AtomicReference<>();

        Happy actualHappy = HappyPath.happyPath(originalHappy)
                .peek(peekedHappy::set)
                .get();

        assertThat(peekedHappy.get()).isSameAs(originalHappy);
        assertThat(actualHappy).isSameAs(originalHappy);
    }

    @Test
    public void peekWithUncaughtExceptionIsATechnicalFailure() {
        Exception uncaughtException = new Exception();

        Exception actualException = HappyPath.happyPath(new Happy())
                .peek(happy -> {throw uncaughtException;})
                .ifTechnicalFailure().get();

        assertThat(actualException).isSameAs(uncaughtException);
    }

    @Test
    public void joinHappy() {
        Happy originalHappy = new Happy();

        String join = HappyPath.happyPath(originalHappy)
                .join(happy -> happy.getClass().getSimpleName(), sad -> sad.getClass().getSimpleName(), e ->  e.getClass().getSimpleName());

        assertThat(join).isEqualTo(originalHappy.getClass().getSimpleName());
    }

    @Test
    public void joinSad() {
        Sad originalSad = new Sad();

        String join = HappyPath.sadPath(originalSad)
                .join(happy -> happy.getClass().getSimpleName(), sad -> sad.getClass().getSimpleName(), e -> e.getClass().getSimpleName());

        assertThat(join).isEqualTo(originalSad.getClass().getSimpleName());
    }

    @Test
    public void joinTechnicalFailure() {
        IllegalStateException failure = new IllegalStateException();

        String join = HappyPath.technicalFailure(failure)
                .join(happy -> happy.getClass().getSimpleName(), sad -> sad.getClass().getSimpleName(), e -> e.getClass().getSimpleName());

        assertThat(join).isEqualTo(failure.getClass().getSimpleName());
    }
}
