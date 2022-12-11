package net.sacredlabyrinth.phaed.simpleclans.proxy.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.IOException;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ConfigurationSerializableAdapter implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!ConfigurationSerializable.class.isAssignableFrom(type.getRawType())) {
            return null;
        }
        TypeAdapter<Map> adapter = gson.getAdapter(Map.class);
        return (TypeAdapter<T>) new TypeAdapter<ConfigurationSerializable>() {
            @Override
            public void write(JsonWriter out, ConfigurationSerializable value) throws IOException {
                if (value == null) {
                    out.nullValue();
                } else {
                    adapter.write(out, value.serialize());
                }
            }

            @Override
            public ConfigurationSerializable read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) { //could be replaced with map == null?
                    in.nextNull();
                    return null;
                }
                Map map = adapter.read(in);
                return ConfigurationSerialization.deserializeObject(map,
                        (Class<? extends ConfigurationSerializable>) type.getRawType());
            }
        };
    }
}
