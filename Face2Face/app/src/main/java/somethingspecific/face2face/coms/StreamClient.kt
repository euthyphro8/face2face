//package somethingspecific.face2face.coms
//
//import android.content.Context
//import android.nfc.Tag
//import org.webrtc.*
//import org.webrtc.PeerConnection
//import org.webrtc.DefaultVideoDecoderFactory
//import org.webrtc.DefaultVideoEncoderFactory
//import org.webrtc.SessionDescription
//import org.webrtc.SdpObserver
//import org.webrtc.MediaStream
//import org.webrtc.RtpReceiver
//import android.os.AsyncTask
//import android.util.Log
//import org.webrtc.DataChannel
//import org.webrtc.PeerConnection.PeerConnectionState
//import org.webrtc.PeerConnection.IceConnectionState
//import org.webrtc.IceCandidate
//import somethingspecific.face2face.events.EventManager
//import java.util.*
//import java.util.concurrent.Executors
//import org.webrtc.PeerConnectionFactory
//import android.os.AsyncTask.execute
//import android.os.Environment
//import android.os.ParcelFileDescriptor
//import org.webrtc.audio.JavaAudioDeviceModule
//import org.webrtc.audio.JavaAudioDeviceModule.AudioTrackErrorCallback
//import org.webrtc.audio.JavaAudioDeviceModule.AudioRecordErrorCallback
//import org.webrtc.audio.AudioDeviceModule
//import java.io.File
//import java.io.IOException
//import java.util.regex.Pattern
//import kotlin.collections.ArrayList
//
//
//class StreamClient(appContext: Context, rootEglBase: EglBase,
//                   streamParams: StreamParameters, eventManager: EventManager) {
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
//    // Executor thread is started once in private ctor and is used for all
//    // peer connection API calls to ensure new peer connection factory is
//    // created on the same thread as previously destroyed factory.
//    private val executor = Executors.newSingleThreadExecutor()
//
//    private var context = appContext
//    private var root = rootEglBase
//    private var params = streamParams
//    private var events = eventManager
//
//    private var audioConstraints = MediaConstraints()
//    private var sdpMediaConstraints = MediaConstraints()
//    private var sdpObserver = SDPObserver()
//    private var pcObserver = PCObserver()
//    private val statsTimer = Timer()
//    private var isInitiator = false
//    private var videoCapturerStopped = true
//    private var isError = false
//    private var renderVideo = false
//    private var enableAudio = false
//
//    private var factory : PeerConnectionFactory?
//    private var peer : PeerConnection?
//
//    private var localSdp : SessionDescription? = null
//    private var queuedRemoteCandidates : MutableList<IceCandidate>? = null
//
//    private var audioSource : AudioSource? = null
//    private var surfaceTextureHelper : SurfaceTextureHelper? = null
//    private var videoSource : VideoSource? = null
//
//    private var localRender : VideoSink? = null
//    private var remoteSinks : List<VideoSink>? = null
//
//    private var videoCapturer : VideoCapturer? = null
//    private var localVideoTrack : VideoTrack? = null
//    private var remoteVideoTrack : VideoTrack? = null
//    private var localVideoSender : RtpSender? = null
//    private var localAudioTrack : AudioTrack? = null
//
//
//    init {
//        //Var initialization
//        sdpMediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "false"))
//        sdpMediaConstraints.mandatory.add( MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
//
//
//
//        Log.d(TAG, "Preferred video codec: " + streamParams.videoCodec)//getSdpVideoCodecName(peerConnectionParameters))
//
//        val fieldTrials = getFieldTrials(params)
//        executor.execute {
//            Log.d(TAG, "Initialize WebRTC. Field trials: $fieldTrials")
//            PeerConnectionFactory.initialize(
//                PeerConnectionFactory.InitializationOptions.builder(appContext)
//                    .setFieldTrials(fieldTrials)
//                    .setEnableInternalTracer(true)
//                    .createInitializationOptions()
//            )
//        }
//
//        val adm = createJavaAudioDevice()
//
//        val encoderFactory = DefaultVideoEncoderFactory(rootEglBase.eglBaseContext,
//            true , true)
//        val decoderFactory = DefaultVideoDecoderFactory(rootEglBase.eglBaseContext)
//
//        factory = PeerConnectionFactory.builder()
//            .setOptions(PeerConnectionFactory.Options())
//            .setAudioDeviceModule(adm)
//            .setVideoEncoderFactory(encoderFactory)
//            .setVideoDecoderFactory(decoderFactory)
//            .createPeerConnectionFactory()
//
//
//        val iceServers = listOf<PeerConnection.IceServer>(
////            PeerConnection.IceServer
////                .builder(
////                    listOf(
////                        "turn:173.194.72.127:19305?transport=udp",
////                        "turn:[2404:6800:4008:C01::7F]:19305?transport=udp",
////                        "turn:173.194.72.127:443?transport=tcp",
////                        "turn:[2404:6800:4008:C01::7F]:443?transport=tcp"))
////                .setUsername("CKjCuLwFEgahxNRjuTAYzc/s6OMT")
////                .setPassword("u1SQDR/SQsPQIxXNWQT7czc/G4c=")
////                .createIceServer(),
////            PeerConnection.IceServer
////                .builder(
////                    listOf(
////                        "stun:stun.l.google.com:19302"))
////                .createIceServer()
//            )
//
//        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
//
//        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
//        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
//        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
//        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
//        // Use ECDSA encryption.
//        rtcConfig.keyType = PeerConnection.KeyType.ECDSA
//        // Enable DTLS for normal calls and disable for loopback calls.
//        rtcConfig.enableDtlsSrtp = true
//        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
//
//        peer = factory!!.createPeerConnection(rtcConfig, pcObserver)
//
//    }
//
//
//
//    //#region Device Initialization
//
//
//    private fun addConnection
//
//
//
//
//
//
//
//
//    private fun createPeerConnectionFactoryInternal(options:PeerConnectionFactory.Options?) {
//        isError = false
//
//        if (params.tracing)
//        {
//            PeerConnectionFactory.startInternalTracingCapture(
//                Environment.getExternalStorageDirectory().absolutePath +
//                        File.separator + "webrtc-trace.txt"
//            )
//        }
//        // It is possible to save a copy in raw PCM format on a file by checking
//        // the "Save input audio to file" checkbox in the Settings UI. A callback
//        // interface is set when this flag is enabled. As a result, a copy of recorded
//        // audio samples are provided to this client directly from the native audio
//        // layer in Java.
////        if (params.saveInputAudioToFile)
////        {
////            if (!params.useOpenSLES)
////            {
////                Log.d(TAG, "Enable recording of microphone input audio to file")
////                saveRecordedAudioToFile = RecordedAudioToFileController(executor)
////            }
////            else
////            {
////                // TODO(henrika): ensure that the UI reflects that if OpenSL ES is selected,
////                // then the "Save inut audio to file" option shall be grayed out.
////                Log.e(TAG, "Recording of input audio is not supported for OpenSL ES")
////            }
////        }
//
//        val adm = createJavaAudioDevice()
//
//        // Create peer connection factory.
//        if (options != null)
//        {
//            Log.d(TAG, "Factory networkIgnoreMask option: " + options.networkIgnoreMask)
//        }
//        val enableH264HighProfile = (VIDEO_CODEC_H264_HIGH == params.videoCodec)
//        val encoderFactory:VideoEncoderFactory
//        val decoderFactory:VideoDecoderFactory
//
//        if (params.videoCodecHwAcceleration)
//        {
//            encoderFactory = DefaultVideoEncoderFactory(
//                root.eglBaseContext, true /* enableIntelVp8Encoder */, enableH264HighProfile)
//            decoderFactory = DefaultVideoDecoderFactory(root.eglBaseContext)
//        }
//        else
//        {
//            encoderFactory = SoftwareVideoEncoderFactory()
//            decoderFactory = SoftwareVideoDecoderFactory()
//        }
//
//        factory = PeerConnectionFactory.builder()
//            .setOptions(options)
//            .setAudioDeviceModule(adm)
//            .setVideoEncoderFactory(encoderFactory)
//            .setVideoDecoderFactory(decoderFactory)
//            .createPeerConnectionFactory()
//        Log.d(TAG, "Peer connection factory created.")
//        adm.release()
//    }
//
//
//
//    private fun createPeerConnectionInternal() {
//        if (factory == null || isError)
//        {
//            Log.e(TAG, "Peerconnection factory is not created")
//            return
//        }
//        Log.d(TAG, "Create peer connection.")
//
//        queuedRemoteCandidates = ArrayList()
//
//        val rtcConfig = PeerConnection.RTCConfiguration(signalingParameters.iceServers)
//        // TCP candidates are only useful when connecting to a server that supports
//        // ICE-TCP.
//        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
//        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
//        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
//        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
//        // Use ECDSA encryption.
//        rtcConfig.keyType = PeerConnection.KeyType.ECDSA
//        // Enable DTLS for normal calls and disable for loopback calls.
//        rtcConfig.enableDtlsSrtp = !params.loopback
//        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
//
//        peer = factory!!.createPeerConnection(rtcConfig, pcObserver)
//        isInitiator = false
//
//        // Set INFO libjingle logging.
//        // NOTE: this _must_ happen while |factory| is alive!
//        Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO)
//
//        val mediaStreamLabels = Collections.singletonList("ARDAMS")
//        if (isVideoCallEnabled())
//        {
//            peer!!.addTrack(createVideoTrack(videoCapturer), mediaStreamLabels)
//            // We can add the renderers right away because we don't need to wait for an
//            // answer to get the remote track.
//            remoteVideoTrack = getRemoteVideoTrack()
//            remoteVideoTrack!!.setEnabled(renderVideo)
//            for (remoteSink in remoteSinks!!)
//            {
//                remoteVideoTrack!!.addSink(remoteSink)
//            }
//        }
//        peer!!.addTrack(createAudioTrack(), mediaStreamLabels)
//        if (isVideoCallEnabled())
//        {
//            findVideoSender()
//        }
//
//        if (params.aecDump)
//        {
//            try
//            {
//                val aecDumpFileDescriptor = ParcelFileDescriptor.open(File(
//                    Environment.getExternalStorageDirectory().getPath()
//                            + File.separator + "Download/audio.aecdump"
//                ),
//                    (ParcelFileDescriptor.MODE_READ_WRITE or ParcelFileDescriptor.MODE_CREATE
//                            or ParcelFileDescriptor.MODE_TRUNCATE))
//                factory!!.startAecDump(aecDumpFileDescriptor.detachFd(), -1)
//            }
//            catch (e: IOException) {
//                Log.e(TAG, "Can not open aecdump file", e)
//            }
//
//        }
//        Log.d(TAG, "Peer connection created.")
//    }
//
//
//
//    /**
//     * Create a PeerConnectionClient with the specified parameters. PeerConnectionClient takes
//     * ownership of |eglBase|.
//     */
//    fun PeerConnectionClient(
//        appContext: Context, eglBase: EglBase, peerConnectionParameters: StreamParameters, events: EventManager) {
//        this.root = eglBase
//        this.context = appContext
//        this.events = events
//        this.params = peerConnectionParameters
//
//        Log.d(TAG, "Preferred video codec: " + getSdpVideoCodecName(peerConnectionParameters))
//
//        val fieldTrials = getFieldTrials(peerConnectionParameters)
//        executor.execute{
//            Log.d(TAG, "Initialize WebRTC. Field trials: $fieldTrials")
//            PeerConnectionFactory.initialize(
//                PeerConnectionFactory.InitializationOptions.builder(appContext)
//                    .setFieldTrials(fieldTrials)
//                    .setEnableInternalTracer(true)
//                    .createInitializationOptions()
//            )
//        }
//    }
//
//    /**
//     * This function should only be called once.
//     */
//    fun createPeerConnectionFactory(options: PeerConnectionFactory.Options) {
//        if (factory != null) {
//            throw IllegalStateException("PeerConnectionFactory has already been constructed")
//        }
//        executor.execute({ createPeerConnectionFactoryInternal(options) })
//    }
//
//    fun createPeerConnection(
//        localRender: VideoSink, remoteSink: VideoSink, videoCapturer: VideoCapturer?) {
//        if (params.videoCallEnabled && videoCapturer == null) {
//            Log.w(TAG, "Video call enabled but no video capturer provided.")
//        }
//        if (videoCapturer != null) {
//            createPeerConnection(localRender, Collections.singletonList(remoteSink), videoCapturer)
//        }
//    }
//
//    fun createPeerConnection(
//        localRender: VideoSink, remoteSinks: List<VideoSink>, videoCapturer: VideoCapturer) {
//        if (params == null) {
//            Log.e(TAG, "Creating peer connection without initializing factory.")
//            return
//        }
//        this.localRender = localRender
//        this.remoteSinks = remoteSinks
//        this.videoCapturer = videoCapturer
//        executor.execute{
//            try {
//                createMediaConstraintsInternal()
//                createPeerConnectionInternal()
////                maybeCreateAndStartRtcEventLog()
//            } catch (e: Exception) {
//                reportError("Failed to create peer connection: " + e.message)
//                throw e
//            }
//        }
//    }
//
//    fun close() {
//        executor.execute{ this.closeInternal() }
//    }
//
//    private fun isVideoCallEnabled(): Boolean {
//        return params.videoCallEnabled && videoCapturer != null
//    }
//    private fun createMediaConstraintsInternal() {
//        // Create video constraints if video call is enabled.
//        params.videoWidth = params.videoWidth
//        params.videoHeight = params.videoHeight
//        params.videoFps = params.videoFps
//
//        // If video resolution is not specified, default to HD.
//        if (params.videoWidth == 0 || params.videoHeight == 0) {
//            params.videoWidth = HD_VIDEO_WIDTH
//            params.videoHeight = HD_VIDEO_HEIGHT
//        }
//
//        // If fps is not specified, default to 30.
//        if (params.videoFps == 0) {
//            params.videoFps = 30
//        }
//        Logging.d(TAG, "Capturing format: " + params.videoWidth + "x" + params.videoHeight + "@" + params.videoFps)
//
//        // Create audio constraints.
//        audioConstraints = MediaConstraints()
//        // added for audio performance measurements
//        if (params.noAudioProcessing) {
//            Log.d(TAG, "Disabling audio processing")
//            audioConstraints.mandatory.add(
//                MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "false")
//            )
//            audioConstraints.mandatory.add(
//                MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false")
//            )
//            audioConstraints.mandatory.add(
//                MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "false")
//            )
//            audioConstraints.mandatory.add(
//                MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "false")
//            )
//        }
//        // Create SDP constraints.
//        sdpMediaConstraints = MediaConstraints()
//        sdpMediaConstraints.mandatory.add(
//            MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
//        )
//        sdpMediaConstraints.mandatory.add(
//            MediaConstraints.KeyValuePair(
//                "OfferToReceiveVideo", java.lang.Boolean.toString(true)
//            )
//        )
//    }
//
//    private fun closeInternal() {
//        if (factory != null && params.aecDump) {
//            factory!!.stopAecDump()
//        }
//        Log.d(TAG, "Closing peer connection.")
//        statsTimer.cancel()
//        if (peer != null) {
//            peer!!.dispose()
//            peer = null
//        }
//        Log.d(TAG, "Closing audio source.")
//        if (audioSource != null) {
//            audioSource!!.dispose()
//            audioSource = null
//        }
//        Log.d(TAG, "Stopping capture.")
//        if (videoCapturer != null) {
//            try {
//                videoCapturer!!.stopCapture()
//            } catch (e: InterruptedException) {
//                throw RuntimeException(e)
//            }
//
//            videoCapturerStopped = true
//            videoCapturer!!.dispose()
//            videoCapturer = null
//        }
//        Log.d(TAG, "Closing video source.")
//        if (videoSource != null) {
//            videoSource!!.dispose()
//            videoSource = null
//        }
//        if (surfaceTextureHelper != null) {
//            surfaceTextureHelper!!.dispose()
//            surfaceTextureHelper = null
//        }
//        localRender = null
//        remoteSinks = null
//        Log.d(TAG, "Closing peer connection factory.")
//        if (factory != null) {
//            factory!!.dispose()
//            factory = null
//        }
//        root.release()
//        Log.d(TAG, "Closing peer connection done.")
//        events.DisconnectedEvent()
//        PeerConnectionFactory.stopInternalTracingCapture()
//        PeerConnectionFactory.shutdownInternalTracer()
//    }
//
//    fun setAudioEnabled(enable: Boolean) {
//        executor.execute{
//            enableAudio = enable
//            if (localAudioTrack != null) {
//                localAudioTrack!!.setEnabled(enableAudio)
//            }
//        }
//    }
//
//    fun setVideoEnabled(enable: Boolean) {
//        executor.execute{
//            renderVideo = enable
//            if (localVideoTrack != null) {
//                localVideoTrack!!.setEnabled(renderVideo)
//            }
//            if (remoteVideoTrack != null) {
//                remoteVideoTrack!!.setEnabled(renderVideo)
//            }
//        }
//    }
//
//    public fun createOffer() {
//        executor.execute {
//            if (peer != null && !isError) {
//                Log.d(TAG, "PC Create OFFER")
//                isInitiator = true
//                peer!!.createOffer(sdpObserver, sdpMediaConstraints)
//            }
//        }
//    }
//
//    public fun createAnswer() {
//        executor.execute{
//        if (peer != null && !isError) {
//                Log.d(TAG, "PC create ANSWER")
//                isInitiator = false
//                peer!!.createAnswer(sdpObserver, sdpMediaConstraints)
//            }
//        }
//    }
//
//    public fun addRemoteIceCandidate(candidate:IceCandidate) {
//        executor.execute{
//            if (peer != null && !isError) {
//                if (queuedRemoteCandidates != null)
//                    queuedRemoteCandidates!!.add(candidate)
//                else
//                    peer!!.addIceCandidate(candidate)
//            }
//        }
//    }
//
//    public fun removeRemoteIceCandidates(candidates:Array<IceCandidate>) {
//        executor.execute{
//            if (peer != null && !isError) {
//                // Drain the queued remote candidates if there is any so that
//                // they are processed in the proper order.
//                drainCandidates()
//                peer!!.removeIceCandidates(candidates)
//            }
//        }
//    }
//
//    public fun setRemoteDescription(sdp:SessionDescription) {
//        executor.execute{
//            if (peer != null && !isError) {
//                var sdpDescription = sdp.description
//                if ((params.audioCodec == AUDIO_CODEC_ISAC)) {
//                    sdpDescription = preferCodec(
//                        sdpDescription, AUDIO_CODEC_ISAC, true)
//                }
//                sdpDescription = preferCodec(
//                    sdpDescription, getSdpVideoCodecName(params), false)
//                if (params.audioStartBitrate > 0) {
//                    sdpDescription = setStartBitrate(
//                        AUDIO_CODEC_OPUS, false, sdpDescription, params.audioStartBitrate)
//                }
//                Log.d(TAG, "Set remote SDP.");
//                var sdpRemote = SessionDescription(sdp.type, sdpDescription)
//                peer?.setRemoteDescription(sdpObserver, sdpRemote)
//            }
//        }
//    }
//
//    private fun getSdpVideoCodecName(parameters: StreamParameters): String {
//        return when (parameters.videoCodec) {
//            VIDEO_CODEC_VP8 -> VIDEO_CODEC_VP8
//            VIDEO_CODEC_VP9 -> VIDEO_CODEC_VP9
//            VIDEO_CODEC_H264_HIGH, VIDEO_CODEC_H264_BASELINE -> VIDEO_CODEC_H264
//            else -> VIDEO_CODEC_VP8
//        }
//    }
//
//    private fun setStartBitrate(codec: String, isVideoCodec: Boolean, sdpDescription: String, bitrateKbps: Int): String {
//        val lines = sdpDescription.split("\r\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//        var rtpmapLineIndex = -1
//        var sdpFormatUpdated = false
//        var codecRtpMap: String? = null
//        // Search for codec rtpmap in format
//        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
//        var regex = "^a=rtpmap:(\\d+) $codec(/\\d+)+[\r]?$"
//        var codecPattern = Pattern.compile(regex)
//        for (i in lines.indices) {
//            val codecMatcher = codecPattern.matcher(lines[i])
//            if (codecMatcher.matches()) {
//                codecRtpMap = codecMatcher.group(1)
//                rtpmapLineIndex = i
//                break
//            }
//        }
//        if (codecRtpMap == null) {
//            Log.w(TAG, "No rtpmap for $codec codec")
//            return sdpDescription
//        }
//        Log.d(TAG, "Found " + codec + " rtpmap " + codecRtpMap + " at " + lines[rtpmapLineIndex])
//
//        // Check if a=fmtp string already exist in remote SDP for this codec and
//        // update it with new bitrate parameter.
//        regex = "^a=fmtp:$codecRtpMap \\w+=\\d+.*[\r]?$"
//        codecPattern = Pattern.compile(regex)
//        for (i in lines.indices) {
//            val codecMatcher = codecPattern.matcher(lines[i])
//            if (codecMatcher.matches()) {
//                Log.d(TAG, "Found " + codec + " " + lines[i])
//                if (isVideoCodec) {
//                    lines[i] += "; $VIDEO_CODEC_PARAM_START_BITRATE=$bitrateKbps"
//                } else {
//                    lines[i] += "; " + AUDIO_CODEC_PARAM_BITRATE + "=" + bitrateKbps * 1000
//                }
//                Log.d(TAG, "Update remote SDP line: " + lines[i])
//                sdpFormatUpdated = true
//                break
//            }
//        }
//
//        val newSdpDescription = StringBuilder()
//        for (i in lines.indices) {
//            newSdpDescription.append(lines[i]).append("\r\n")
//            // Append new a=fmtp line if no such line exist for a codec.
//            if (!sdpFormatUpdated && i == rtpmapLineIndex) {
//                val bitrateSet: String
//                if (isVideoCodec) {
//                    bitrateSet = "a=fmtp:$codecRtpMap $VIDEO_CODEC_PARAM_START_BITRATE=$bitrateKbps"
//                } else {
//                    bitrateSet = ("a=fmtp:" + codecRtpMap + " " + AUDIO_CODEC_PARAM_BITRATE + "="
//                            + bitrateKbps * 1000)
//                }
//                Log.d(TAG, "Add remote SDP line: $bitrateSet")
//                newSdpDescription.append(bitrateSet).append("\r\n")
//            }
//        }
//        return newSdpDescription.toString()
//    }
//
//
//    /** Returns the line number containing "m=audio|video", or -1 if no such line exists.  */
//    private fun findMediaDescriptionLine(isAudio: Boolean, sdpLines: Array<String>): Int {
//        val mediaDescription = if (isAudio) "m=audio " else "m=video "
//        for (i in sdpLines.indices) {
//            if (sdpLines[i].startsWith(mediaDescription)) {
//                return i
//            }
//        }
//        return -1
//    }
//
//    private fun joinString(s: Iterable<CharSequence>, delimiter: String, delimiterAtEnd: Boolean): String {
//        val iter = s.iterator()
//        if (!iter.hasNext()) {
//            return ""
//        }
//        val buffer = StringBuilder(iter.next())
//        while (iter.hasNext()) {
//            buffer.append(delimiter).append(iter.next())
//        }
//        if (delimiterAtEnd) {
//            buffer.append(delimiter)
//        }
//        return buffer.toString()
//    }
//
//    private fun movePayloadTypesToFront(preferredPayloadTypes: List<String>, mLine: String): String? {
//        // The format of the media description line should be: m=<media> <port> <proto> <fmt> ...
//        val origLineParts = mLine.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
//        if (origLineParts.size <= 3) {
//            Log.e(TAG, "Wrong SDP media description format: $mLine")
//            return null
//        }
//        val header = origLineParts.subList(0, 3)
//        val unpreferredPayloadTypes = origLineParts.subList(3, origLineParts.size)
//        unpreferredPayloadTypes.filter { !preferredPayloadTypes.contains(it) }
//
//        // Reconstruct the line with |preferredPayloadTypes| moved to the beginning of the payload
//        // types.
//        val newLineParts = ArrayList<String>()
//        newLineParts.addAll(header)
//        newLineParts.addAll(preferredPayloadTypes)
//        newLineParts.addAll(unpreferredPayloadTypes)
//        return joinString(newLineParts, " ", false)
//    }
//
//    private fun preferCodec(sdpDescription: String, codec: String, isAudio: Boolean): String {
//        val lines = sdpDescription.split("\r\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//        val mLineIndex = findMediaDescriptionLine(isAudio, lines)
//        if (mLineIndex == -1) {
//            Log.w(TAG, "No mediaDescription line, so can't prefer $codec")
//            return sdpDescription
//        }
//        // A list with all the payload types with name |codec|. The payload types are integers in the
//        // range 96-127, but they are stored as strings here.
//        val codecPayloadTypes = ArrayList<String>()
//        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
//        val codecPattern = Pattern.compile("^a=rtpmap:(\\d+) $codec(/\\d+)+[\r]?$")
//        for (line in lines) {
//            val codecMatcher = codecPattern.matcher(line)
//            if (codecMatcher.matches()) {
//                codecPayloadTypes.add(codecMatcher.group(1))
//            }
//        }
//        if (codecPayloadTypes.isEmpty()) {
//            Log.w(TAG, "No payload types with name $codec")
//            return sdpDescription
//        }
//
//        val newMLine = movePayloadTypesToFront(codecPayloadTypes, lines[mLineIndex]) ?: return sdpDescription
//        Log.d(TAG, "Change media description from: " + lines[mLineIndex] + " to " + newMLine)
//        lines[mLineIndex] = newMLine
//        return joinString(lines.asList(), "\r\n", true /* delimiterAtEnd */)
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
//    public fun stopVideoSource() {
//        executor.execute{
//            if (videoCapturer != null && !videoCapturerStopped) {
//                Log.d(TAG, "Stop video source.")
//                try {
//                    videoCapturer!!.stopCapture()
//                }
//                catch (e: InterruptedException) {}
//                videoCapturerStopped = true
//            }
//        }
//    }
//
//    public fun startVideoSource() {
//        executor.execute{
//            if (videoCapturer != null && videoCapturerStopped) {
//                Log.d(TAG, "Restart video source.")
//                videoCapturer?.startCapture(
//                    params.videoWidth, params.videoHeight, params.videoFps)
//                videoCapturerStopped = false
//            }
//        }
//    }
//
//
//    private fun createAudioTrack(): AudioTrack? {
//        audioSource = factory?.createAudioSource(audioConstraints)
//        localAudioTrack = factory?.createAudioTrack(AUDIO_TRACK_ID, audioSource)
//        localAudioTrack?.setEnabled(true)
//        return localAudioTrack
//    }
//
//  // Returns the remote VideoTrack, assuming there is only one.
//  private fun createVideoTrack(capturer:VideoCapturer ):VideoTrack? {
//      surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", root.eglBaseContext)
//      videoSource = factory?.createVideoSource(capturer.isScreencast)
//      capturer.initialize(surfaceTextureHelper, context, videoSource?.capturerObserver)
//      capturer.startCapture(params.videoWidth, params.videoHeight, params.videoFps)
//
//      localVideoTrack = factory?.createVideoTrack(VIDEO_TRACK_ID, videoSource)
//      localVideoTrack?.setEnabled(true)
//      localVideoTrack?.addSink(localRender)
//      return localVideoTrack
//  }
//    public fun changeCaptureFormat() {
//        executor.execute{ switchCameraInternal() }
//    }
//
//    private fun switchCameraInternal() {
//        if (videoCapturer is CameraVideoCapturer) {
//            if (isError) {
//                Log.e(TAG, "Failed to switch camera. Error : $isError")
//                return // No video is sent or only one camera is available or error happened.
//            }
//            Log.d(TAG, "Switch camera")
//            val cameraVideoCapturer = videoCapturer as CameraVideoCapturer
//            cameraVideoCapturer.switchCamera(null)
//        }
//        else {
//            Log.d(TAG, "Will not switch camera, video caputurer is not a camera");
//        }
//    }
//
//    public fun changeCaptureFormat(width:Int, height:Int, framerate:Int) {
//        executor.execute{ changeCaptureFormatInternal(width, height, framerate) }
//    }
//    private fun changeCaptureFormatInternal(width:Int, height:Int, framerate:Int) {
//        if (isError || videoCapturer == null)
//        {
//            Log.e(TAG,"Failed to change capture format." + ". Error : " + isError)
//            return
//        }
//        Log.d(TAG, "changeCaptureFormat: " + width + "x" + height + "@" + framerate)
//        videoSource?.adaptOutputFormat(width, height, framerate)
//    }
//
//    //#endregion
//
//
//
//    //#region Observers
//
//    // Implementation detail: observe ICE & stream changes and react accordingly.
//    private inner class PCObserver : PeerConnection.Observer {
//        override fun onDataChannel(p0: DataChannel?) {}
//
//        override fun onIceCandidate(candidate: IceCandidate) {
//            AsyncTask.THREAD_POOL_EXECUTOR.execute {
//                events.IceCandidateEvent(candidate)
//            }
//        }
//
//        override fun onIceCandidatesRemoved(candidates: Array<IceCandidate>) {
//            AsyncTask.THREAD_POOL_EXECUTOR.execute {
//                events.IceCandidateRemovedEvent(candidates)
//            }
//        }
//
//        override fun onSignalingChange(newState: PeerConnection.SignalingState) {
//            Log.d("Observer", "SignalingState: $newState")
//        }
//
//        override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState) {
//            AsyncTask.THREAD_POOL_EXECUTOR.execute {
//                Log.d("Observer", "IceConnectionState: $newState")
//                when (newState) {
//                    IceConnectionState.CONNECTED -> events.IceConnectedEvent()
//                    IceConnectionState.DISCONNECTED -> events.IceConnectedEvent()
//                    IceConnectionState.FAILED -> Log.e("IceDisconnectedEvent", "ICE connection failed.")
//                    IceConnectionState.NEW -> Log.d("Observer", "New connection.")
//                    IceConnectionState.CLOSED -> Log.d("Observer", "Ice closed!")
//                    IceConnectionState.CHECKING -> Log.d("Observer", "Checking ice...")
//                    IceConnectionState.COMPLETED -> Log.d("Observer", "Ice just completed.")
//                }
//            }
//        }
//
//        override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
//            AsyncTask.THREAD_POOL_EXECUTOR.execute {
//                Log.d("Observer", "PeerConnectionState: " + newState!!)
//                when (newState) {
//                    PeerConnectionState.CONNECTED -> events.ConnectedEvent()
//                    PeerConnectionState.DISCONNECTED -> events.DisconnectedEvent()
//                    PeerConnectionState.FAILED -> Log.e("Observer", "DTLS connection failed.")
//                    PeerConnectionState.NEW -> Log.d("Observer", "New connection.")
//                    PeerConnectionState.CLOSED -> Log.d("Observer", "Connection closed.")
//                    PeerConnectionState.CONNECTING -> Log.d("Observer", "Now connecting.")
//                }
//            }
//        }
//
//        override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState) {
//            Log.d("Observer", "IceGatheringState: $newState")
//        }
//
//        override fun onIceConnectionReceivingChange(receiving: Boolean) {
//            Log.d("Observer", "IceConnectionReceiving changed to $receiving")
//        }
//
//        override fun onAddStream(stream: MediaStream) {
//            Log.d("Observer", "Got stream... (this shouldn't happen!)")
//        }
//
//        override fun onRemoveStream(stream: MediaStream) {
//            Log.d("Observer", "Stream removed!")
//        }
//
//        override fun onRenegotiationNeeded() {
//            Log.d("Observer", "Renegotiation needed!")
//        }
//
//        override fun onAddTrack(receiver: RtpReceiver, mediaStreams: Array<MediaStream>)
//        {
//            Log.d("Observer", "Track added!")
//            //TODO: add this to the eglbase/root/whatever
//        }
//    }
//
//
//
//
//
//
//    // Implementation detail: handle offer creation/signaling and answer setting,
//    // as well as adding remote ICE candidates once the answer SDP is set.
//    private inner class SDPObserver : SdpObserver {
//        override fun onCreateSuccess(origSdp: SessionDescription) {
//            if (localSdp != null) {
//                Log.e("Observer", "Multiple SDP create.")
//                return
//            }
//            val sdpDescription = origSdp.description
////                sdpDescription = preferCodec(sdpDescription, VIDEO_CODEC_H264_HIGH, false)
//            val sdp = SessionDescription(origSdp.type, sdpDescription)
//            localSdp = sdp
//
//            AsyncTask.THREAD_POOL_EXECUTOR.execute {
//                peer?.let {
//                    Log.d("Observer", "Set local SDP from " + sdp.type)
//                    it.setLocalDescription(sdpObserver, sdp)
//                }
//            }
//        }
//
//        override fun onSetSuccess() {
//            peer ?: return
//            AsyncTask.THREAD_POOL_EXECUTOR.execute {
//                if (isInitiator) {
//                    // For offering peer connection we first create offer and set
//                    // local SDP, then after receiving answer set remote SDP.
//                    if (peer!!.remoteDescription == null) {
//                        // We've just set our local SDP so time to send it.
//                        Log.d("Observer", "Local SDP set successfully")
//                        localSdp?.let {
//                            events.LocalDescriptionEvent(it)
//                        }
//                    } else {
//                        // We've just set remote description, so drain remote
//                        // and send local ICE candidates.
//                        Log.d("Observer", "Remote SDP set successfully")
//                        drainCandidates()
//                    }
//                } else {
//                    // For answering peer connection we set remote SDP and then
//                    // create answer and set local SDP.
//                    if (peer!!.localDescription != null) {
//                        // We've just set our local SDP so time to send it, drain
//                        // remote and send local ICE candidates.
//                        Log.d("Observer", "Local SDP set successfully")
//                        localSdp?.let {
//                            events.LocalDescriptionEvent(it)
//                        }
//
//                        drainCandidates()
//                    } else {
//                        // We've just set remote SDP - do nothing for now -
//                        // answer will be created soon.
//                        Log.d("Observer", "Remote SDP set succesfully")
//                    }
//                }
//            }
//        }
//
//        override fun onCreateFailure(error: String) {
//            Log.e(TAG, "createSDP error: $error")
//        }
//
//        override fun onSetFailure(error: String) {
//            Log.e(TAG, "setSDP error: $error")
//        }
//    }
//
//
//    //#endregion
//
//
//
//    //#region Helpers
//    private fun drainCandidates() {
//        queuedRemoteCandidates ?: return
//        peer ?: return
//
//        Log.d(TAG, "Add " + queuedRemoteCandidates!!.size + " remote candidates")
//        for (candidate in queuedRemoteCandidates!!)
//            peer!!.addIceCandidate(candidate)
//        queuedRemoteCandidates = null
//    }
//
//
//    private fun getFieldTrials(streamParams: StreamParameters): String {
//        var fieldTrials = ""
//        if (streamParams.videoFlexfecEnabled) {
//            fieldTrials += VIDEO_FLEXFEC_FIELDTRIAL
//            Log.d(TAG, "Enable FlexFEC field trial.")
//        }
//        fieldTrials += VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL
//        if (streamParams.disableWebRtcAGCAndHPF) {
//            fieldTrials += DISABLE_WEBRTC_AGC_FIELDTRIAL
//            Log.d(TAG, "Disable WebRTC AGC field trial.")
//        }
//        return fieldTrials
//    }
//
//    fun createJavaAudioDevice(): AudioDeviceModule {
//        // Enable/disable OpenSL ES playback.
//        if (!params.useOpenSLES) {
//            Log.w(TAG, "External OpenSLES ADM not implemented yet.")
//        }
//
//        // Set audio record error callbacks.
//        val audioRecordErrorCallback = object : AudioRecordErrorCallback {
//            override fun onWebRtcAudioRecordInitError(errorMessage: String) {
//                Log.e(TAG, "onWebRtcAudioRecordInitError: $errorMessage")
//                reportError(errorMessage)
//            }
//
//            override fun onWebRtcAudioRecordStartError(
//                errorCode: JavaAudioDeviceModule.AudioRecordStartErrorCode, errorMessage: String
//            ) {
//                Log.e(TAG, "onWebRtcAudioRecordStartError: $errorCode. $errorMessage")
//                reportError(errorMessage)
//            }
//
//            override fun onWebRtcAudioRecordError(errorMessage: String) {
//                Log.e(TAG, "onWebRtcAudioRecordError: $errorMessage")
//                reportError(errorMessage)
//            }
//        }
//
//        val audioTrackErrorCallback = object : AudioTrackErrorCallback {
//            override fun onWebRtcAudioTrackInitError(errorMessage: String) {
//                Log.e(TAG, "onWebRtcAudioTrackInitError: $errorMessage")
//                reportError(errorMessage)
//            }
//
//            override fun onWebRtcAudioTrackStartError(
//                errorCode: JavaAudioDeviceModule.AudioTrackStartErrorCode, errorMessage: String
//            ) {
//                Log.e(TAG, "onWebRtcAudioTrackStartError: $errorCode. $errorMessage")
//                reportError(errorMessage)
//            }
//
//            override fun onWebRtcAudioTrackError(errorMessage: String) {
//                Log.e(TAG, "onWebRtcAudioTrackError: $errorMessage")
//                reportError(errorMessage)
//            }
//        }
//
//        return JavaAudioDeviceModule.builder(context)
//            .setUseHardwareAcousticEchoCanceler(!params.disableBuiltInAEC)
//            .setUseHardwareNoiseSuppressor(!params.disableBuiltInNS)
//            .setAudioRecordErrorCallback(audioRecordErrorCallback)
//            .setAudioTrackErrorCallback(audioTrackErrorCallback)
//            .createAudioDeviceModule()
//    }
//
//  private fun reportError(errorMessage: String) {
//    Log.e(TAG, "Peer connection error: $errorMessage")
//    executor.execute{
//      if (!isError) {
//        events.RtcConnectionError(errorMessage)
//        isError = true
//      }
//    }
//  }
//
//    //#endregion
//
//}
//
//
