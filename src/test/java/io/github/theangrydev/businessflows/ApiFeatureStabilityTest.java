package io.github.theangrydev.businessflows;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import static io.github.theangrydev.businessflows.ApiFeatureStability.*;

public class ApiFeatureStabilityTest implements WithAssertions {

    @Test
    public void constantNames() {
        assertThat(BETA).hasToString("BETA");
        assertThat(DEPRECATED).hasToString("DEPRECATED");
        assertThat(STABLE).hasToString("STABLE");
        assertThat(EXPERIMENTAL).hasToString("EXPERIMENTAL");
    }

    @Test
    public void className() {
        assertThat(ApiFeatureStability.class.getName()).isEqualTo("io.github.theangrydev.businessflows.ApiFeatureStability");
    }
}