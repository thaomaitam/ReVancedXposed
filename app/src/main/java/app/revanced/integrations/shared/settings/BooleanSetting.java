package app.revanced.integrations.shared.settings;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String key, boolean value) {
        super(key, value);
    }

    public BooleanSetting(String key, boolean value, Object drop) {
        super(key, value);
    }

}
