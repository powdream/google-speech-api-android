package jerry.speechapi.client.audio;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

public interface RecognitionAudio {
    class Builder {
        @Nullable
        private Uri uri;

        @Nullable
        private File audioFile;

        @Nullable
        private byte[] audioData;

        @NonNull
        public Builder setUri(@NonNull Uri uri) {
            this.uri = uri;
            audioData = null;
            audioFile = null;
            return this;
        }

        @NonNull
        public Builder setAudioFile(@NonNull File audioFile) {
            this.audioFile = audioFile;
            uri = null;
            audioData = null;
            return this;
        }

        @NonNull
        public Builder setAudioData(@NonNull byte[] audioData) {
            this.audioData = audioData;
            uri = null;
            audioFile = null;
            return this;
        }

        @NonNull
        public RecognitionAudio build() {
            if (uri != null) {
                return new GoogleCloudStorageRecognitionAudio(uri);
            }
            if (audioData != null) {
                return new ByteRecognitionAudio(audioData);
            }
            if (audioFile != null) {
                return new FileRecognitionAudio(audioFile);
            }
            throw new IllegalStateException("Please set any audio source.");
        }
    }
}
