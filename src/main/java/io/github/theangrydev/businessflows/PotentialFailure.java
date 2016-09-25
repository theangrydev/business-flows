package io.github.theangrydev.businessflows;

import java.util.Optional;

import static io.github.theangrydev.businessflows.HappyPath.happyPath;

public class PotentialFailure<Sad> {

    private final Optional<Sad> result;

    private PotentialFailure(Optional<Sad> result) {
        this.result = result;
    }

    public static <Sad> PotentialFailure<Sad> failure(Sad sad) {
        return new PotentialFailure<>(Optional.of(sad));
    }

    public static <Sad> PotentialFailure<Sad> success() {
        return new PotentialFailure<>(Optional.empty());
    }

    public <Happy> HappyPath<Happy, Sad> toHappyPath(Happy happy) {
        return result.map(HappyPath::<Happy, Sad>sadPath).orElse(happyPath(happy));
    }
}
