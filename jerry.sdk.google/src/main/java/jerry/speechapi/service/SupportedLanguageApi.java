package jerry.speechapi.service;

import android.support.annotation.NonNull;

import java.util.List;

import jerry.speechapi.BuildConfig;
import jerry.speechapi.client.lang.SupportedLanguage;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import rx.Single;

interface SupportedLanguageApi {
    String HEADER_USER_AGENT = "User-Agent: (Linux; Android) " + BuildConfig.APPLICATION_ID + "/" + BuildConfig.VERSION_NAME + " Mobile";

    @NonNull
    @Headers({HEADER_USER_AGENT})
    @GET("speech/docs/languages")
    Call<List<SupportedLanguage>> getSupportedLanguageList();

    @NonNull
    @Headers({HEADER_USER_AGENT})
    @GET("speech/docs/languages")
    Single<List<SupportedLanguage>> getSupportedLanguageListAsSingle();
}
