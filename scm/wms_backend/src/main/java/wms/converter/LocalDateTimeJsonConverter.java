package wms.converter;

import com.google.gson.*;
import wms.common.constant.DateTimeConst;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeJsonConverter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeConst.DATE_TIME_FORMAT);

    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(localDateTime.format(dateTimeFormatter));
    }

    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return LocalDateTime.parse(json.getAsString(), dateTimeFormatter);
    }
}