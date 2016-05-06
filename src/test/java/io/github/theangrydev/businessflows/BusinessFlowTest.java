package io.github.theangrydev.businessflows;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;


public class BusinessFlowTest implements WithAssertions {

    private static class Sad {
        private final Exception exception;

        public Sad() {
            this(null);
        }

        public Sad(Exception exception) {
            this.exception = exception;
        }

        public static Sad technicalFailure(Exception exception) {
            return new Sad(exception);
        }

        public Exception exception() {
            return exception;
        }
    }

    private class Happy {

    }

    private class Happy2 {

    }

    private Happy happyAttempt() throws IOException {
        process();
        return new Happy();
    }
    private String process() throws IOException {
        return "";
    }

    private final BusinessFlows businessFlows = new BusinessFlows(exception -> System.out.println("exception = " + exception));

    @Test
    public void thenWithUnexpectedRuntimeExceptionIsATechnicalFailure() {
        businessFlows.happyAttempt(this::happyAttempt, Sad::technicalFailure);

        RuntimeException runtimeException = new RuntimeException();

        Sad actualSad = businessFlows.happyPath(new Happy(), Sad::technicalFailure)
                .then(happy -> {throw runtimeException;})
                .sadPath()
                .get();

        assertThat(actualSad.exception()).isSameAs(runtimeException);
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
    public void mapWithUnexpectedRuntimeExceptionIsATechnicalFailure() {
        RuntimeException runtimeException = new RuntimeException();

        Sad actualSad = businessFlows.happyPath(new Happy(), Sad::technicalFailure)
                .map(happy -> {throw runtimeException;})
                .sadPath()
                .get();

        assertThat(actualSad.exception()).isSameAs(runtimeException);
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

        Set<Sad> actualSads = businessFlows.happyPath(new Happy(), Sad::technicalFailure)
                .attemptAll(happy -> Optional.of(firstSad), happy -> Optional.of(secondSad))
                .sadPath()
                .get();

        assertThat(actualSads).containsOnly(firstSad, secondSad);
    }

    @Test
    public void attemptWithMultiplePassesStaysHappy() {
        Happy originalHappy = new Happy();

        Happy actualHappy = businessFlows.happyPath(originalHappy, Sad::technicalFailure)
                .attemptAll(happy -> Optional.empty(), happy -> Optional.empty())
                .get();

        assertThat(actualHappy).isSameAs(originalHappy);
    }
}
