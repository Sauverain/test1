package com.dashboard.services;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ThemeManager {
    private static final BooleanProperty darkModeActive = new SimpleBooleanProperty(false);

    public static BooleanProperty darkModeProperty() {
        return darkModeActive;
    }

    public static boolean isDarkMode() {
        return darkModeActive.get();
    }

    public static void setDarkMode(boolean active) {
        darkModeActive.set(active);
    }
}
