package nullengine.client.rendering.model.block.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import nullengine.util.Direction;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static nullengine.client.rendering.model.ModelUtils.toDirectionInt;

public class ModelJsonUtils {

    public static Vector3f vector3f(JsonElement json) {
        if (json == null) {
            return null;
        }
        var array = json.getAsJsonArray();
        return new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(),
                array.get(2).getAsFloat());
    }

    public static Vector4f vector4f(JsonElement json) {
        if (json == null) {
            return null;
        }
        var array = json.getAsJsonArray();
        return new Vector4f(array.get(0).getAsFloat(), array.get(1).getAsFloat(),
                array.get(2).getAsFloat(), array.get(3).getAsFloat());
    }

    public static <E> List<E> list(JsonElement json, Function<JsonElement, E> mapper) {
        List<E> list = new ArrayList<>();
        if (json == null) {
            return list;
        }

        if (json.isJsonArray()) {
            json.getAsJsonArray().forEach(element -> list.add(mapper.apply(element)));
        } else {
            list.add(mapper.apply(json.getAsJsonObject()));
        }

        return list;
    }

    public static <E> E[] array(JsonElement json, Class<E> type, Function<JsonElement, E> mapper) {
        if (json == null) {
            return (E[]) Array.newInstance(type, 0);
        }

        if (json.isJsonArray()) {
            var jsonArray = json.getAsJsonArray();
            var array = (E[]) Array.newInstance(type, jsonArray.size());
            for (int i = 0; i < jsonArray.size(); i++) {
                array[i] = mapper.apply(jsonArray.get(i));
            }
            return array;
        } else {
            var array = (E[]) Array.newInstance(type, 1);
            array[0] = mapper.apply(json);
            return array;
        }
    }

    public static <V> Map<String, V> map(JsonElement json, Function<JsonElement, V> mapper) {
        Map<String, V> map = new HashMap<>();
        if (json == null || !json.isJsonObject()) {
            return map;
        }

        for (var entry : json.getAsJsonObject().entrySet()) {
            map.put(entry.getKey(), mapper.apply(entry.getValue()));
        }
        return map;
    }

    public static int cullFaces(JsonElement json) {
        if (json.isJsonPrimitive()) {
            return toDirectionInt(Direction.valueOf(json.getAsString().toUpperCase()));
        } else if (json.isJsonArray()) {
            var array = json.getAsJsonArray();
            int result = 0;
            for (var element : array) {
                result |= toDirectionInt(Direction.valueOf(element.getAsString().toUpperCase()));
            }
            return result;
        }
        throw new JsonParseException("Illegal cull faces");
    }
}