package jerry.speechapi.client.audio;

import android.support.annotation.NonNull;

import java.io.File;

public final class FileRecognitionAudio implements RecognitionAudio {
    @NonNull
    private final File audioFile;

    public FileRecognitionAudio(@NonNull File audioFile) {
        this.audioFile = audioFile;
    }

    @NonNull
    public File getAudioFile() {
        return audioFile;
    }

    @Override
    public String toString() {
        return "FileRecognitionAudio{" +
                "audioFile=" + audioFile +
                '}';
    }
}
