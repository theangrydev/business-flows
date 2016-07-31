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

import java.util.Optional;

/**
 * Attempt to perform an action on a happy business case that will either:
 * <ul>
 *     <li>Succeed and return {@link Optional#empty()}</li>
 *     <li>Fail in a known way as a sad path {@link Optional#of(Object) Optional.of(Sad)}</li>
 *     <li>Result in a technical failure and throw any kind of {@link Exception}</li>
 * </ul>
 *
 * @param <Happy> The type of happy object the action will be performed on
 * @param <Sad> The type of sad object that will be returned if the action fails in a known way
 */
@FunctionalInterface
public interface ActionThatMightFail<Happy, Sad> {

    /**
     * @param happy The happy object to attempt an action on
     * @return {@link Optional#empty()} if the action succeeded, else a sad path {@link Optional#of(Object) Optional.of(Sad)}
     * @throws Exception If there was a technical failure when attempting
     */
    Optional<Sad> attempt(Happy happy) throws Exception;
}
