package jerry.speechapi.client;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jerry.speechapi.client.audio.ByteRecognitionAudio;
import jerry.speechapi.client.audio.FileRecognitionAudio;
import jerry.speechapi.client.result.SpeechRecognitionResult;

public final class SpeechApiParseUtils {
    private SpeechApiParseUtils() {
        /* Prevent to make an instance */
    }

    @NonNull
    public static List<SpeechRecognitionResult> parseResults(@Nullable JSONArray jsonResults) {
        if (jsonResults == null || jsonResults.length() <= 0) {
            return Collections.emptyList();
        }

        int length = jsonResults.length();
        List<SpeechRecognitionResult> speechRecognitionResults = new ArrayList<>(length);
        for (int i = 0; i < length; ++i) {
            final JSONObject jsonSpeechRecognitionResult = jsonResults.optJSONObject(i);
            speechRecognitionResults.add(SpeechRecognitionResult.from(jsonSpeechRecognitionResult));
        }
        return Collections.unmodifiableList(speechRecognitionResults);
    }

    public static void addTo(@NonNull JSONObject jsonObject, @NonNull JSONBuildable source) throws JSONException, IOException {
        Class<? extends JSONBuildable> clazz = source.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Object rawValue = getValue(source, field);
            if (rawValue instanceof Integer ||
                    rawValue instanceof Boolean) {
                jsonObject.put(field.getName(), rawValue);
            } else if (rawValue instanceof FileRecognitionAudio) {
                FileRecognitionAudio value = (FileRecognitionAudio) rawValue;
                ByteRecognitionAudio byteRecognitionAudio = new ByteRecognitionAudio(value.getAudioFile());
                JSONObject newJsonObject = new JSONObject();
                addTo(newJsonObject, byteRecognitionAudio);
                jsonObject.put(field.getName(), newJsonObject);
            } else if (rawValue instanceof JSONBuildable) {
                JSONBuildable jsonBuildableField = (JSONBuildable) rawValue;
                JSONObject newJsonObject = new JSONObject();
                addTo(newJsonObject, jsonBuildableField);
                jsonObject.put(field.getName(), newJsonObject);
            } else if (rawValue instanceof Enum) {
                Enum<?> enumField = (Enum<?>) rawValue;
                jsonObject.put(field.getName(), enumField.name());
            } else if (rawValue instanceof byte[]) {
                byte[] value = (byte[]) rawValue;
                jsonObject.put(field.getName(), Base64.encodeToString(value, Base64.NO_WRAP));
            } else if (rawValue instanceof Collection) {
                Collection<?> value = (Collection<?>) rawValue;
                JSONArray newJsonArray = new JSONArray();
                addTo(newJsonArray, value);
                jsonObject.put(field.getName(), newJsonArray);
            } else if (rawValue != null) {
                jsonObject.put(field.getName(), rawValue.toString());
            }
        }
    }

    private static void addTo(@NonNull JSONArray jsonArray, @NonNull Collection<?> source) throws JSONException, IOException {
        for (Object element : source) {
            Class<?> clazz = element.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Class<?> fieldType = field.getType();
                if (fieldType == Integer.class || fieldType == int.class ||
                        fieldType == Long.class || fieldType == long.class ||
                        fieldType == Boolean.class || fieldType == boolean.class) {
                    Object value = getValue(source, field);
                    if (value != null) {
                        jsonArray.put(value);
                    }
                } else if (isJSONBuildable(fieldType)) {
                    JSONBuildable jsonBuildableField = getValue(source, field);
                    if (jsonBuildableField != null) {
                        JSONObject newJsonObject = new JSONObject();
                        addTo(newJsonObject, jsonBuildableField);
                        jsonArray.put(newJsonObject);
                    }
                } else if (isEnumClass(fieldType)) {
                    Enum<?> enumField = getValue(source, field);
                    jsonArray.put(enumField.name());
                } else if (fieldType == byte[].class) {
                    byte[] value = getValue(source, field);
                    if (value != null) {
                        jsonArray.put(Base64.encodeToString(value, Base64.NO_WRAP));
                    }
                } else {
                    Object value = getValue(source, field);
                    if (value != null) {
                        jsonArray.put(value.toString());
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getValue(@NonNull Object source, Field field) {
        try {
            field.setAccessible(true);
            return (T) field.get(source);
        } catch (IllegalAccessException e) {
            throw new AssertionError("No reach here.");
        }
    }

    private static boolean isEnumClass(Class<?> clazz) {
        if (clazz == null) {
            return false;
        } else if (clazz.equals(Enum.class)) {
            return true;
        } else if (clazz.equals(Object.class)) {
            return false;
        } else {
            return isEnumClass(clazz.getSuperclass());
        }
    }

    public static boolean isJSONBuildable(Class<?> clazz) {
        return isImplementing(clazz, JSONBuildable.class);
    }

    private static boolean isCollection(Class<?> clazz) {
        return isImplementing(clazz, Collection.class);
    }

    public static boolean isImplementing(Class<?> test, @NonNull Class<?> implement) {
        if (test == null) {
            return false;
        } else {
            for (Class<?> interfaceClass : test.getInterfaces()) {
                if (interfaceClass.equals(implement)) {
                    return true;
                }
            }
            return false;
        }
    }
}
