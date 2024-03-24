package json;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class WendyJsonUtils {

    // 将Java对象序列化为JSON字符串,仅支持String和int
    public static String serialize(Object obj) {
        if (obj == null) {
            return "null";
        }

        StringBuilder jsonBuilder = new StringBuilder();
        Class<?> objClass = obj.getClass();
        jsonBuilder.append("{");

        boolean firstField = true;
        for (Field field : objClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (!firstField) {
                jsonBuilder.append(",");
            }
            try {
                if (field.getType().equals(String.class)) {
                    jsonBuilder.append(String.format("\"%s\":\"%s\"", field.getName(), field.get(obj)));
                } else {
                    jsonBuilder.append(String.format("\"%s\":%s", field.getName(), field.get(obj)));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            firstField = false;
        }

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

    // 将JSON字符串反序列化为Java对象
    public static <T> T deserialize(String json, Class<T> objClass) {
        T obj = null;
        try {
            obj = objClass.newInstance();
            json = json.substring(json.indexOf("{") + 1, json.lastIndexOf("}"));
            String[] keyValuePairs = json.split(",");

            Map<String, String> map = new HashMap<>();
            for (String pair : keyValuePairs) {
                String[] entry = pair.split(":");
                map.put(entry[0].replace("\"", "").trim(), entry[1].replace("\"", "").trim());
            }

            for (Field field : objClass.getDeclaredFields()) {
                field.setAccessible(true);
                String value = map.get(field.getName());
                if (value != null) {
                    if (field.getType().equals(int.class)) {
                        field.setInt(obj, Integer.parseInt(value));
                    } else if (field.getType().equals(String.class)) {
                        field.set(obj, value);
                    }
                    // 可以根据需要继续添加其他类型的支持
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}
