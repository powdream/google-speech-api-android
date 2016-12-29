package jerry.speechapi.service;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import jerry.speechapi.client.SpeechApiParseUtils;
import jerry.speechapi.client.result.SpeechRecognitionResult;
import okhttp3.ResponseBody;
import retrofit2.Converter;

class SpeechRecognitionResultConverter implements Converter<ResponseBody, List<SpeechRecognitionResult>> {
    @Override
    public List<SpeechRecognitionResult> convert(ResponseBody value) throws IOException {
        try {
            JSONObject response = new JSONObject(value.string());
            return SpeechApiParseUtils.parseResults(response.optJSONArray("results"));
        } catch (JSONException e) {
            throw new IOException("Response conversion error: " + value.string(), e);
        }
    }
}
