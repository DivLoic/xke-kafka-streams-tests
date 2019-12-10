package fr.xebia.ldi.crocodile.streams.operator;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.jasongoodwin.monads.Try;
import org.apache.kafka.streams.kstream.ValueMapper;

/**
 * Created by loicmdivad.
 */
public class Mappers {

    public static final ValueMapper<String, Integer> getPrice = (String json) -> Try.ofFailable(() ->
                    new Double(
                            new Gson().fromJson(json, JsonElement.class)
                                    .getAsJsonObject()
                                    .get("price")
                                    .getAsDouble() * 100
                    ).intValue()
    ).orElse(-1);
}
