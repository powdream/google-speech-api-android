package jerry.speechapi.service;

import android.support.annotation.NonNull;

import java.util.List;

import jerry.speechapi.auth.Authentication;
import jerry.speechapi.client.RecognitionRequest;
import jerry.speechapi.client.lang.SupportedLanguage;
import jerry.speechapi.client.result.SpeechRecognitionResult;
import retrofit2.Call;
import rx.Single;

final class GoogleSpeechServiceImpl implements GoogleSpeechService {
    @NonNull
    private final SpeechApi speechApi;

    @NonNull
    private final SupportedLanguageApi supportedLanguageApi;

    @NonNull
    private final AuthenticationSpeechApiBridgeCache authenticationSpeechApiBridgeCache;

    GoogleSpeechServiceImpl(
            @NonNull SpeechApi speechApi,
            @NonNull SupportedLanguageApi supportedLanguageApi,
            @NonNull AuthenticationSpeechApiBridgeCache authenticationSpeechApiBridgeCache) {
        this.speechApi = speechApi;
        this.supportedLanguageApi = supportedLanguageApi;
        this.authenticationSpeechApiBridgeCache = authenticationSpeechApiBridgeCache;
    }

    @NonNull
    @Override
    public Call<List<SpeechRecognitionResult>> recognize(
            @NonNull Authentication auth,
            @NonNull RecognitionRequest request) {
        return authenticationSpeechApiBridgeCache
                .get(auth.getClass())
                .recognize(speechApi, auth, request);
    }

    @NonNull
    @Override
    public Single<List<SpeechRecognitionResult>> recognizeAsSingle(
            @NonNull Authentication auth,
            @NonNull RecognitionRequest request) {
        return authenticationSpeechApiBridgeCache
                .get(auth.getClass())
                .recognizeAsSingle(speechApi, auth, request);
    }

    @NonNull
    @Override
    public Call<List<SupportedLanguage>> getSupportedLanguageList() {
        return supportedLanguageApi.getSupportedLanguageList();
    }

    @NonNull
    @Override
    public Single<List<SupportedLanguage>> getSupportedLanguageListAsSingle() {
        return supportedLanguageApi.getSupportedLanguageListAsSingle();
    }

}
