
package wms.utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import wms.annotation.AnnotationExclusionStrategy;
import wms.common.constant.DateTimeConst;
import wms.converter.LocalDateTimeJsonConverter;


import java.time.LocalDateTime;

public class GsonUtils {

    /**
     * Convert String to Object
     *
     * @param json
     * @param cls
     * @return
     */
    public static <T> T toObject(String json, Class<T> cls) {
        return new GsonBuilder().setDateFormat(DateTimeConst.DATE_TIME_FORMAT).registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonConverter()).create().fromJson(json, cls);
    }

    /**
     * Convert String to Object
     *
     * @param json
     * @param cls
     * @return
     */
    public static <T> T toObject(JsonElement json, Class<T> cls) {
        return new GsonBuilder().setDateFormat(DateTimeConst.DATE_TIME_FORMAT).registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonConverter()).create().fromJson(json, cls);
    }

    /**
     * Convert Object to String using Json
     *
     * @param obj
     * @return
     */
    public static String toStringResult(Object obj) {
        ExclusionStrategy excludeJsonIgnore = new JsonIgnoreExclusionStrategy();
        return new GsonBuilder().setExclusionStrategies(excludeJsonIgnore).excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create().toJson(obj);
    }

    private static class JsonIgnoreExclusionStrategy implements ExclusionStrategy {
        public boolean shouldSkipClass(Class<?> clazz) {
            return clazz.getAnnotation(JsonIgnore.class) != null;
        }

        public boolean shouldSkipField(FieldAttributes f) {
            return f.getAnnotation(JsonIgnore.class) != null;
        }
    }

    /**
     * Convert Object to String using Json
     *
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonConverter())
                .setPrettyPrinting()
                .setExclusionStrategies(new AnnotationExclusionStrategy())
                .create()
                .toJson(obj);
    }

    public static String toStringCompact(Object obj) {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonConverter())
                .setPrettyPrinting()
                .create()
                .toJson(obj);
    }

}
