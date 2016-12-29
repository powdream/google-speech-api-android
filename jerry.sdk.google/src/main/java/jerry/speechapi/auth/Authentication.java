package jerry.speechapi.auth;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public interface Authentication {
    @NonNull
    String getToken();

    class Builder {
        @Nullable
        private String apiKey;

        @Nullable
        private String accessToken;

        @NonNull
        public Builder setApiKey(@NonNull String apiKey) {
            this.apiKey = apiKey;
            this.accessToken = null;
            return this;
        }

        @NonNull
        public Builder setApiKeyFromMetaData(@NonNull Context context, @NonNull String metaDataKey)
                throws PackageManager.NameNotFoundException {
            return setApiKey(context
                    .getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData
                    .getString(metaDataKey, ""));
        }

        @NonNull
        public Builder setAccessToken(@NonNull String accessToken) {
            this.accessToken = accessToken;
            this.apiKey = null;
            return this;
        }

        @NonNull
        public Authentication build() {
            if (!TextUtils.isEmpty(apiKey)) {
                return new ApiKeyAuthentication(apiKey);
            }
            if (!TextUtils.isEmpty(accessToken)) {
                return new ServiceAccountAuthentication(accessToken);
            }
            throw new IllegalStateException("Please set a valid authentication method.");
        }
    }
}
