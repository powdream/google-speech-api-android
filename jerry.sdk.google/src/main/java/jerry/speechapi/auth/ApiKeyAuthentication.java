package jerry.speechapi.auth;

import android.support.annotation.NonNull;
import android.text.TextUtils;

@SuppressWarnings("WeakerAccess")
public class ApiKeyAuthentication implements Authentication {
    @NonNull
    private final String apiKey;

    ApiKeyAuthentication(@NonNull String apiKey) {
        if (TextUtils.isEmpty(apiKey)) {
            throw new IllegalArgumentException("No valid api key is found.");
        }
        this.apiKey = apiKey;
    }

    @NonNull
    @Override
    public String getToken() {
        return apiKey;
    }
}
