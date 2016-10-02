package io.github.theangrydev.businessflows;

/**
 * A {@link PotentialFailureSuccess} is a {@link PotentialFailure} that is actually a success.
 *
 * {@inheritDoc}
 */
class PotentialFailureSuccess<Sad> extends PotentialFailure<Sad> {

    @Override
    <Happy> HappyPath<Happy, Sad> toHappyPath(Happy happy) {
        return HappyPath.happyPath(happy);
    }
}
