For the latest online version of the README.md see:
    
  https://github.com/powdream/GoogleSpeechApiAndroidClient/blob/master/README.md

# Google Cloud Speech Api - Android Client

## About

This will help you use [Google Cloud Speech Api](https://cloud.google.com/speech/) on
 Android mobile application.
It is written based on Google Cloud Speech Api v1beta1.


## How to Use

### Gradle

```gradle
allprojects {
    repositories {
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```

```gradle
dependencies {
    compile 'com.github.powdream:google-speech-api-android:${VERSION}'
    ...
}
```

Please refer to the [JitPack](https://jitpack.io/) homepage for more information.

### Android Manifest
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest package="jerry.execise.speechapi"
          xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- PERMISSION FOR ACCESSING GOOGLE SPEECH API VIA NETWORK -->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.INTERNET"/>

    ...

    <application>

        ...
    
        <!-- API KEY FOR USING GOOGLE SPEECH API: Optional -->
        <meta-data
            android:name="GOOGLE_API_KEY"
            android:value="YOUR_ACTUAL_API_KEY"/>
    </application>

</manifest>
```

You have to add two ``<uses-permission>`` tags to your manifest file.

Optionally, you can load the google api key from the ``<meta-data>`` tag on the Android manifest
file.

### Java

```java
public class MainActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        speechService = GoogleSpeechServiceFactory.newService();
        authentication = new Authentication.Builder()
                .setApiKeyFromMetaData(this, "GOOGLE_API_KEY")
                // or .setApiKey("YOUR_ACTUAL_API_KEY")
                .build();
        request();
    }

    private void request() {
        speechService
                .recognizeAsSingle(authentication, new RecognitionRequest.Builder()
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
                        .build()
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<List<SpeechRecognitionResult>>() {
                    @Override
                    public void onSuccess(List<SpeechRecognitionResult> speechRecognitionResults) {
                        Log.v(TAG, "recognition result: " + speechRecognitionResults);
                        textViewResult.setText(speechRecognitionResults.toString());
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Unexpected error", error);
                    }
                });
    }
}
```

### Same codes

* https://github.com/powdream/GoogleSpeechApiAndroidClient/tree/master/app

#### CAUTION

Because obtaining the supported language feature is not officially provided
by Google, I cannot make sure whether it will work always correctly.
If it is working incorrectly, please let me know. 


## License

    Anyone is free to copy, modify, publish, use, compile, sell, or
    distribute this software, either in source code form or as a compiled
    binary, for any purpose, commercial or non-commercial, and by any
    means.

    In jurisdictions that recognize copyright laws, the author or authors
    of this software dedicate any and all copyright interest in the
    software to the public domain. We make this dedication for the benefit
    of the public at large and to the detriment of our heirs and
    successors. We intend this dedication to be an overt act of
    relinquishment in perpetuity of all present and future rights to this
    software under copyright law.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
    EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
    IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
    OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
    ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
    OTHER DEALINGS IN THE SOFTWARE.

    For more information, please refer to <http://unlicense.org>

**NOTE**: This software depends on other packages that may be licensed under different open source licenses.


## References

* Google Cloud Platform : https://cloud.google.com/speech/
* Google Cloud Speech Api : https://cloud.google.com/speech/
* Retrofit2 : https://square.github.io/retrofit/
* RxJava : https://github.com/ReactiveX/RxJava
