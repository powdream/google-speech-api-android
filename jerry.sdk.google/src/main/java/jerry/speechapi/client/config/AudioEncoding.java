package jerry.speechapi.client.config;

/**
 * @see <a href="http://cloud.google.com/speech/reference/rest/v1beta1/RecognitionConfig#AudioEncoding">https://cloud.google.com/speech/reference/rest/v1beta1/RecognitionConfig#AudioEncoding</a>
 */
public enum AudioEncoding {
    /**
     * Not specified. Will return result google.rpc.Code.INVALID_ARGUMENT.
     */
    ENCODING_UNSPECIFIED,
    /**
     * Uncompressed 16-bit signed little-endian samples (Linear PCM).
     * This is the only encoding that may be used by <code>speech.asyncrecognize</code>.
     */
    LINEAR16,
    /**
     * This is the recommended encoding for <code>speech.syncrecognize</code>
     * and <code>StreamingRecognize</code>
     * because it uses lossless compression;
     * therefore recognition accuracy is not compromised by a lossy codec.<br/>
     * <br/>
     * The stream FLAC (Free Lossless Audio Codec) encoding is specified at:
     * <a href="http://flac.sourceforge.net/documentation.html>http://flac.sourceforge.net/documentation.html</a>.
     * 16-bit and 24-bit samples are supported. Not all fields in STREAMINFO are supported.
     */
    FLAC,
    /**
     * 8-bit samples that compand 14-bit audio samples using G.711 PCMU/mu-law.
     */
    MULAW,
    /**
     * Adaptive Multi-Rate Narrowband codec. <code>sampleRate</code> must be 8000 Hz.
     */
    AMR,
    /**
     * Adaptive Multi-Rate Wideband codec. <code>sampleRate</code> must be 16000 Hz.
     */
    AMR_WB
}
