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
 * Similar to a {@link java.util.function.Function} but is allowed to throw an {@link Exception}.
 *
 * @param <Old> The old type (before mapping)
 * @param <New> The new type (after mapping)
 */
@FunctionalInterface
public interface Mapping<Old, New> {

    /**
     * @param old The old argument
     * @return The new result
     * @throws Exception If there is a technical failure during the mapping
     */
    New map(Old old) throws Exception;

    /**
     * Helper method to extend an existing {@link Mapping} by mapping the result in the successful case to a new type.
     *
     * @param after The {@link Mapping} to apply after {@link #map(Object) map(Old)} is called
     * @param <NewNew> The new type of object that will be produced in the successful case
     * @return A {@link Mapping} that will apply this and then the given mapping
     */
    default <NewNew> Mapping<Old, NewNew> andThen(Mapping<New, NewNew> after) {
        return old -> after.map(map(old));
    }
}
