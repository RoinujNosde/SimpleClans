package net.sacredlabyrinth.phaed.simpleclans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.List;

public class Flags {

    private final JSONObject flags;

    public Flags(@Nullable String json) {
        JSONObject flags = null;
        if (json != null && !json.isEmpty()) {
            flags = (JSONObject) JSONValue.parse(json);
        }
        if (flags == null) {
            flags = new JSONObject();
        }
        this.flags = flags;
    }

    @NotNull
    public List<String> getStringList(@NotNull String key) {
        Object object = flags.get(key);
        ArrayList<String> list = new ArrayList<>();
        if (object instanceof JSONArray) {
            for (Object o : ((JSONArray) object)) {
                list.add(String.valueOf(o));
            }
        }
        return list;
    }

    @Nullable
    public String getString(@NotNull String key) {
        Object object = flags.get(key);
        if (object == null) {
            return null;
        }
        return String.valueOf(object);
    }

    @NotNull
    public String getString(@NotNull String key, @NotNull String def) {
        String string = getString(key);
        if (string == null) {
            return def;
        }
        return string;
    }

    @NotNull
    public Number getNumber(@NotNull String key) {
        Object object = flags.get(key);
        if (object instanceof Number) {
            return (Number) object;
        }
        return 0;
    }

    public boolean getBoolean(@NotNull String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(@NotNull String key, boolean def) {
        Object object = flags.get(key);
        if (object instanceof Boolean) {
            return (Boolean) object;
        }
        return def;
    }

    @SuppressWarnings("unchecked")
    public void put(@NotNull String key, @NotNull List<?> value) {
        JSONArray array = new JSONArray();
        array.addAll(value);
        flags.put(key, array);
    }

    @SuppressWarnings("unchecked")
    public void put(@NotNull String key, @NotNull Object object) {
        flags.put(key, object);
    }

    public String toJSONString() {
        return flags.toJSONString();
    }
}
