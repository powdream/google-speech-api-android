package jerry.speechapi.client.result;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SpeechRecognitionResult {
    @NonNull
    private final List<SpeechRecognitionAlternative> alternatives;

    private SpeechRecognitionResult(@NonNull List<SpeechRecognitionAlternative> alternatives) {
        this.alternatives = alternatives;
    }

    @NonNull
    public List<SpeechRecognitionAlternative> getAlternatives() {
        return alternatives;
    }

    @Override
    public String toString() {
        return "SpeechRecognitionResult{" +
                "alternatives=" + alternatives +
                '}';
    }

    @NonNull
    public static SpeechRecognitionResult from(@NonNull JSONObject jsonResponse) {
        JSONArray results = jsonResponse.optJSONArray("alternatives");
        if (results == null) {
            return new SpeechRecognitionResult(
                    Collections.<SpeechRecognitionAlternative>emptyList()
            );
        }

        List<SpeechRecognitionAlternative> alternatives = new ArrayList<>(results.length());
        final int length = results.length();
        for (int i = 0; i < length; ++i) {
            final JSONObject jsonAlternative = results.optJSONObject(i);
            if (jsonAlternative != null) {
                alternatives.add(SpeechRecognitionAlternative.from(jsonAlternative));
            }
        }
        return new SpeechRecognitionResult(Collections.unmodifiableList(alternatives));
    }
}
