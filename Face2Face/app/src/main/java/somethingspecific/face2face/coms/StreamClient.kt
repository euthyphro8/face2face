package somethingspecific.face2face.coms

import android.content.Context
import android.nfc.Tag
import org.webrtc.*
import org.webrtc.PeerConnection
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.SessionDescription
import org.webrtc.SdpObserver
import org.webrtc.MediaStream
import org.webrtc.RtpReceiver
import android.os.AsyncTask
import android.util.Log
import org.webrtc.DataChannel
import org.webrtc.PeerConnection.PeerConnectionState
import org.webrtc.PeerConnection.IceConnectionState
import org.webrtc.IceCandidate
import somethingspecific.face2face.events.EventManager
import java.util.*
import java.util.concurrent.Executors
import org.webrtc.PeerConnectionFactory
import android.os.AsyncTask.execute
import somethingspecific.face2face.apprtc.PeerConnectionClient.PeerConnectionParameters
import org.webrtc.audio.JavaAudioDeviceModule
import org.webrtc.audio.JavaAudioDeviceModule.AudioTrackErrorCallback
import org.webrtc.audio.JavaAudioDeviceModule.AudioRecordErrorCallback
import org.webrtc.audio.AudioDeviceModule








class StreamClient(appContext: Context, rootEglBase: EglBase,
                   streamParams: StreamParameters, eventManager: EventManager) {

    val VIDEO_TRACK_ID = "ARDAMSv0"
    val AUDIO_TRACK_ID = "ARDAMSa0"
    val VIDEO_TRACK_TYPE = "video"
    private val TAG = "PCRTCClient"
    private val VIDEO_CODEC_VP8 = "VP8"
    private val VIDEO_CODEC_VP9 = "VP9"
    private val VIDEO_CODEC_H264 = "H264"
    private val VIDEO_CODEC_H264_BASELINE = "H264 Baseline"
    private val VIDEO_CODEC_H264_HIGH = "H264 High"
    private val AUDIO_CODEC_OPUS = "opus"
    private val AUDIO_CODEC_ISAC = "ISAC"
    private val VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate"
    private val VIDEO_FLEXFEC_FIELDTRIAL = "WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/"
    private val VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL = "WebRTC-IntelVP8/Enabled/"
    private val DISABLE_WEBRTC_AGC_FIELDTRIAL = "WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/"
    private val AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate"
    private val AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation"
    private val AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl"
    private val AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter"
    private val AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression"
    private val DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement"
    private val HD_VIDEO_WIDTH = 1280
    private val HD_VIDEO_HEIGHT = 720
    private val BPS_IN_KBPS = 1000
    private val RTCEVENTLOG_OUTPUT_DIR_NAME = "rtc_event_log"


    // Executor thread is started once in private ctor and is used for all
    // peer connection API calls to ensure new peer connection factory is
    // created on the same thread as previously destroyed factory.
    private val executor = Executors.newSingleThreadExecutor()

    private val context = appContext
    private val root = rootEglBase
    private val params = streamParams
    private val events = eventManager

    private var audioConstraints = MediaConstraints()
    private var sdpMediaConstraints = MediaConstraints()
    private var sdpObserver = SDPObserver()
    private var pcObserver = PCObserver()
    private val statsTimer = Timer()
    private var isInitiator = false
    private var videoCapturerStopped = true
    private var isError = false

    private var factory : PeerConnectionFactory?
    private var peer : PeerConnection?

    private var localSdp : SessionDescription? = null
    private var queuedRemoteCandidates : List<IceCandidate>? = null

    private var audioSource : AudioSource? = null
    private var surfaceTextureHelper : SurfaceTextureHelper? = null
    private var videoSource : VideoSource? = null

    private var localRender : VideoSink? = null
    private var remoteSinks : List<VideoSink>? = null

    private var videoCapturer : VideoCapturer? = null
    private var localVideoTrack : VideoTrack? = null
    private var remoteVideoTrack : VideoTrack? = null
    private var localVideoSender : RtpSender? = null
    private var localAudioTrack : AudioTrack? = null


    init {
        //Var initialization
        sdpMediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "false"))
        sdpMediaConstraints.mandatory.add( MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))



        Log.d(TAG, "Preferred video codec: " + streamParams.videoCodec)//getSdpVideoCodecName(peerConnectionParameters))

        val fieldTrials = getFieldTrials(params)
        executor.execute {
            Log.d(TAG, "Initialize WebRTC. Field trials: $fieldTrials")
            PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(appContext)
                    .setFieldTrials(fieldTrials)
                    .setEnableInternalTracer(true)
                    .createInitializationOptions()
            )
        }

        val adm = createJavaAudioDevice()

        val encoderFactory = DefaultVideoEncoderFactory(rootEglBase.eglBaseContext,
            true , true)
        val decoderFactory = DefaultVideoDecoderFactory(rootEglBase.eglBaseContext)

        factory = PeerConnectionFactory.builder()
            .setOptions(PeerConnectionFactory.Options())
            .setAudioDeviceModule(adm)
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()


        val iceServers = listOf(
            PeerConnection.IceServer
                .builder(
                    listOf(
                        "turn:173.194.72.127:19305?transport=udp",
                        "turn:[2404:6800:4008:C01::7F]:19305?transport=udp",
                        "turn:173.194.72.127:443?transport=tcp",
                        "turn:[2404:6800:4008:C01::7F]:443?transport=tcp"))
                .setUsername("CKjCuLwFEgahxNRjuTAYzc/s6OMT")
                .setPassword("u1SQDR/SQsPQIxXNWQT7czc/G4c=")
                .createIceServer(),
            PeerConnection.IceServer
                .builder(
                    listOf(
                        "stun:stun.l.google.com:19302"))
                .createIceServer()
            )

        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)

        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        // Use ECDSA encryption.
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA
        // Enable DTLS for normal calls and disable for loopback calls.
        rtcConfig.enableDtlsSrtp = true
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN

        peer = factory!!.createPeerConnection(rtcConfig, pcObserver)

    }



    //#region Device Initialization




    //#endregion


    //#region Observers

    // Implementation detail: observe ICE & stream changes and react accordingly.
    private inner class PCObserver : PeerConnection.Observer {
        override fun onDataChannel(p0: DataChannel?) {}

        override fun onIceCandidate(candidate: IceCandidate) {
            AsyncTask.THREAD_POOL_EXECUTOR.execute {
                events.IceCandidateEvent(candidate)
            }
        }

        override fun onIceCandidatesRemoved(candidates: Array<IceCandidate>) {
            AsyncTask.THREAD_POOL_EXECUTOR.execute {
                events.IceCandidateRemovedEvent(candidates)
            }
        }

        override fun onSignalingChange(newState: PeerConnection.SignalingState) {
            Log.d("Observer", "SignalingState: $newState")
        }

        override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState) {
            AsyncTask.THREAD_POOL_EXECUTOR.execute {
                Log.d("Observer", "IceConnectionState: $newState")
                when (newState) {
                    IceConnectionState.CONNECTED -> events.IceConnectedEvent()
                    IceConnectionState.DISCONNECTED -> events.IceConnectedEvent()
                    IceConnectionState.FAILED -> Log.e("IceDisconnectedEvent", "ICE connection failed.")
                    IceConnectionState.NEW -> Log.d("Observer", "New connection.")
                    IceConnectionState.CLOSED -> Log.d("Observer", "Ice closed!")
                    IceConnectionState.CHECKING -> Log.d("Observer", "Checking ice...")
                    IceConnectionState.COMPLETED -> Log.d("Observer", "Ice just completed.")
                }
            }
        }

        override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
            AsyncTask.THREAD_POOL_EXECUTOR.execute {
                Log.d("Observer", "PeerConnectionState: " + newState!!)
                when (newState) {
                    PeerConnectionState.CONNECTED -> events.ConnectedEvent()
                    PeerConnectionState.DISCONNECTED -> events.DisconnectedEvent()
                    PeerConnectionState.FAILED -> Log.e("Observer", "DTLS connection failed.")
                    PeerConnectionState.NEW -> Log.d("Observer", "New connection.")
                    PeerConnectionState.CLOSED -> Log.d("Observer", "Connection closed.")
                    PeerConnectionState.CONNECTING -> Log.d("Observer", "Now connecting.")
                }
            }
        }

        override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState) {
            Log.d("Observer", "IceGatheringState: $newState")
        }

        override fun onIceConnectionReceivingChange(receiving: Boolean) {
            Log.d("Observer", "IceConnectionReceiving changed to $receiving")
        }

        override fun onAddStream(stream: MediaStream) {
            Log.d("Observer", "Got stream... (this shouldn't happen!)")
        }

        override fun onRemoveStream(stream: MediaStream) {
            Log.d("Observer", "Stream removed!")
        }

        override fun onRenegotiationNeeded() {
            Log.d("Observer", "Renegotiation needed!")
        }

        override fun onAddTrack(receiver: RtpReceiver, mediaStreams: Array<MediaStream>)
        {
            Log.d("Observer", "Track added!")
            //TODO: add this to the eglbase/root/whatever
        }
    }

    // Implementation detail: handle offer creation/signaling and answer setting,
    // as well as adding remote ICE candidates once the answer SDP is set.
    private inner class SDPObserver : SdpObserver {
        override fun onCreateSuccess(origSdp: SessionDescription) {
            if (localSdp != null) {
                Log.e("Observer", "Multiple SDP create.")
                return
            }
            val sdpDescription = origSdp.description
//                sdpDescription = preferCodec(sdpDescription, VIDEO_CODEC_H264_HIGH, false)
            val sdp = SessionDescription(origSdp.type, sdpDescription)
            localSdp = sdp

            AsyncTask.THREAD_POOL_EXECUTOR.execute {
                peer?.let {
                    Log.d("Observer", "Set local SDP from " + sdp.type)
                    it.setLocalDescription(sdpObserver, sdp)
                }
            }
        }

        override fun onSetSuccess() {
            peer ?: return
            AsyncTask.THREAD_POOL_EXECUTOR.execute {
                if (isInitiator) {
                    // For offering peer connection we first create offer and set
                    // local SDP, then after receiving answer set remote SDP.
                    if (peer!!.remoteDescription == null) {
                        // We've just set our local SDP so time to send it.
                        Log.d("Observer", "Local SDP set successfully")
                        localSdp?.let {
                            events.LocalDescriptionEvent(it)
                        }
                    } else {
                        // We've just set remote description, so drain remote
                        // and send local ICE candidates.
                        Log.d("Observer", "Remote SDP set successfully")
                        drainCandidates()
                    }
                } else {
                    // For answering peer connection we set remote SDP and then
                    // create answer and set local SDP.
                    if (peer!!.localDescription != null) {
                        // We've just set our local SDP so time to send it, drain
                        // remote and send local ICE candidates.
                        Log.d("Observer", "Local SDP set successfully")
                        localSdp?.let {
                            events.LocalDescriptionEvent(it)
                        }

                        drainCandidates()
                    } else {
                        // We've just set remote SDP - do nothing for now -
                        // answer will be created soon.
                        Log.d("Observer", "Remote SDP set succesfully")
                    }
                }
            }
        }


        private fun drainCandidates() {
            queuedRemoteCandidates ?: return
            peer ?: return

            Log.d(TAG, "Add " + queuedRemoteCandidates!!.size + " remote candidates")
            for (candidate in queuedRemoteCandidates!!)
                peer!!.addIceCandidate(candidate)
            queuedRemoteCandidates = null
        }


        override fun onCreateFailure(error: String) {
            Log.e(TAG, "createSDP error: $error")
        }

        override fun onSetFailure(error: String) {
            Log.e(TAG, "setSDP error: $error")
        }
    }


    //#endregion

    //#region Helpers

    private fun getFieldTrials(streamParams: StreamParameters): String {
        var fieldTrials = ""
        if (streamParams.videoFlexfecEnabled) {
            fieldTrials += VIDEO_FLEXFEC_FIELDTRIAL
            Log.d(TAG, "Enable FlexFEC field trial.")
        }
        fieldTrials += VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL
        if (streamParams.disableWebRtcAGCAndHPF) {
            fieldTrials += DISABLE_WEBRTC_AGC_FIELDTRIAL
            Log.d(TAG, "Disable WebRTC AGC field trial.")
        }
        return fieldTrials
    }

    fun createJavaAudioDevice(): AudioDeviceModule {
        // Enable/disable OpenSL ES playback.
        if (!params.useOpenSLES) {
            Log.w(TAG, "External OpenSLES ADM not implemented yet.")
        }

        // Set audio record error callbacks.
        val audioRecordErrorCallback = object : AudioRecordErrorCallback {
            override fun onWebRtcAudioRecordInitError(errorMessage: String) {
                Log.e(TAG, "onWebRtcAudioRecordInitError: $errorMessage")
                reportError(errorMessage)
            }

            override fun onWebRtcAudioRecordStartError(
                errorCode: JavaAudioDeviceModule.AudioRecordStartErrorCode, errorMessage: String
            ) {
                Log.e(TAG, "onWebRtcAudioRecordStartError: $errorCode. $errorMessage")
                reportError(errorMessage)
            }

            override fun onWebRtcAudioRecordError(errorMessage: String) {
                Log.e(TAG, "onWebRtcAudioRecordError: $errorMessage")
                reportError(errorMessage)
            }
        }

        val audioTrackErrorCallback = object : AudioTrackErrorCallback {
            override fun onWebRtcAudioTrackInitError(errorMessage: String) {
                Log.e(TAG, "onWebRtcAudioTrackInitError: $errorMessage")
                reportError(errorMessage)
            }

            override fun onWebRtcAudioTrackStartError(
                errorCode: JavaAudioDeviceModule.AudioTrackStartErrorCode, errorMessage: String
            ) {
                Log.e(TAG, "onWebRtcAudioTrackStartError: $errorCode. $errorMessage")
                reportError(errorMessage)
            }

            override fun onWebRtcAudioTrackError(errorMessage: String) {
                Log.e(TAG, "onWebRtcAudioTrackError: $errorMessage")
                reportError(errorMessage)
            }
        }

        return JavaAudioDeviceModule.builder(context)
            .setUseHardwareAcousticEchoCanceler(!params.disableBuiltInAEC)
            .setUseHardwareNoiseSuppressor(!params.disableBuiltInNS)
            .setAudioRecordErrorCallback(audioRecordErrorCallback)
            .setAudioTrackErrorCallback(audioTrackErrorCallback)
            .createAudioDeviceModule()
    }

  private fun reportError(errorMessage: String) {
    Log.e(TAG, "Peer connection error: $errorMessage")
    executor.execute{
      if (!isError) {
        events.RtcConnectionError(errorMessage)
        isError = true
      }
    }
  }

    //#endregion


}


