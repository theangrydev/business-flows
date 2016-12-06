package api;

import io.github.theangrydev.businessflows.ApiFeature;
import io.github.theangrydev.businessflows.ApiFeatureStability;
import io.github.theangrydev.businessflows.ApiVersionHistory;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.lang.reflect.Method;

public class ApiFeatureTest implements WithAssertions {

    @Test
    public void className() throws NoSuchMethodException {
        assertThat(ApiFeature.class.getName()).isEqualTo("io.github.theangrydev.businessflows.ApiFeature");
    }

    @Test
    public void sinceMethod() throws NoSuchMethodException {
        Method since = ApiFeature.class.getDeclaredMethod("since");
        assertThat(since.getReturnType()).isEqualTo(ApiVersionHistory.class);
    }

    @Test
    public void stabilityMethod() throws NoSuchMethodException {
        Method stability = ApiFeature.class.getDeclaredMethod("stability");
        assertThat(stability.getReturnType()).isEqualTo(ApiFeatureStability.class);
    }

    @Test
    public void commentsMethod() throws NoSuchMethodException {
        Method comments = ApiFeature.class.getDeclaredMethod("comments");
        assertThat(comments.getReturnType()).isEqualTo(String.class);
    }

    @Test
    public void newMethodsAreDocumented() {
        assertThat(ApiFeature.class.getDeclaredMethods()).hasSize(3);
    }
}
