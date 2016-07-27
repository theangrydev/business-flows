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
import java.util.function.Function;

interface BusinessCase<Happy, Sad> {
    <Result> Result join(Function<Happy, Result> happyJoiner, Function<Sad, Result> sadJoiner, Function<Exception, Result> technicalFailureJoiner);
    <Result> Result join(Function<Happy, Result> happyJoiner, Function<Sad, Result> sadJoiner) throws Exception;
    Optional<Happy> happyOptional();
    Optional<Sad> sadOptional();
    Optional<Exception> technicalFailureOptional();
}
