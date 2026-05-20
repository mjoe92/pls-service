package de.vw.paso.client.util.preference;

public record PreferenceKeys<T>(String key, Class<T> type, T defaultValue) {

    public static final PreferenceKeys<Boolean> WAS_LINK_CREATED = new PreferenceKeys<>("was_link_created",
        Boolean.class, false);
    public static final PreferenceKeys<String> LAST_SELECTED_DIRECTORY = new PreferenceKeys<>("last_selected_directory",
        String.class, System.getProperty("user.home"));
}
