package jerry.speechapi.client.audio;

import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jerry.speechapi.client.JSONBuildable;

public final class ByteRecognitionAudio implements RecognitionAudio, JSONBuildable {
    @NonNull
    private final byte[] content;

    public ByteRecognitionAudio(@NonNull byte[] content) {
        this.content = content;
    }

    public ByteRecognitionAudio(@NonNull File file) throws IOException {
        this(readAudioData(file));
    }

    @Override
    public String toString() {
        String encodedContent = Base64.encodeToString(content, Base64.NO_WRAP);
        if (encodedContent.length() > 64) {
            encodedContent = encodedContent.substring(0, 64) + "...";
        }
        return "ByteRecognitionAudio{" +
                "content=" + encodedContent +
                '}';
    }

    private static byte[] readAudioData(@NonNull File audioFile) throws IOException {
        Closeable closeable = null;
        try {
            InputStream in = new FileInputStream(audioFile);
            closeable = in;
            BufferedInputStream bufferedIn = new BufferedInputStream(in);
            closeable = bufferedIn;

            byte[] data = new byte[(int) audioFile.length()];
            final int loadedDataSize = bufferedIn.read(data);
            if (loadedDataSize != audioFile.length()) {
                throw new IOException("File size doesn't match with the loaded data size. fileSize="
                        + audioFile.length() + ", loadedDataSize=" + loadedDataSize);
            }

            return data;
        } finally {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException ignore) {
                }
            }
        }
    }
}
