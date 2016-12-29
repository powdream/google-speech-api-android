package jerry.execise.speechapi;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jerry.speechapi.auth.Authentication;
import jerry.speechapi.client.RecognitionRequest;
import jerry.speechapi.client.audio.RecognitionAudio;
import jerry.speechapi.client.config.AudioEncoding;
import jerry.speechapi.client.config.RecognitionConfig;
import jerry.speechapi.client.lang.SupportedLanguage;
import jerry.speechapi.client.result.SpeechRecognitionResult;
import jerry.speechapi.service.GoogleSpeechService;
import jerry.speechapi.service.GoogleSpeechServiceFactory;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int REQUEST_PERMISSION = 1;

    @BindView(R.id.text_result)
    TextView textViewResult;

    @BindView(R.id.button_record)
    Button buttonRecord;

    @BindView(R.id.spinner_language_list)
    Spinner spinnerLanguageList;

    private Unbinder butterKnifeUnbinder;

    private final RecognitionRequest googleSampleRequest = new RecognitionRequest.Builder()
            .setConfig(
                    new RecognitionConfig.Builder()
                            .setAudioEncoding(AudioEncoding.FLAC)
                            .setSampleRate(16000)
                            .setLanguageCode("en-US")
                            .build()
            )
            .setAudio(
                    new RecognitionAudio.Builder()
                            .setUri(Uri.parse("gs://cloud-samples-tests/speech/brooklyn.flac"))
                            .build()
            )
            .build();

    private GoogleSpeechService speechService;
    private Authentication authentication;

    private AtomicReference<Subscription> recognizeSubscription = new AtomicReference<>();

    private MediaRecorderManager mediaRecorderManager;

    private ArrayAdapter<SupportedLanguageWrapper> spinnerAdapter;

    private static final class SupportedLanguageWrapper {
        @NonNull
        final SupportedLanguage supportedLanguage;

        SupportedLanguageWrapper(@NonNull SupportedLanguage supportedLanguage) {
            this.supportedLanguage = supportedLanguage;
        }

        @Override
        public String toString() {
            return supportedLanguage.getName();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        butterKnifeUnbinder = ButterKnife.bind(this);

        speechService = GoogleSpeechServiceFactory.newService();
        try {
            authentication = new Authentication.Builder()
                    .setApiKeyFromMetaData(this, "GOOGLE_API_KEY")
                    .build();
        } catch (PackageManager.NameNotFoundException e) {
            throw new AssertionError(e);
        }

        mediaRecorderManager = new MediaRecorderManager();

        spinnerAdapter = new ArrayAdapter<>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                android.R.id.text1
        );

        showProgressDialog("Loading language list...", false, null);
        spinnerLanguageList.setAdapter(spinnerAdapter);
        speechService
                .getSupportedLanguageListAsSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(new Action1<List<SupportedLanguage>>() {
                    @Override
                    public void call(List<SupportedLanguage> supportedLanguages) {
                        spinnerAdapter.clear();
                        for (SupportedLanguage l : supportedLanguages) {
                            Log.v(TAG, "Supported language: " + l);
                            spinnerAdapter.add(new SupportedLanguageWrapper(l));
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Func1<List<SupportedLanguage>, String>() {
                    @Override
                    public String call(List<SupportedLanguage> supportedLanguages) {
                        SharedPreferences sp = getPreferences(MODE_PRIVATE);
                        return sp.getString("last-selected-language-code", null);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(new Action1<String>() {
                    @Override
                    public void call(String lastSelectedLanguageCode) {
                        int count = spinnerLanguageList.getCount();
                        if (!TextUtils.isEmpty(lastSelectedLanguageCode)) {
                            for (int i = 0; i < count; i++) {
                                SupportedLanguageWrapper l = (SupportedLanguageWrapper) spinnerLanguageList.getItemAtPosition(i);
                                if (l.supportedLanguage.getCode().equals(lastSelectedLanguageCode)) {
                                    spinnerLanguageList.setSelection(i, true);
                                }
                            }
                        }
                    }
                })
                .subscribe(new SingleSubscriber<String>() {
                    @Override
                    public void onSuccess(String lastSelectedLanguageCode) {
                        spinnerLanguageList.setOnItemSelectedListener(new LastSelectedLanguageCodeBackup());
                        dismissProgressDialogIfShowing();
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Failed to obtain the supported language list.", error);
                        dismissProgressDialogIfShowing();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        butterKnifeUnbinder.unbind();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        stopRecording(false);
        mediaRecorderManager.deleteRecordFileIfExists();
        cancelIfRecognizeRequestExists();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void sendRequestAsync(View view) {
        requestToRecognize(googleSampleRequest);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO},
                REQUEST_PERMISSION
        );
    }

    private boolean checkPermission() {
        int resultWriteExternalStorage = ContextCompat.checkSelfPermission(
                this, WRITE_EXTERNAL_STORAGE);
        int resultRecordAudio = ContextCompat.checkSelfPermission(
                this, RECORD_AUDIO);
        return resultWriteExternalStorage == PackageManager.PERMISSION_GRANTED &&
                resultRecordAudio == PackageManager.PERMISSION_GRANTED;
    }

    public void toggleRecord(View ignore) {
        if (mediaRecorderManager.isRecording()) {
            stopRecording(true);
        } else {
            startRecording();
        }
    }

    private void startRecording() {
        if (!mediaRecorderManager.isRecording()) {
            mediaRecorderManager.deleteRecordFileIfExists();
            if (checkPermission()) {
                try {
                    mediaRecorderManager.startRecording();
                    buttonRecord.setText("Stop recording");
                } catch (IOException e) {
                    Log.e(TAG, "error from startRecording()", e);
                }
            } else {
                requestPermission();
            }
        }
    }

    private void stopRecording(boolean recognizeAfterStop) {
        if (mediaRecorderManager.isRecording()) {
            mediaRecorderManager.stopRecording();
            buttonRecord.setText("Start recording");

            if (recognizeAfterStop) {
                SupportedLanguageWrapper selectedLanguage = (SupportedLanguageWrapper) spinnerLanguageList.getSelectedItem();
                requestToRecognize(new RecognitionRequest.Builder()
                        .setConfig(
                                new RecognitionConfig.Builder()
                                        .setAudioEncoding(AudioEncoding.AMR_WB)
                                        .setSampleRate(16000)
                                        .setLanguageCode(selectedLanguage.supportedLanguage.getCode())
                                        .build()
                        )
                        .setAudio(
                                new RecognitionAudio.Builder()
                                        .setAudioFile(mediaRecorderManager.getRecordFile())
                                        .build()
                        )
                        .build());
            }
        }
    }

    private void requestToRecognize(@NonNull RecognitionRequest request) {
        Toast.makeText(this, "sendRequestAsync!!", Toast.LENGTH_SHORT).show();
        cancelIfRecognizeRequestExists();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewResult.setText("");
            }
        });

        final Subscription newRequest = speechService
                .recognizeAsSingle(authentication, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<List<SpeechRecognitionResult>>() {
                    @Override
                    public void onSuccess(List<SpeechRecognitionResult> speechRecognitionResults) {
                        Log.v(TAG, "recognition result: " + speechRecognitionResults);
                        textViewResult.setText(speechRecognitionResults.toString());
                        dismissProgressDialogIfShowing();
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Unexpected error", error);
                        textViewResult.setText(error.toString());
                        dismissProgressDialogIfShowing();
                    }
                });

        if (!recognizeSubscription.compareAndSet(null, newRequest)) {
            newRequest.unsubscribe();
        }

        showProgressDialog("Recognizing...", true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (recognizeSubscription.compareAndSet(newRequest, null)) {
                    newRequest.unsubscribe();
                }
                Toast.makeText(MainActivity.this, "Recognition cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelIfRecognizeRequestExists() {
        Subscription previousRequest = recognizeSubscription.getAndSet(null);
        if (previousRequest != null) {
            previousRequest.unsubscribe();
        }
    }

    private AtomicReference<ProgressDialog> dialogAtomicReference = new AtomicReference<>();

    private void showProgressDialog(
            @Nullable final String message,
            final boolean cancelable,
            @Nullable final DialogInterface.OnCancelListener cancelListener) {
        final ProgressDialog prevDialog = dialogAtomicReference.getAndSet(null);
        if (prevDialog != null && prevDialog.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    prevDialog.dismiss();
                }
            });
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ProgressDialog newDialog = ProgressDialog.show(
                        // Context
                        MainActivity.this,
                        // Title
                        null,
                        // Message
                        message,
                        // Indeterminate
                        true,
                        // Cancelable
                        cancelable,
                        // CancelListener
                        cancelListener
                );
                if (!dialogAtomicReference.compareAndSet(null, newDialog)) {
                    newDialog.dismiss();
                }
            }
        });
    }

    private void dismissProgressDialogIfShowing() {
        final ProgressDialog prevDialog = dialogAtomicReference.getAndSet(null);
        if (prevDialog != null && prevDialog.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    prevDialog.dismiss();
                }
            });
        }
    }

    private class LastSelectedLanguageCodeBackup implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            SupportedLanguageWrapper l = (SupportedLanguageWrapper) parent.getItemAtPosition(position);
            SharedPreferences sp = getPreferences(MODE_PRIVATE);
            sp.edit().putString("last-selected-language-code", l.supportedLanguage.getCode()).apply();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            SharedPreferences sp = getPreferences(MODE_PRIVATE);
            sp.edit().remove("last-selected-language-code").apply();
        }
    }
}
