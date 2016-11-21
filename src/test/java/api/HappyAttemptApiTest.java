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
package api;

import io.github.theangrydev.businessflows.HappyPath;
import org.junit.Test;

/**
 * These tests exist to prevent the failed solution to https://github.com/theangrydev/business-flows/issues/12 from
 * being attempted again in the future without realising it :)
 */
public class HappyAttemptApiTest {

    private static class Sad {}

    @Test
    public void happyAttemptReturnTypeCanBeUsedOutsideOfTheInternalPackage() {
        usageOfTheReturnType();
    }

    @Test
    public void happyAttemptCanIntroduceSadTypeViaThen() {
        usageOfTheReturnType().then(happy -> HappyPath.sadPath(new Sad()));
    }

    private HappyPath<Object, Sad> usageOfTheReturnType() {
        return HappyPath.happyAttempt(Object::new);
    }
}