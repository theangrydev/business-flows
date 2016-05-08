package io.github.theangrydev.businessflows.example;

import io.github.theangrydev.businessflows.BusinessFlow;
import io.github.theangrydev.businessflows.HappyFlow;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;



public class BusinessFlowTest implements WithAssertions {

    private class Sad {
    }

    private class Happy {

    }

    private class Happy2 {

    }

    @Test
    public void happyAttemptThatSucceedsWithTechnicalFailureMappingResultsInHappy() {
        Happy originalHappy = new Happy();

        Happy actualHappy = HappyFlow.happyAttempt(() -> originalHappy).get();

        assertThat(actualHappy).isSameAs(originalHappy);
    }

    @Test
    public void happyAttemptThatSucceedsWithNoTechnicalFailureMappingResultsInHappy() {
        Happy originalHappy = new Happy();

        Happy actualHappy = HappyFlow.happyAttempt(() -> originalHappy).get();

        assertThat(actualHappy).isSameAs(originalHappy);
    }

    @Test
    public void happyAttemptThatFailsWithNoTechnicalFailureMappingResultsInException() {
        Exception exceptionDuringAttempt = new Exception();

        Exception actualException = HappyFlow.happyAttempt(() -> {throw exceptionDuringAttempt;})
                .technicalFailure()
                .get();

        assertThat(actualException).isSameAs(exceptionDuringAttempt);
    }

    @Test
    public void happyAttemptThatFailsWithTechnicalFailureMappingResultsInSad() {
        Exception exceptionDuringAttempt = new Exception();

        Exception exception = HappyFlow.happyAttempt(() -> {throw exceptionDuringAttempt;})
                .technicalFailure()
                .get();

        assertThat(exception).isSameAs(exceptionDuringAttempt);
    }

    @Test
    public void thenWithUncaughtExceptionIsATechnicalFailure() {
        Exception exceptionDuringThen = new Exception();

        Exception exception = HappyFlow.happyPath(new Happy())
                .then(happy -> {throw exceptionDuringThen;})
                .technicalFailure()
                .get();

        assertThat(exception).isSameAs(exceptionDuringThen);
    }

    @Test
    public void thenWithPossibleSadPathThatIsSad() {
        Sad expectedSad = new Sad();

        Sad actualSad = HappyFlow.happyPath(new Happy())
                .then(happy -> BusinessFlow.sadPath(expectedSad))
                .sadPath()
                .get();

        assertThat(actualSad).isSameAs(expectedSad);
    }

    @Test
    public void thenWithPossibleHappyPathThatIsHappy() {
        Happy2 expectedHappy = new Happy2();

        Happy2 actualHappy = HappyFlow.happyPath(new Happy())
                .then(happy -> BusinessFlow.happyPath(expectedHappy))
                .get();

        assertThat(actualHappy).isSameAs(expectedHappy);
    }

    @Test
    public void mapWithUncaughtExceptionIsATechnicalFailure() {
        Exception uncaughtException = new Exception();

        Exception exception = HappyFlow.happyPath(new Happy())
                .map(happy -> {throw uncaughtException;})
                .technicalFailure()
                .get();

        assertThat(exception).isSameAs(uncaughtException);
    }

    @Test
    public void mapThatIsHappy() {
        Happy2 expectedHappy = new Happy2();

        Happy2 actualHappy = HappyFlow.happyPath(new Happy())
                .map(happy -> expectedHappy)
                .get();

        assertThat(actualHappy).isSameAs(expectedHappy);
    }

    @Test
    public void attemptWithNoFailureRemainsHappy() {
        Happy originalHappy = new Happy();

        Happy actualHappy = HappyFlow.happyPath(originalHappy)
                .attempt(happy -> Optional.empty())
                .get();

        assertThat(actualHappy).isSameAs(originalHappy);
    }

    @Test
    public void attemptWithUncaughtExceptionFailureTurnsSad() {
        Exception uncaughtException = new Exception();

        Exception exception = HappyFlow.happyPath(new Happy())
                .attempt(happy -> {throw uncaughtException;})
                .technicalFailure()
                .get();

        assertThat(exception).isSameAs(uncaughtException);
    }

    @Test
    public void attemptWithFailureTurnsSad() {
        Sad expectedSad = new Sad();

        Sad actualSad = BusinessFlow.<Sad, Happy>happyPath(new Happy())
                .attempt(happy -> Optional.of(expectedSad))
                .sadPath()
                .get();

        assertThat(actualSad).isSameAs(expectedSad);
    }

    @Test
    public void validateWithMultipleFailureTurnsSad() {
        Sad firstSad = new Sad();
        Sad secondSad = new Sad();

        List<Sad> actualSads = HappyFlow.happyPath(new Happy())
                .validate(happy -> Optional.of(firstSad), happy -> Optional.of(secondSad))
                .sadPath()
                .get();

        assertThat(actualSads).containsExactly(firstSad, secondSad);
    }

    @Test
    public void validateWithMultiplePassesStaysHappy() {
        Happy originalHappy = new Happy();

        Happy actualHappy = HappyFlow.happyPath(originalHappy)
                .validate(happy -> Optional.empty(), happy -> Optional.empty())
                .get();

        assertThat(actualHappy).isSameAs(originalHappy);
    }

    @Test
    public void sadToFail() {
        Sad originalSad = new Sad();
        Exception failure = new Exception();

        Exception actualFailure = BusinessFlow.sadPath(originalSad)
                .fail(sad -> failure)
                .technicalFailure()
                .get();

        assertThat(actualFailure).isSameAs(failure);
    }

    @Test
    public void sadPeek() {
        Sad originalSad = new Sad();
        AtomicReference<Sad> peekedSad = new AtomicReference<>();

        Sad actualSad = BusinessFlow.sadPath(originalSad)
                .sadPath()
                .peek(peekedSad::set)
                .sadPath()
                .get();

        assertThat(peekedSad.get()).isSameAs(originalSad);
        assertThat(actualSad).isSameAs(originalSad);
    }

    @Test
    public void sadPeekWithUncaughtExceptionFailureTurnsSad() {
        Exception uncaughtException = new Exception();

        Exception exception = BusinessFlow.sadPath(new Sad())
                .sadPath()
                .peek(happy -> {throw uncaughtException;})
                .technicalFailure()
                .get();

        assertThat(exception).isSameAs(uncaughtException);
    }

    @Test
    public void peekWithNoFailureRemainsHappy() {
        Happy originalHappy = new Happy();
        AtomicReference<Happy> peekedHappy = new AtomicReference<>();

        Happy actualHappy = HappyFlow.happyPath(originalHappy)
                .peek(peekedHappy::set)
                .get();

        assertThat(peekedHappy.get()).isSameAs(originalHappy);
        assertThat(actualHappy).isSameAs(originalHappy);
    }

    @Test
    public void peekWithUncaughtExceptionFailureTurnsSad() {
        Exception uncaughtException = new Exception();

        Exception exception = HappyFlow.happyPath(new Happy())
                .peek(happy -> {throw uncaughtException;})
                .technicalFailure()
                .get();

        assertThat(exception).isSameAs(uncaughtException);
    }

    @Test
    public void joinHappy() {
        Happy originalHappy = new Happy();

        String join = BusinessFlow.happyPath(originalHappy)
                .join(happy -> happy.getClass().getSimpleName(), sad -> sad.getClass().getSimpleName(), e ->  e.getClass().getSimpleName());

        assertThat(join).isEqualTo(originalHappy.getClass().getSimpleName());
    }

    @Test
    public void joinSad() {
        Sad originalSad = new Sad();

        String join = BusinessFlow.sadPath(originalSad)
                .join(happy -> happy.getClass().getSimpleName(), sad -> sad.getClass().getSimpleName(), e -> e.getClass().getSimpleName());

        assertThat(join).isEqualTo(originalSad.getClass().getSimpleName());
    }

    @Test
    public void joinException() {
        IllegalStateException technicalFailure = new IllegalStateException();

        String join = BusinessFlow.technicalFailure(technicalFailure)
                .join(happy -> happy.getClass().getSimpleName(), sad -> sad.getClass().getSimpleName(), e -> e.getClass().getSimpleName());

        assertThat(join).isEqualTo(technicalFailure.getClass().getSimpleName());
    }
}
