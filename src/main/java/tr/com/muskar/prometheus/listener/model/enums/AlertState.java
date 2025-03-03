package tr.com.muskar.prometheus.listener.model.enums;

import lombok.Getter;

public enum AlertState {
    INACTIVE("Inactive"),
    PENDING("Pending"),
    FIRING("Firing");

    @Getter
    private final String text;

    AlertState(String text) {
        this.text = text;
    }

    public static AlertState fromValue(String text) {
        for (AlertState c : AlertState.values()) {
            if (c.text.equals(text)) {
                return c;
            }
        }
        return null;
    }
}
