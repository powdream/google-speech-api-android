package jerry.speechapi.client;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import jerry.speechapi.client.audio.RecognitionAudio;
import jerry.speechapi.client.config.RecognitionConfig;

public final class RecognitionRequest implements JSONBuildable {
    @NonNull
    private final RecognitionConfig config;

    @NonNull
    private final RecognitionAudio audio;

    public static final class Builder {
        @Nullable
        private RecognitionConfig config;

        @Nullable
        private RecognitionAudio audio;

        @NonNull
        public Builder setConfig(@NonNull RecognitionConfig config) {
            this.config = config;
            return this;
        }

        @NonNull
        public Builder setAudio(@NonNull RecognitionAudio audio) {
            this.audio = audio;
            return this;
        }

        @NonNull
        public RecognitionRequest build() {
            return new RecognitionRequest(this);
        }
    }

    private RecognitionRequest(@NonNull Builder builder) {
        if (builder.config == null) {
            throw new IllegalStateException("no 'config'.");
        }
        if (builder.audio == null) {
            throw new IllegalStateException("no 'audio'.");
        }
        this.config = builder.config;
        this.audio = builder.audio;
    }

    @NonNull
    public RecognitionConfig getConfig() {
        return config;
    }

    @NonNull
    public RecognitionAudio getAudio() {
        return audio;
    }

    @Override
    public String toString() {
        return "RecognitionRequest{" +
                "config=" + config +
                ", audio=" + audio +
                '}';
    }
}
