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

/**
 * This type represents a failure that may occur in e.g. {@link HappyPath#attempt(ActionThatMightFail)}.
 *
 * @param <Sad> The type of sad object that represents a failure
 */
public abstract class PotentialFailure<Sad> {

    /**
     * Construct a known failure that is mapped to a {@link Sad} object.
     *
     * @param sad   The sad object that represents a failure
     * @param <Sad> The type of sad object that represents a failure
     * @return A {@link PotentialFailure} that represents a failure
     */
    public static <Sad> PotentialFailure<Sad> failure(Sad sad) {
        return new PotentialFailureFailure<>(sad);
    }

    /**
     * Construct a known success.
     *
     * @param <Sad> The type of sad object that represents a failure
     * @return A {@link PotentialFailure} that represents a success
     */
    public static <Sad> PotentialFailure<Sad> success() {
        return new PotentialFailureSuccess<>();
    }

    /**
     * Convert the {@link PotentialFailure} to a {@link HappyPath}.
     *
     * @param happy   The happy object the {@link PotentialFailure} is about
     * @param <Happy> The type of the happy object the {@link PotentialFailure} is about
     * @return A {@link HappyPath} that is happy if the {@link PotentialFailure} is a {@link PotentialFailure#success()}
     * or sad inside if the {@link PotentialFailure} is a {@link PotentialFailure#failure(Object)}
     */
    abstract <Happy> HappyPath<Happy, Sad> toHappyPath(Happy happy);

    /**
     * Take a look at the sad case (if there really is one).
     *
     * @param peek What to do if the underlying business case is sad
     * @throws Exception If the {@link Peek} throws one
     */
    abstract void ifSad(Peek<Sad> peek) throws Exception;
}
