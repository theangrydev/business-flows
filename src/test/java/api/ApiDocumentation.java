package api;

import io.github.theangrydev.businessflows.ApiFeature;

import java.lang.reflect.Method;

public class ApiDocumentation {
    public final Class<?> apiTest;
    public final Method apiMethod;

    private ApiDocumentation(Class<?> apiTest, Method apiMethod) {
        this.apiTest = apiTest;
        this.apiMethod = apiMethod;
    }

    public static ApiDocumentation apiDocumentation(Class<?> apiTest, Method apiMethod) {
        return new ApiDocumentation(apiTest, apiMethod);
    }

    public String addedInVersion() {
        return apiMethod.getDeclaredAnnotation(ApiFeature.class).since();
    }
}
