package fr.xebia.ldi.crocodilej.stream.operator;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.kafka.streams.kstream.ValueMapper;

/**
 * Created by loicmdivad.
 */
public class Mappers {

    public static final ValueMapper<String, Integer> getPrice = (String json) ->
            new Gson().fromJson(json, JsonElement.class)
                    .getAsJsonObject()
                    .get("price")
                    .getAsInt() * 100;
}
