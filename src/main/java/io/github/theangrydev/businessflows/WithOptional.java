/*
 * Copyright 2016-2017 Liam Williams <liam.williams@zoho.com>.
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
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.github.theangrydev.businessflows.ApiFeatureStability.STABLE;
import static io.github.theangrydev.businessflows.ApiVersionHistory.*;
import static java.lang.String.format;

/**
 * This class provides convenience methods for accessing an {@link Optional} that would otherwise be obtained by calling
 * the {@link WithOptional#toOptional()} method.
 *
 * @param <Content> The type of content that may be held
 */
@ApiFeature(since = VERSION_7_6_0, stability = STABLE)
@FunctionalInterface
public interface WithOptional<Content> {

    /**
     * @return The {@link Optional} {@link Content}
     */
    @ApiFeature(since = VERSION_7_6_0, stability = STABLE)
    Optional<Content> toOptional();

    /**
     * @return If the {@link Content} is present then the {@link Content}, else an {@link IllegalStateException}
     * @throws IllegalStateException If the underlying {@link Content} is not present
     */
    @ApiFeature(since = VERSION_7_6_0, stability = STABLE)
    default Content get() {
        return orElseThrow(() -> new IllegalStateException(format("Not present. This is: '%s'.", this)));
    }

    /**
     * @param alternative The result if the {@link Content} is not present
     * @return If {@link Content} is present then the {@link Content}, else the given alternative
     */
    @ApiFeature(since = VERSION_7_6_0, stability = STABLE)
    default Content orElse(Content alternative) {
        return toOptional().orElse(alternative);
    }

    /**
     * @param alternativeSupplier The supplier of the alternative result if the {@link Content} is not present
     * @return If the {@link Content} is present then the {@link Content}, else the given alternative
     */
    @ApiFeature(since = VERSION_7_6_0, stability = STABLE)
    default Content orElseGet(Supplier<Content> alternativeSupplier) {
        return toOptional().orElseGet(alternativeSupplier);
    }

    /**
     * Take a look at the content (if it is present).
     *
     * @param consumer What to do if the {@link Content} is present
     */
    @ApiFeature(since = VERSION_7_6_0, stability = STABLE)
    default void ifPresent(Consumer<Content> consumer) {
        toOptional().ifPresent(consumer);
    }

    /**
     * @param exceptionSupplier The supplier of the exception to be thrown if the {@link Content} is not present
     * @param <X>               The type of {@link Exception} that will be thrown if the {@link Content} is not present
     * @return The {@link Content} if the {@link Content} is present
     * @throws X If the {@link Content} is not present
     */
    @ApiFeature(since = VERSION_7_6_0, stability = STABLE)
    default <X extends Exception> Content orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return toOptional().orElseThrow(exceptionSupplier);
    }

    /**
     * @return true if the {@link Content} is present, false otherwise.
     */
    @ApiFeature(since = VERSION_8_2_0, stability = STABLE)
    default boolean isPresent() {
        return toOptional().isPresent();
    }
}
