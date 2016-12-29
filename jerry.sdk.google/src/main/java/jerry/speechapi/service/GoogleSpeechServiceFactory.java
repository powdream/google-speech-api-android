package jerry.speechapi.service;

import android.support.annotation.NonNull;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

public final class GoogleSpeechServiceFactory {
    private static final String HOST_SPEECH_API = "https://speech.googleapis.com";
    private static final String HOST_SUPPORTED_LANGUAGE_API = "https://cloud.google.com";

    @NonNull
    public static GoogleSpeechService newService() {
        return new GoogleSpeechServiceImpl(
                new Retrofit.Builder()
                        .baseUrl(HOST_SPEECH_API)
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .addConverterFactory(new GoogleSpeechServiceConverterFactory())
                        .build()
                        .create(SpeechApi.class),
                new Retrofit.Builder()
                        .baseUrl(HOST_SUPPORTED_LANGUAGE_API)
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .addConverterFactory(new GoogleSpeechServiceConverterFactory())
                        .build()
                        .create(SupportedLanguageApi.class),
                new AuthenticationSpeechApiBridgeCache()
        );
    }

}
