package jerry.speechapi.client.audio;

import android.net.Uri;
import android.support.annotation.NonNull;

import jerry.speechapi.client.JSONBuildable;

public class GoogleCloudStorageRecognitionAudio implements RecognitionAudio, JSONBuildable {
    @NonNull
    private final Uri uri;

    public GoogleCloudStorageRecognitionAudio(@NonNull Uri uri) {
        if (!"gs".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("Only Google Cloud Storage is supported. - " + uri);
        }
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "GoogleCloudStorageRecognitionAudio{" +
                "uri=" + uri +
                '}';
    }
}
