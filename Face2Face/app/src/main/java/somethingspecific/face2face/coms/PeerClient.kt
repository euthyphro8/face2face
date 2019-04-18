//package somethingspecific.face2face.coms
//
//import org.webrtc.*
//
//public class PeerConnectionClient {
//
//    val VIDEO_TRACK_ID = "ARDAMSv0"
//    val AUDIO_TRACK_ID = "ARDAMSa0"
//    val VIDEO_TRACK_TYPE = "video"
//    private val TAG = "PCRTCClient"
//    private val VIDEO_CODEC_VP8 = "VP8"
//    private val VIDEO_CODEC_VP9 = "VP9"
//    private val VIDEO_CODEC_H264 = "H264"
//    private val VIDEO_CODEC_H264_BASELINE = "H264 Baseline"
//    private val VIDEO_CODEC_H264_HIGH = "H264 High"
//    private val AUDIO_CODEC_OPUS = "opus"
//    private val AUDIO_CODEC_ISAC = "ISAC"
//    private val VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate"
//    private val VIDEO_FLEXFEC_FIELDTRIAL = "WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/"
//    private val VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL = "WebRTC-IntelVP8/Enabled/"
//    private val DISABLE_WEBRTC_AGC_FIELDTRIAL = "WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/"
//    private val AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate"
//    private val AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation"
//    private val AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl"
//    private val AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter"
//    private val AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression"
//    private val DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement"
//    private val HD_VIDEO_WIDTH = 1280
//    private val HD_VIDEO_HEIGHT = 720
//    private val BPS_IN_KBPS = 1000
//    private val RTCEVENTLOG_OUTPUT_DIR_NAME = "rtc_event_log"
//
//
//
//    // Executor thread is started once in private ctor and is used for all
//// peer connection API calls to ensure new peer connection factory is
//// created on the same thread as previously destroyed factory.
//    private val executor = Executors.newSingleThreadExecutor()
//
//    private val pcObserver = PCObserver()
//    private val sdpObserver = SDPObserver()
//    private val statsTimer = Timer()
//    private val rootEglBase: EglBase? = null
//    private val appContext: Context? = null
//    private val peerConnectionParameters: PeerConnectionParameters? = null
//    private val events: PeerConnectionEvents? = null
//
//    private val factory: PeerConnectionFactory? = null
//    private val peerConnection: PeerConnection? = null
//    private val audioSource: AudioSource? = null
//    private val surfaceTextureHelper: SurfaceTextureHelper? = null
//    private val videoSource: VideoSource? = null
//    private val preferIsac: Boolean = false
//    private val videoCapturerStopped: Boolean = false
//    private val isError: Boolean = false
//    private val localRender: VideoSink? = null
//    private val remoteSinks: List<VideoSink>? = null
//    private val signalingParameters: SignalingParameters? = null
//    private val videoWidth: Int = 0
//    private val videoHeight: Int = 0
//    private val videoFps: Int = 0
//    private val audioConstraints: MediaConstraints? = null
//    private val sdpMediaConstraints: MediaConstraints? = null
//    // Queued remote ICE candidates are consumed only after both local and
//// remote descriptions are set. Similarly local ICE candidates are sent to
//// remote peer after both local and remote description are set.
//    private val queuedRemoteCandidates: List<IceCandidate>? = null
//    private val isInitiator: Boolean = false
//    private val localSdp: SessionDescription? = null // either offer or answer SDP
//    private val videoCapturer: VideoCapturer? = null
//    // enableVideo is set to true if video should be rendered and sent.
//    private val renderVideo = true
//    private val localVideoTrack: VideoTrack? = null
//    private val remoteVideoTrack: VideoTrack? = null
//    private val localVideoSender: RtpSender? = null
//    // enableAudio is set to true if audio should be sent.
//    private val enableAudio = true
//    private val localAudioTrack: AudioTrack? = null
//    private val dataChannel: DataChannel? = null
//    private val dataChannelEnabled: Boolean = false
//    // Enable RtcEventLog.
//    private val rtcEventLog: RtcEventLog? = null
//    // Implements the WebRtcAudioRecordSamplesReadyCallback interface and writes
//// recorded audio samples to an output file.
//    private val saveRecordedAudioToFile: RecordedAudioToFileController? = null
//
//
//    /**
//     * Peer connection parameters.
//     */
//    class PeerConnectionParameters(
//        val videoCallEnabled: Boolean //true
//        , val loopback: Boolean          //false
//        , val tracing: Boolean           //false
//        ,
//        val videoWidth: Int, val videoHeight: Int, val videoFps: Int, val videoMaxBitrate: Int, val videoCodec: String,
//        val videoCodecHwAcceleration: Boolean, val videoFlexfecEnabled: Boolean, val audioStartBitrate: Int,
//        val audioCodec: String, val noAudioProcessing: Boolean, val aecDump: Boolean, val saveInputAudioToFile: Boolean,
//        val useOpenSLES: Boolean, val disableBuiltInAEC: Boolean, val disableBuiltInAGC: Boolean,
//        val disableBuiltInNS: Boolean, val disableWebRtcAGCAndHPF: Boolean, val enableRtcEventLog: Boolean,
//        private val dataChannelParameters: DataChannelParameters
//    )
//
//
//    /**
//     * Peer connection events.
//     */
//    interface PeerConnectionEvents {
//        /**
//         * Callback fired once local SDP is created and set.
//         */
//        fun onLocalDescription(sdp: SessionDescription)
//
//        /**
//         * Callback fired once local Ice candidate is generated.
//         */
//        fun onIceCandidate(candidate: IceCandidate)
//
//        /**
//         * Callback fired once local ICE candidates are removed.
//         */
//        fun onIceCandidatesRemoved(candidates: Array<IceCandidate>)
//
//        /**
//         * Callback fired once connection is established (IceConnectionState is
//         * CONNECTED).
//         */
//        fun onIceConnected()
//
//        /**
//         * Callback fired once connection is disconnected (IceConnectionState is
//         * DISCONNECTED).
//         */
//        fun onIceDisconnected()
//
//        /**
//         * Callback fired once DTLS connection is established (PeerConnectionState
//         * is CONNECTED).
//         */
//        fun onConnected()
//
//        /**
//         * Callback fired once DTLS connection is disconnected (PeerConnectionState
//         * is DISCONNECTED).
//         */
//        fun onDisconnected()
//
//        /**
//         * Callback fired once peer connection is closed.
//         */
//        fun onPeerConnectionClosed()
//
//        /**
//         * Callback fired once peer connection statistics is ready.
//         */
//        fun onPeerConnectionStatsReady(reports: Array<StatsReport>)
//
//        /**
//         * Callback fired once peer connection error happened.
//         */
//        fun onPeerConnectionError(description: String)
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//}