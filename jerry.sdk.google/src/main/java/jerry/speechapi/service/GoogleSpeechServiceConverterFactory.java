package jerry.speechapi.service;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jerry.speechapi.client.JSONBuildable;
import jerry.speechapi.client.SpeechApiParseUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.ByteString;
import retrofit2.Converter;
import retrofit2.Retrofit;

class GoogleSpeechServiceConverterFactory extends Converter.Factory {
    private static final MediaType CONTENT_TYPE = MediaType.parse("application/json");

    private final Map<Type, Converter<ResponseBody, ?>> responseBodyConverterCache = new ConcurrentHashMap<>();
    private final Map<Type, Converter<?, RequestBody>> requestBodyConverterCache = new ConcurrentHashMap<>();

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        Converter<ResponseBody, ?> responseBodyConverter = responseBodyConverterCache.get(type);
        if (responseBodyConverter != null) {
            return responseBodyConverter;
        }

        switch (type.toString()) {
            case "java.util.List<jerry.speechapi.client.result.SpeechRecognitionResult>": {
                responseBodyConverter = new SpeechRecognitionResultConverter();
                responseBodyConverterCache.put(type, responseBodyConverter);
                return responseBodyConverter;
            }

            case "java.util.List<jerry.speechapi.client.lang.SupportedLanguage>": {
                responseBodyConverter = new SupportedLanguageConverter();
                responseBodyConverterCache.put(type, responseBodyConverter);
                return responseBodyConverter;
            }

            default:
                return null;
        }
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        Converter<?, RequestBody> requestBodyConverter = requestBodyConverterCache.get(type);
        if (requestBodyConverter != null) {
            return requestBodyConverter;
        }

        if (type instanceof Class && SpeechApiParseUtils.isJSONBuildable((Class<?>) type)) {
            requestBodyConverter = new Converter<JSONBuildable, RequestBody>() {
                @Override
                public RequestBody convert(JSONBuildable value) throws IOException {
                    JSONObject requestJsonObject = new JSONObject();
                    try {
                        SpeechApiParseUtils.addTo(requestJsonObject, value);
                    } catch (JSONException e) {
                        throw new IOException("Request conversion error: " + value, e);
                    }
                    return RequestBody.create(CONTENT_TYPE, ByteString.encodeUtf8(requestJsonObject.toString()));
                }
            };
            requestBodyConverterCache.put(type, requestBodyConverter);
            return requestBodyConverter;
        }

        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }

}
