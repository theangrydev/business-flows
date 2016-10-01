package io.github.theangrydev.businessflows;

class PotentialFailureSuccess<Sad> extends PotentialFailure<Sad> {

    @Override
    <Happy> HappyPath<Happy, Sad> toHappyPath(Happy happy) {
        return HappyPath.happyPath(happy);
    }
}
