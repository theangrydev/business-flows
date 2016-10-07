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

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.theangrydev.businessflows.HappyPath.actions;
import static io.github.theangrydev.businessflows.PotentialFailure.failure;
import static io.github.theangrydev.businessflows.PotentialFailure.success;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class HappyPathTest {

    private class Sad {

    }
    private class Happy {

    }
    private class Happy2 {

    }

    private class Sad2 {

    }

    @Test
    public void sadHappyPath() {
        Sad expectedSad = new Sad();
        Sad actualSad = HappyPath.sadPath(expectedSad).ifSad().get();

        assertThat(actualSad).isEqualTo(expectedSad);
    }

    @Test
    public void technicalFailureHappyPath() {
        Exception expectedTechnicalFailure = new Exception();
        Exception actualTechnicalFailure = HappyPath.technicalFailure(expectedTechnicalFailure).ifTechnicalFailure().get();

        assertThat(actualTechnicalFailure).isEqualTo(expectedTechnicalFailure);
    }


    @Test
    public void happyPathAttemptThatSucceedsResultsInHappyPath() {
        Happy originalHappy = new Happy();

        Happy actualHappy = HappyPath.happyPathAttempt(() -> HappyPath.happyPath(originalHappy)).get();

        assertThat(actualHappy).isSameAs(originalHappy);
    }

    @Test
    public void happyPathAttemptThatFailsResultsInTechnicalFailure() {
        Exception technicalFailure = new Exception();

        Exception actualTechnicalFailure = HappyPath.happyPathAttempt(() -> {throw technicalFailure;}).ifTechnicalFailure().get();

        assertThat(actualTechnicalFailure).isSameAs(technicalFailure);
    }

    @Test
    public void happyAttemptThatSucceedsWithNoFailureMappingResultsInHappy() {
        Happy originalHappy = new Happy();

        Happy actualHappy = HappyPath.happyAttempt(() -> originalHappy).get();

        assertThat(actualHappy).isSameAs(originalHappy);
    }

    @Test
    public void happyAttemptThatSucceedsWithNoSadFailureMappingResultsInHappy() {
        Happy originalHappy = new Happy();

        Happy actualHappy = HappyPath.happyAttempt(() -> originalHappy, e -> new Sad()).get();

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
    public void happyAttemptThatFailsWithUncaughtExceptionIsASadPath() {
        Sad expectedSad = new Sad();

        Sad actualSad = HappyPath.happyAttempt(() -> {throw new Exception();}, e -> expectedSad)
                .ifSad().get();

        assertThat(actualSad).isSameAs(expectedSad);
    }

    @Test
    public void happyAttemptThatFailsWithUncaughtExceptionAndUncaughtExceptionDuringSadMappingIsATechnicalFailure() {
        Exception exceptionDuringAttempt = new Exception();

        Exception actualException = HappyPath.happyAttempt(() -> {throw new Exception();}, e -> {throw exceptionDuringAttempt; })
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
    public void attemptAllWithNoFailureRemainsHappy() {
        Happy originalHappy = new Happy();

        Happy actualHappy = HappyPath.happyPath(originalHappy)
                .attemptAll(asList(happy -> success(), happy -> success()))
                .get();

        assertThat(actualHappy).isSameAs(originalHappy);
    }

    @Test
    public void attemptWithNoFailureRemainsHappy() {
        Happy originalHappy = new Happy();

        Happy actualHappy = HappyPath.happyPath(originalHappy)
                .attempt(happy -> success())
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
    public void attemptAllWithUncaughtExceptionTurnsSad() {
        Exception uncaughtException = new Exception();

        Exception actualException = HappyPath.happyPath(new Happy())
                .attemptAll(asList(happy -> success(), happy -> {throw uncaughtException;}))
                .ifTechnicalFailure().get();

        assertThat(actualException).isSameAs(uncaughtException);
    }

    @Test
    public void attemptWithFailureTurnsSad() {
        Sad expectedSad = new Sad();

        Sad actualSad = HappyPath.<Happy, Sad>happyPath(new Happy())
                .attempt(happy -> failure(expectedSad))
                .ifSad().get();

        assertThat(actualSad).isSameAs(expectedSad);
    }

    @Test
    public void attemptAllWithFailureTurnsSad() {
        Sad expectedSad = new Sad();

        Sad actualSad = HappyPath.<Happy, Sad>happyPath(new Happy())
                .attemptAll(actions(happy -> success(), happy -> failure(expectedSad)))
                .ifSad().get();

        assertThat(actualSad).isSameAs(expectedSad);
    }

    @Test
    public void sadOperationWhenBusinessCaseIsActuallyHappy() {
        Sad2 mappedSad = new Sad2();

        Optional<Sad2> actualSad = HappyPath.<Happy, Sad>happyPath(new Happy())
                .ifSad()
                .map(sad -> mappedSad)
                .toOptional();

        assertThat(actualSad).isEmpty();
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
    public void joinHappyWithNoTechnicalFailureJoin() {
        Happy originalHappy = new Happy();

        String join = HappyPath.happyPath(originalHappy)
                .join(sad -> sad.getClass().getSimpleName(), happy -> happy.getClass().getSimpleName());

        assertThat(join).isEqualTo(originalHappy.getClass().getSimpleName());
    }

    @Test
    public void joinHappy() {
        Happy originalHappy = new Happy();

        String join = HappyPath.happyPath(originalHappy)
                .join(sad -> sad.getClass().getSimpleName(), happy -> happy.getClass().getSimpleName(), e ->  e.getClass().getSimpleName());

        assertThat(join).isEqualTo(originalHappy.getClass().getSimpleName());
    }

    @Test
    public void joinHappyThatMightBlowUp() throws Exception {
        Happy originalHappy = new Happy();

        String join = HappyPath.happyPath(originalHappy)
                .joinOrThrow(sad -> sad.getClass().getSimpleName(), happy -> happy.getClass().getSimpleName());

        assertThat(join).isEqualTo(originalHappy.getClass().getSimpleName());
    }
}
