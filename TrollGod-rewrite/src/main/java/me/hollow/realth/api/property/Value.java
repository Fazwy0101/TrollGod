package me.hollow.realth.api.property;

import me.hollow.realth.client.events.ClientEvent;
import net.minecraftforge.common.MinecraftForge;

public class Value<T> {

    private final String label;

    private final T defaultValue;
    private T value;

    private T min;
    private T max;
    private boolean hasRestriction;

    public Value(String label, T defaultValue)  {
        this.label = label;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public Value(String label, T defaultValue, T min, T max)  {
        this.label = label;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.hasRestriction = true;
    }

    public final String getLabel() {
        return this.label;
    }

    public final T getValue() {
        return this.value;
    }

    public final T getMinimum() {
        return this.min;
    }

    public final T getMaximum() {
        return this.max;
    }

    public final void setValue(T value) {
        MinecraftForge.EVENT_BUS.post(new ClientEvent(this));
        if (hasRestriction) {
            T plannedValue = value;

            if (((Number) min).floatValue() > ((Number) value).floatValue()) {
                plannedValue = min;
            }

            if (((Number) max).floatValue() < ((Number) value).floatValue()) {
                plannedValue = max;
            }

            this.value = plannedValue;
            return;
        }
        this.value = value;
    }


    public <T> String getClassName(T value) {
        return value.getClass().getSimpleName();
    }

    public boolean isNumberSetting() {
        return (value instanceof Double || value instanceof Integer || value instanceof Short || value instanceof Long || value instanceof Float);
    }

    public String getType() {
        if (this.isEnumSetting()) {
            return "Enum";
        }
        return this.getClassName(this.defaultValue);
    }

    public int getEnum(String input) {
        for (int i = 0; i < this.value.getClass().getEnumConstants().length; i++) {
            final Enum e = (Enum)this.value.getClass().getEnumConstants()[i];
            if (e.name().equalsIgnoreCase(input)) {
                return i;
            }
        }
        return -1;
    }

    public void setEnumValue(String value) {
        for (Enum e : ((Enum) this.value).getClass().getEnumConstants()) {
            if (e.name().equalsIgnoreCase(value)) {
                this.value = (T)e;
            }
        }
    }

    public void increaseEnum() {
        this.value = (T) EnumConverter.increaseEnum((Enum)this.value);
        MinecraftForge.EVENT_BUS.post(new ClientEvent(this));
    }

    public void decreaseEnum() {
        this.value = (T) EnumConverter.decreaseEnum((Enum)this.value);
        MinecraftForge.EVENT_BUS.post(new ClientEvent(this));
    }

    public String currentEnumName() {
        return EnumConverter.getProperName((Enum)this.value);
    }

    public int currentEnum() {
        return EnumConverter.currentEnum((Enum)this.value);
    }

    public boolean isEnumSetting() {
        return !isNumberSetting() && !(value instanceof String) && !(value instanceof Character) && !(value instanceof Boolean);
    }

    public boolean isStringSetting() {
        return value instanceof String;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public String getValueAsString() {
        return this.value.toString();
    }

    public boolean hasRestriction() {
        return this.hasRestriction;
    }
}
