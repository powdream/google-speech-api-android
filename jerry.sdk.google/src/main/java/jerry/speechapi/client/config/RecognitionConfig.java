package jerry.speechapi.client.config;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import jerry.speechapi.client.JSONBuildable;
import jerry.speechapi.client.lang.SupportedLanguage;

@SuppressWarnings("WeakerAccess")
public final class RecognitionConfig implements JSONBuildable {
    @NonNull
    private final AudioEncoding encoding;

    @IntRange(from = 8000, to = 48000)
    private final int sampleRate;

    // Optional
    @Nullable
    private final String languageCode;

    // Optional
    @Nullable
    @IntRange(from = 0, to = 30)
    private final Integer maxAlternatives;

    // Optional
    @Nullable
    private final Boolean profanityFilter;

    // Optional
    @Nullable
    private final SpeechContext speechContext;

    private RecognitionConfig(@NonNull Builder builder) {
        if (builder.audioEncoding == AudioEncoding.ENCODING_UNSPECIFIED) {
            throw new IllegalArgumentException("Please choose another valid AudioEncoding - " + builder.audioEncoding);
        }
        encoding = builder.audioEncoding;
        if (builder.sampleRate < 8000 || builder.sampleRate > 48000) {
            throw new IllegalArgumentException("Please choose a valid sampleRate between 8000 and 48000. - " + builder.sampleRate);
        }
        sampleRate = builder.sampleRate;
        languageCode = builder.languageCode;
        if (builder.maxAlternatives != null &&
                (builder.maxAlternatives < 0 || builder.maxAlternatives > 30)) {
            throw new IllegalArgumentException("Please choose a valid maxAlternatives between 0 and 30. - " + builder.maxAlternatives);
        }
        maxAlternatives = builder.maxAlternatives;
        profanityFilter = builder.profanityFilter;
        speechContext = builder.speechContext;
    }

    @NonNull
    public AudioEncoding getEncoding() {
        return encoding;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    @Nullable
    public String getLanguageCode() {
        return languageCode;
    }

    @Nullable
    public Integer getMaxAlternatives() {
        return maxAlternatives;
    }

    @Nullable
    public Boolean getProfanityFilter() {
        return profanityFilter;
    }

    @Nullable
    public SpeechContext getSpeechContext() {
        return speechContext;
    }

    @Override
    public String toString() {
        return "RecognitionConfig{" +
                "audioEncoding=" + encoding +
                ", sampleRate=" + sampleRate +
                (languageCode != null ? ", languageCode='" + languageCode + '\'' : "") +
                (maxAlternatives != null ? ", maxAlternatives=" + maxAlternatives : "") +
                (profanityFilter != null ? ", profanityFilter=" + profanityFilter : "") +
                (speechContext != null ? ", speechContext=" + speechContext : "") +
                '}';
    }

    public static final class Builder {
        // Mandatory
        @NonNull
        private AudioEncoding audioEncoding = AudioEncoding.ENCODING_UNSPECIFIED;

        // Mandatory
        @IntRange(from = 8000, to = 48000)
        private int sampleRate = 16000;

        // Optional
        @Nullable
        private String languageCode;

        // Optional
        @Nullable
        @IntRange(from = 0, to = 30)
        private Integer maxAlternatives;

        // Optional
        @Nullable
        private Boolean profanityFilter;

        // Optional
        @Nullable
        private SpeechContext speechContext;

        public Builder() {
        }

        @NonNull
        public Builder setAudioEncoding(@NonNull AudioEncoding audioEncoding) {
            this.audioEncoding = audioEncoding;
            return this;
        }

        @NonNull
        public Builder setLanguageCode(@NonNull String languageCode) {
            this.languageCode = languageCode;
            return this;
        }

        @NonNull
        public Builder setLanguage(@NonNull SupportedLanguage language) {
            this.languageCode = language.getCode();
            return this;
        }

        @NonNull
        public Builder setMaxAlternatives(@IntRange(from = 0, to = 30) int maxAlternatives) {
            this.maxAlternatives = maxAlternatives;
            return this;
        }

        @NonNull
        public Builder setSampleRate(@IntRange(from = 8000, to = 48000) int sampleRate) {
            this.sampleRate = sampleRate;
            return this;
        }

        @NonNull
        public Builder setProfanityFilter(boolean profanityFilter) {
            this.profanityFilter = profanityFilter;
            return this;
        }

        @NonNull
        public Builder setSpeechContext(@NonNull SpeechContext speechContext) {
            this.speechContext = speechContext;
            return this;
        }

        @NonNull
        public RecognitionConfig build() {
            return new RecognitionConfig(this);
        }
    }
}
