package de.vw.paso.client.util.preference;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import de.vw.paso.login.client.PasoClientProperties;

public class PreferenceHandler {

    private static PreferenceHandler INSTANCE;

    private final Preferences prefs;
    private final Map<Class<?>, PrefGetter<?>> getter = new HashMap<>();
    private final Map<Class<?>, PrefSetter<?>> setter = new HashMap<>();

    private PreferenceHandler() {
        prefs = Preferences.userRoot().node("paso" + PasoClientProperties.get().getStage().name().toLowerCase());

        registerGetter(Boolean.class, this::getBoolean);
        registerGetter(String.class, this::getString);
        registerGetter(Double.class, this::getDouble);
        registerGetter(Float.class, this::getFloat);
        registerGetter(Integer.class, this::getInt);
        registerGetter(Long.class, this::getLong);
        registerGetter(byte[].class, this::getByteArray);
        registerSetter(Boolean.class, this::setBoolean);
        registerSetter(String.class, this::setString);
        registerSetter(Double.class, this::setDouble);
        registerSetter(Float.class, this::setFloat);
        registerSetter(Integer.class, this::setInt);
        registerSetter(Long.class, this::setLong);
        registerSetter(byte[].class, this::setByteArray);
    }

    public static PreferenceHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PreferenceHandler();
        }

        return INSTANCE;
    }

    public void clear() throws BackingStoreException {
        prefs.clear();
    }

    private <T> void registerGetter(Class<T> type, PrefGetter<T> prefGetter) {
        getter.put(type, prefGetter);
    }

    @SuppressWarnings("unchecked")
    private <T> PrefGetter<T> getGetter(Class<T> type) {
        return (PrefGetter<T>) getter.get(type);
    }

    public <T> T get(PreferenceKeys<T> key) {
        return getGetter(key.type()).get(key);
    }

    private Boolean getBoolean(PreferenceKeys<Boolean> key) {
        return prefs.getBoolean(key.key(), key.defaultValue());
    }

    private String getString(PreferenceKeys<String> key) {
        return prefs.get(key.key(), key.defaultValue());
    }

    private Double getDouble(PreferenceKeys<Double> key) {
        return prefs.getDouble(key.key(), key.defaultValue());
    }

    private Float getFloat(PreferenceKeys<Float> key) {
        return prefs.getFloat(key.key(), key.defaultValue());
    }

    private Integer getInt(PreferenceKeys<Integer> key) {
        return prefs.getInt(key.key(), key.defaultValue());
    }

    private Long getLong(PreferenceKeys<Long> key) {
        return prefs.getLong(key.key(), key.defaultValue());
    }

    private byte[] getByteArray(PreferenceKeys<byte[]> key) {
        return prefs.getByteArray(key.key(), key.defaultValue());
    }

    private <T> void registerSetter(Class<T> type, PrefSetter<T> prefSetter) {
        setter.put(type, prefSetter);
    }

    @SuppressWarnings("unchecked")
    private <T> PrefSetter<T> getSetter(Class<T> type) {
        return (PrefSetter<T>) setter.get(type);
    }

    public <T> void set(PreferenceKeys<T> key, T value) {
        getSetter(key.type()).put(key, value);
    }

    private void setBoolean(PreferenceKeys<Boolean> key, Boolean value) {
        prefs.putBoolean(key.key(), value);
    }

    private void setString(PreferenceKeys<String> key, String value) {
        prefs.put(key.key(), value);
    }

    private void setDouble(PreferenceKeys<Double> key, Double value) {
        prefs.putDouble(key.key(), value);
    }

    private void setFloat(PreferenceKeys<Float> key, Float value) {
        prefs.putFloat(key.key(), value);
    }

    private void setInt(PreferenceKeys<Integer> key, Integer value) {
        prefs.putInt(key.key(), value);
    }

    private void setLong(PreferenceKeys<Long> key, Long value) {
        prefs.putLong(key.key(), value);
    }

    private void setByteArray(PreferenceKeys<byte[]> key, byte[] value) {
        prefs.putByteArray(key.key(), value);
    }

    private interface PrefGetter<T> {

        T get(PreferenceKeys<T> key);
    }

    private interface PrefSetter<T> {

        void put(PreferenceKeys<T> key, T value);
    }
}
