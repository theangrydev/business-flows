package io.github.theangrydev.businessflows;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class BusinessFlowTest implements WithAssertions {

    private static class Sad {
        private final Exception exception;

        Sad() {
            this(null);
        }

        Sad(Exception exception) {
            this.exception = exception;
        }

        static Sad technicalFailure(Exception exception) {
            return new Sad(exception);
        }
    }

    private class Happy {

    }

    private class Happy2 {

    }

    private final UncaughtExceptionHandler uncaughtExceptionHandler = mock(UncaughtExceptionHandler.class);
    private final BusinessFlows businessFlows = new BusinessFlows(uncaughtExceptionHandler);

    @Test
    public void happyAttemptThatSucceedsWithTechnicalFailureMappingResultsInHappy() {
        Happy originalHappy = new Happy();

        Happy actualHappy = businessFlows.happyAttempt(() -> originalHappy, Sad::technicalFailure).get();

        assertThat(actualHappy).isSameAs(originalHappy);
    }

    @Test
    public void happyAttemptThatSucceedsWithNoTechnicalFailureMappingResultsInHappy() {
        Happy originalHappy = new Happy();

        Happy actualHappy = businessFlows.happyAttempt(() -> originalHappy).get();

        assertThat(actualHappy).isSameAs(originalHappy);
    }

    @Test
    public void happyAttemptThatFailsWithNoTechnicalFailureMappingResultsInException() {
        Exception exceptionDuringAttempt = new Exception();

        Exception actualException = businessFlows.happyAttempt(() -> {throw exceptionDuringAttempt;})
                .sadPath()
                .get();

        assertThat(actualException).isSameAs(exceptionDuringAttempt);
    }

    @Test
    public void happyAttemptThatFailsWithTechnicalFailureMappingResultsInSad() {
        Exception exceptionDuringAttempt = new Exception();

        Sad sad = businessFlows.happyAttempt(() -> {throw exceptionDuringAttempt;}, Sad::technicalFailure)
                .sadPath()
                .get();

        assertThat(sad.exception).isSameAs(exceptionDuringAttempt);
    }

    @Test
    public void thenWithUncaughtExceptionIsATechnicalFailure() {
        Exception exception = new Exception();

        Sad actualSad = businessFlows.happyPath(new Happy(), Sad::technicalFailure)
                .then(happy -> {throw exception;})
                .sadPath()
                .get();

        verify(uncaughtExceptionHandler).handle(exception);
        assertThat(actualSad.exception).isSameAs(exception);
    }

    @Test
    public void thenWithPossibleSadPathThatIsSad() {
        Sad expectedSad = new Sad();

        Sad actualSad = businessFlows.happyPath(new Happy(), Sad::technicalFailure)
                .then(happy -> businessFlows.sadPath(expectedSad, Sad::technicalFailure))
                .sadPath()
                .get();

        assertThat(actualSad).isSameAs(expectedSad);
    }

    @Test
    public void thenWithPossibleHappyPathThatIsHappy() {
        Happy2 expectedHappy = new Happy2();

        Happy2 actualHappy = businessFlows.happyPath(new Happy(), Sad::technicalFailure)
                .then(happy -> businessFlows.happyPath(expectedHappy, Sad::technicalFailure))
                .get();

        assertThat(actualHappy).isSameAs(expectedHappy);
    }

    @Test
    public void mapWithUncaughtExceptionIsATechnicalFailure() {
        Exception uncaughtException = new Exception();

        Sad actualSad = businessFlows.happyPath(new Happy(), Sad::technicalFailure)
                .map(happy -> {throw uncaughtException;})
                .sadPath()
                .get();

        verify(uncaughtExceptionHandler).handle(uncaughtException);
        assertThat(actualSad.exception).isSameAs(uncaughtException);
    }

    @Test
    public void mapThatIsHappy() {
        Happy2 expectedHappy = new Happy2();

        Happy2 actualHappy = businessFlows.happyPath(new Happy(), Sad::technicalFailure)
                .map(happy -> expectedHappy)
                .get();

        assertThat(actualHappy).isSameAs(expectedHappy);
    }


    @Test
    public void attemptWithNoFailureRemainsHappy() {
        Happy originalHappy = new Happy();

        Happy actualHappy = businessFlows.happyPath(originalHappy, Sad::technicalFailure)
                .attempt(happy -> Optional.empty())
                .get();

        assertThat(actualHappy).isSameAs(originalHappy);
    }

    @Test
    public void attemptWithUncaughtExceptionFailureTurnsSad() {
        Exception uncaughtException = new Exception();

        Sad actualSad = businessFlows.happyPath(new Happy(), Sad::technicalFailure)
                .attempt(happy -> {throw uncaughtException;})
                .sadPath()
                .get();

        verify(uncaughtExceptionHandler).handle(uncaughtException);
        assertThat(actualSad.exception).isSameAs(uncaughtException);
    }

    @Test
    public void attemptWithFailureTurnsSad() {
        Sad expectedSad = new Sad();

        Sad actualSad = businessFlows.happyPath(new Happy(), Sad::technicalFailure)
                .attempt(happy -> Optional.of(expectedSad))
                .sadPath()
                .get();

        assertThat(actualSad).isSameAs(expectedSad);
    }

    @Test
    public void attemptWithMultipleFailureTurnsSad() {
        Sad firstSad = new Sad();
        Sad secondSad = new Sad();

        List<Sad> actualSads = businessFlows.happyPath(new Happy(), Sad::technicalFailure)
                .validate(happy -> Optional.of(firstSad), happy -> Optional.of(secondSad))
                .sadPath()
                .get();

        assertThat(actualSads).containsExactly(firstSad, secondSad);
    }

    @Test
    public void attemptWithMultiplePassesStaysHappy() {
        Happy originalHappy = new Happy();

        Happy actualHappy = businessFlows.happyPath(originalHappy, Sad::technicalFailure)
                .validate(happy -> Optional.empty(), happy -> Optional.empty())
                .get();

        assertThat(actualHappy).isSameAs(originalHappy);
    }

    @Test
    public void peekWithNoFailureRemainsHappy() {
        Happy originalHappy = new Happy();
        AtomicReference<Happy> peekedHappy = new AtomicReference<>();

        Happy actualHappy = businessFlows.happyPath(originalHappy, Sad::technicalFailure)
                .peek(peekedHappy::set)
                .get();

        assertThat(peekedHappy.get()).isSameAs(originalHappy);
        assertThat(actualHappy).isSameAs(originalHappy);
    }

    @Test
    public void peekWithUncaughtExceptionFailureTurnsSad() {
        Exception uncaughtException = new Exception();

        Sad actualSad = businessFlows.happyPath(new Happy(), Sad::technicalFailure)
                .peek(happy -> {throw uncaughtException;})
                .sadPath()
                .get();

        verify(uncaughtExceptionHandler).handle(uncaughtException);
        assertThat(actualSad.exception).isSameAs(uncaughtException);
    }

    @Test
    public void joinHappy() {
        Happy originalHappy = new Happy();

        String join = businessFlows.happyPath(originalHappy, Sad::technicalFailure)
                .join(happy -> happy.getClass().getSimpleName(), sad -> sad.getClass().getSimpleName());

        assertThat(join).isEqualTo(originalHappy.getClass().getSimpleName());
    }

    @Test
    public void joinSad() {
        Sad originalSad = new Sad();

        String join = businessFlows.sadPath(originalSad, Sad::technicalFailure)
                .join(happy -> happy.getClass().getSimpleName(), sad -> sad.getClass().getSimpleName());

        assertThat(join).isEqualTo(originalSad.getClass().getSimpleName());
    }
}
