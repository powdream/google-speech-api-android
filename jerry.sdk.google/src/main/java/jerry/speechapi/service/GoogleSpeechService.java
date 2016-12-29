package jerry.speechapi.service;

import android.support.annotation.NonNull;

import java.util.List;

import jerry.speechapi.auth.Authentication;
import jerry.speechapi.client.RecognitionRequest;
import jerry.speechapi.client.lang.SupportedLanguage;
import jerry.speechapi.client.result.SpeechRecognitionResult;
import retrofit2.Call;
import rx.Single;

public interface GoogleSpeechService {
    @NonNull
    Call<List<SpeechRecognitionResult>> recognize(
            @NonNull Authentication auth,
            @NonNull RecognitionRequest request);

    @NonNull
    Single<List<SpeechRecognitionResult>> recognizeAsSingle(
            @NonNull Authentication auth,
            @NonNull RecognitionRequest request);

    @NonNull
    Call<List<SupportedLanguage>> getSupportedLanguageList();

    @NonNull
    Single<List<SupportedLanguage>> getSupportedLanguageListAsSingle();
}
