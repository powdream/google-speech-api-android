package jerry.execise.speechapi;

import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;

class MediaRecorderManager {

    private boolean isRecording = false;

    @Nullable
    private MediaRecorder mediaRecorder;

    @NonNull
    private final String tempFileDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();

    @Nullable
    private File recordFile;

    boolean isRecording() {
        return isRecording;
    }

    void startRecording() throws IOException {
        if (!isRecording) {
            prepareMediaRecorder();
            if (mediaRecorder != null) {
                mediaRecorder.prepare();
                mediaRecorder.start();
            }
            isRecording = true;
        }
    }

    void stopRecording() {
        if (isRecording) {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
            }
            isRecording = false;
        }
    }

    @Nullable
    File getRecordFile() {
        return recordFile;
    }

    void deleteRecordFileIfExists() {
        if (recordFile != null) {
            if (recordFile.exists()) {
                recordFile.delete();
            }
            recordFile = null;
        }
    }

    private void prepareMediaRecorder() {
        deleteRecordFileIfExists();
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
        }
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        mediaRecorder.setAudioSamplingRate(16000);
        try {
            recordFile = File.createTempFile("temp", ".amr", new File(tempFileDirectory));
            mediaRecorder.setOutputFile(recordFile.getAbsolutePath());
        } catch (IOException ignore) {
        }
    }
}
