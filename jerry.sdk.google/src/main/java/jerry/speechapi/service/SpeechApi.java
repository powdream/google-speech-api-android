package jerry.speechapi.service;

import android.support.annotation.NonNull;

import java.util.List;

import jerry.speechapi.BuildConfig;
import jerry.speechapi.client.RecognitionRequest;
import jerry.speechapi.client.result.SpeechRecognitionResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Single;

interface SpeechApi {
    String HEADER_ACCEPT = "Accept: application/json";
    String HEADER_CONTENT_TYPE = "Content-Type: application/json";
    String HEADER_USER_AGENT = "User-Agent: (Linux; Android) " + BuildConfig.APPLICATION_ID + "/" + BuildConfig.VERSION_NAME + " Mobile";

    String API_VERSION = "v1beta1";
    String API_PATH_RECOGNIZE = API_VERSION + "/speech:syncrecognize";

    @NonNull
    @Headers({HEADER_ACCEPT, HEADER_CONTENT_TYPE, HEADER_USER_AGENT,})
    @POST(API_PATH_RECOGNIZE)
    Call<List<SpeechRecognitionResult>> recognizeWithApiKey(
            @NonNull @Query("key") String apiKey,
            @NonNull @Body RecognitionRequest request);

    @NonNull
    @Headers({HEADER_ACCEPT, HEADER_CONTENT_TYPE, HEADER_USER_AGENT,})
    @POST(API_PATH_RECOGNIZE)
    Call<List<SpeechRecognitionResult>> recognizeWithAccessToken(
            @NonNull @Header("Authorization") String accessToken,
            @NonNull @Body RecognitionRequest request);

    @NonNull
    @Headers({HEADER_ACCEPT, HEADER_CONTENT_TYPE, HEADER_USER_AGENT,})
    @POST(API_PATH_RECOGNIZE)
    Single<List<SpeechRecognitionResult>> recognizeWithApiKeyAsSingle(
            @NonNull @Query("key") String apiKey,
            @NonNull @Body RecognitionRequest request);

    @NonNull
    @Headers({HEADER_ACCEPT, HEADER_CONTENT_TYPE, HEADER_USER_AGENT,})
    @POST(API_PATH_RECOGNIZE)
    Single<List<SpeechRecognitionResult>> recognizeWithAccessTokenAsSingle(
            @NonNull @Header("Authorization") String accessToken,
            @NonNull @Body RecognitionRequest request);
}
