package io.github.theangrydev.businessflows;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Documentation about a feature of the API.
 */
@Documented
@Target({TYPE, METHOD, CONSTRUCTOR})
@Retention(RUNTIME)
public @interface ApiFeature {

    /**
     * @return The version that this feature was added.
     */
    String since();

    /**
     * @return How stable this feature is.
     */
    ApiFeatureStability stability();

    /**
     * @return Comments about the feature (e.g. if it is deprecated, what else to use instead).
     */
    String comments() default "";
}
