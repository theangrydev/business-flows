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

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.String.format;


public class BusinessFlowsTest implements WithAssertions {

    private class Sad {
    }

    private class Sad2 {
    }

    private class Happy {

    }

    private class Happy2 {

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
    public void getBiasThatIsPresentReturnsIt() {
        Happy expectedHappy = new Happy();

        Happy actualHappy = HappyPath.happyPath(expectedHappy)
                .get();

        assertThat(actualHappy).isSameAs(expectedHappy);
    }

    @Test
    public void getBiasThatIsNotPresentThrowsIllegalStateException() {
        Sad sad = new Sad();
        assertThatThrownBy(() -> SadPath.sadPath(sad).ifHappy().get())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage(format("Not present. Business case is: 'Sad: %s'.", sad));
    }

    @Test
    public void orElseBiasThatIsNotPresentReturnsTheAlternative() {
        Happy expectedHappy = new Happy();
        Happy actualHappy = SadPath.<Happy, Sad>sadPath(new Sad()).ifHappy().orElse(expectedHappy);

        assertThat(actualHappy).isEqualTo(expectedHappy);
    }

    @Test
    public void orElseGetBiasThatIsNotPresentReturnsTheAlternative() {
        Happy expectedHappy = new Happy();
        Happy actualHappy = SadPath.<Happy, Sad>sadPath(new Sad()).ifHappy().orElseGet(() -> expectedHappy);

        assertThat(actualHappy).isEqualTo(expectedHappy);
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
    public void thenWithPossibleSadPathThatIsSad() {
        Sad expectedSad = new Sad();

        Sad actualSad = HappyPath.<Happy, Sad>happyPath(new Happy())
                .then(happy -> SadPath.sadPath(expectedSad))
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

        Sad actualSad = HappyPath.<Happy, Sad>happyPath(new Happy())
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
    public void sadPeek() {
        Sad originalSad = new Sad();
        AtomicReference<Sad> peekedSad = new AtomicReference<>();

        Sad actualSad = SadPath.sadPath(originalSad)
                .ifSad().peek(peekedSad::set)
                .get();

        assertThat(peekedSad.get()).isSameAs(originalSad);
        assertThat(actualSad).isSameAs(originalSad);
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

    @Test
    public void sadRecovery() {
        Happy expectedHappy = new Happy();

        Happy actualHappy = SadPath.<Happy, Sad>sadPath(new Sad())
                .recover(sad -> expectedHappy)
                .get();

        assertThat(actualHappy).isSameAs(expectedHappy);
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
    public void technicalFailureMapToSadPath() {
        Sad sad = new Sad();

        Sad actualSad = TechnicalFailure.<Happy, Sad>technicalFailure(new Exception())
                .mapToSadPath(exception -> sad)
                .get();

        assertThat(actualSad).isSameAs(sad);
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
    public void technicalFailureMap() {
        Exception mappedTechnicalFailure = new Exception();

        Exception actualTechnicalFailure = TechnicalFailure.technicalFailure(new Exception())
                .map(sad -> mappedTechnicalFailure)
                .get();

        assertThat(actualTechnicalFailure).isSameAs(mappedTechnicalFailure);
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
    public void sadPeekWithUncaughtExceptionIsATechnicalFailure() {
        Exception uncaughtException = new Exception();

        Exception actualException = SadPath.sadPath(new Sad())
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
                .join(sad -> sad.getClass().getSimpleName(), happy -> happy.getClass().getSimpleName(), e ->  e.getClass().getSimpleName());

        assertThat(join).isEqualTo(originalHappy.getClass().getSimpleName());
    }

    @Test
    public void joinHappyThatMightBlowUp() throws Exception {
        Happy originalHappy = new Happy();

        String join = HappyPath.happyPath(originalHappy)
                .join(sad -> sad.getClass().getSimpleName(), happy -> happy.getClass().getSimpleName());

        assertThat(join).isEqualTo(originalHappy.getClass().getSimpleName());
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
                .join(sad -> sad.getClass().getSimpleName(), happy -> happy.getClass().getSimpleName());

        assertThat(join).isEqualTo(originalSad.getClass().getSimpleName());
    }

    @Test
    public void joinTechnicalFailure() {
        IllegalStateException failure = new IllegalStateException();

        String join = TechnicalFailure.technicalFailure(failure)
                .join(sad -> sad.getClass().getSimpleName(), happy -> happy.getClass().getSimpleName(), e -> e.getClass().getSimpleName());

        assertThat(join).isEqualTo(failure.getClass().getSimpleName());
    }

    @Test
    public void joinTechnicalThatBlowsUp() {
        IllegalStateException failure = new IllegalStateException();

        assertThatThrownBy(() -> technicalFailureJoinThatBlowsUpWith(failure)).isEqualTo(failure);
    }

    private String technicalFailureJoinThatBlowsUpWith(IllegalStateException failure) throws Exception {
        return TechnicalFailure.technicalFailure(failure)
                .join(sad -> sad.getClass().getSimpleName(), happy -> happy.getClass().getSimpleName());
    }
}
