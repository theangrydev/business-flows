package io.github.theangrydev.businessflows;

class PotentialFailureFailure<Sad> extends PotentialFailure<Sad> {

    private final Sad sad;

    PotentialFailureFailure(Sad sad) {
        this.sad = sad;
    }

    @Override
    <Happy> HappyPath<Happy, Sad> toHappyPath(Happy happy) {
        return HappyPath.sadPath(sad);
    }
}
