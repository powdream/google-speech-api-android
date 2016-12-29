package jerry.speechapi.client.config;

import android.support.annotation.NonNull;

import java.util.Collection;

import jerry.speechapi.client.JSONBuildable;

@SuppressWarnings("WeakerAccess")
public final class SpeechContext implements JSONBuildable {
    @NonNull
    private final Collection<String> phrases;

    public SpeechContext(@NonNull Collection<String> phrases) {
        this.phrases = phrases;
    }

    @NonNull
    public Collection<String> getPhrases() {
        return phrases;
    }

    @Override
    public String toString() {
        return "SpeechContext{" +
                "phrases=" + phrases +
                '}';
    }
}

