package jerry.speechapi.service;

import android.util.LruCache;

import jerry.speechapi.auth.ApiKeyAuthentication;
import jerry.speechapi.auth.Authentication;
import jerry.speechapi.auth.ServiceAccountAuthentication;

final class AuthenticationSpeechApiBridgeCache extends LruCache<Class<? extends Authentication>, AuthenticationSpeechApiBridge> {
    AuthenticationSpeechApiBridgeCache() {
        super(2);
    }

    @Override
    protected AuthenticationSpeechApiBridge create(Class<? extends Authentication> key) {
        if (ApiKeyAuthentication.class.equals(key)) {
            return new ApiKeyAuthenticationSpeechApiBridge();
        } else if (ServiceAccountAuthentication.class.equals(key)) {
            return new ServiceAccountAuthenticationSpeechApiBridge();
        }
        throw new IllegalArgumentException("Unsupported authentication method. - " + key);
    }
}
