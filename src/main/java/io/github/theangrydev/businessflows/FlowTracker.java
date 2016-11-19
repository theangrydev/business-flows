package io.github.theangrydev.businessflows;

import java.util.HashSet;
import java.util.Set;

public class FlowTracker {
    private static final ThreadLocal<Set<Object>> FLOWS = ThreadLocal.withInitial(HashSet::new);

    public static void endFlow(Object flow) {
        if (!FLOWS.get().remove(flow)) {
            throw new IllegalStateException("Flow was already ended: " + flow);
        }
    }

    public static <T extends BusinessFlow<?, ?>> T trackFlow(T businessFlow) {
        checkForDanglingFlow();
        FLOWS.get().add(businessFlow);
        return businessFlow;
    }

    public static void checkForDanglingFlow() {
        if (!FLOWS.get().isEmpty()) {
            throw new IllegalStateException("Dangling flow(s) found: " + FLOWS.get());
        }
    }
}
