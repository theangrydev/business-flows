package io.github.theangrydev.businessflows;

/**
 * This is a description of how stable part of the API is considered to be.
 */
public enum ApiFeatureStability {

    /**
     * Considered stable.
     * Backwards compatibility will always be preserved.
     */
    STABLE,

    /**
     * Considered stable, but there is a better way of doing this now that should be used instead.
     * Backwards compatibility will always be preserved while this feature exists.
     * If the maintenance cost is too high or this feature clashes with a new feature, this feature may be removed.
     */
    DEPRECATED,

    /**
     * In the beta testing phase. Could change based on user feedback.
     */
    BETA,

    /**
     * Unstable. Could change at any time for any reason.
     */
    EXPERIMENTAL
}
