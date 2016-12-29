package jerry.speechapi.auth;

import android.support.annotation.NonNull;

@SuppressWarnings("WeakerAccess")
public class ServiceAccountAuthentication implements Authentication {
    @NonNull
    private final String accessToken;

    ServiceAccountAuthentication(@NonNull String accessToken) {
        this.accessToken = accessToken;
    }

    @NonNull
    @Override
    public String getToken() {
        return "Bearer " + accessToken;
    }
}
