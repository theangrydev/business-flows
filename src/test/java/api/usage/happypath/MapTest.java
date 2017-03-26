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
package api.usage.happypath;

import api.usage.Happy;
import api.usage.Sad;
import io.github.theangrydev.businessflows.HappyPath;
import io.github.theangrydev.businessflows.Mapping;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

public class MapTest implements WithAssertions {

    @Test
    public void ifTheUnderlyingCaseIsHappyThenTheMappingIsApplied() {
        Happy happy = new Happy("name");
        HappyPath<Happy, Sad> happyPath = HappyPath.happyPath(happy);

        HappyPath<String, Sad> mapped = happyPath.map(Happy::toString);

        assertThat(mapped.getHappy()).isEqualTo("name");
    }

    @Test
    public void exceptionsThrownDuringMappingAreTurnedIntoTechnicalFailures() {
        Exception uncaught = new Exception("boom");
        Mapping<Happy, String> mapping = happy -> {throw uncaught;};
        HappyPath<Happy, Sad> happyPath = HappyPath.happyPath(new Happy());

        HappyPath<String, Sad> mapped = happyPath.map(mapping);

        assertThat(mapped.getTechnicalFailure()).isEqualTo(uncaught);
    }

    @Test
    public void ifTheUnderlyingCaseIsSadThenTheMappingIsNotApplied() {
        Sad sad = new Sad();
        HappyPath<Happy, Sad> happyPath = HappyPath.sadPath(sad);

        HappyPath<String, Sad> mapped = happyPath.map(Happy::toString);

        assertThat(mapped.getSad()).isEqualTo(sad);
    }

    @Test
    public void ifTheUnderlyingCaseIsATechnicalFailureThenTheMappingIsNotApplied() {
        Exception technicalFailure = new Exception();
        HappyPath<Happy, Sad> happyPath = HappyPath.technicalFailure(technicalFailure);

        HappyPath<String, Sad> mapped = happyPath.map(Happy::toString);

        assertThat(mapped.getTechnicalFailure()).isEqualTo(technicalFailure);
    }
}
