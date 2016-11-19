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
 * This is a placeholder sad type used in the {@link HappyPath#happyAttempt(Attempt)} return type for the sad type, to
 * prevent using any of the {@link SadPath} methods, since they are not applicable yet.
 * <p>
 * This class is intentionally package private so that if you try to use e.g. {@link SadPath#map(Mapping)}, your code
 * will not compile because it won't be able to see the {@link NoSad} type.
 */
final class NoSad {
}
