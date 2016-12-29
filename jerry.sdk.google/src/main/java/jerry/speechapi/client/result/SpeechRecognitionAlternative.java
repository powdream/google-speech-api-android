package jerry.speechapi.client.result;

import android.support.annotation.NonNull;

import org.json.JSONObject;

@SuppressWarnings("WeakerAccess")
public final class SpeechRecognitionAlternative {
    @NonNull
    private final String transcript;

    private final double confidence;

    private SpeechRecognitionAlternative(@NonNull String transcript, double confidence) {
        this.transcript = transcript;
        this.confidence = confidence;
    }

    @NonNull
    public String getTranscript() {
        return transcript;
    }

    public double getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return "SpeechRecognitionAlternative{" +
                "transcript='" + transcript + '\'' +
                ", confidence=" + confidence +
                '}';
    }

    @NonNull
    static SpeechRecognitionAlternative from(@NonNull JSONObject jsonObject) {
        return new SpeechRecognitionAlternative(
                jsonObject.optString("transcript", ""),
                jsonObject.optDouble("confidence", 0.0)
        );
    }
}
