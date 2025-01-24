package app.revanced.extension.shared.settings;

public class Setting<T> {
    T value;
    public boolean rebootApp = false;

    public Setting(String key, T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void save(T newValue){
        value = newValue;
    }
}
