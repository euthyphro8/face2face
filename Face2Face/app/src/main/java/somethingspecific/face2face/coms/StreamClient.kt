package somethingspecific.face2face.coms

import android.content.Context
import android.util.Log
import org.webrtc.*
import org.webrtc.audio.JavaAudioDeviceModule
import somethingspecific.face2face.events.EventManager
import java.util.*
import java.util.concurrent.Executors

class StreamClient(appContext: Context, rootEglBase: EglBase, eventManager: EventManager,
                   local: SurfaceViewRenderer, remote: SurfaceViewRenderer) {


    val TAG = "StreamClient"
    val VIDEO_TRACK_ID = "ARDAMSv0"
    val AUDIO_TRACK_ID = "ARDAMSa0"

    val executor = Executors.newSingleThreadExecutor()
    val context = appContext
    val root = rootEglBase

    val events = eventManager
    val pcObserver = PCObserver(eventManager)
    val sdpObserver = SdpObserver(this)
    var localSdp: SessionDescription? = null
    var candidates : MutableList<IceCandidate>
    var sdpMediaConstraints = MediaConstraints()
    var audioConstraints = MediaConstraints()

    var factory : PeerConnectionFactory?
    var peer : PeerConnection?

    var capturer: VideoCapturer?
    var source: VideoSource
    var helper: SurfaceTextureHelper

    var localTrack: VideoTrack

    var remoteSink: ProxyVideoSink? = ProxyVideoSink()
    var localSink: ProxyVideoSink? = ProxyVideoSink()

    var isInitiator = false


    init{
        //Initialize surface views with open el context
        local.init(root.eglBaseContext, null)
        local.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
        local.setEnableHardwareScaler(false)
//        local.setZOrderOnTop(true)

        remote.init(root.eglBaseContext, null)
        remote.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
        remote.setEnableHardwareScaler(true)

        //Give the video sink the target views
        localSink?.setTarget(local)
        remoteSink?.setTarget(remote)

        //Create the rtc factory
        factory = createFactory()
        initMediaConstraints()
        candidates = mutableListOf()

        //Initialize the camera itself and the
        capturer = createCameraCapturer(Camera1Enumerator())
        source = factory!!.createVideoSource(false)
        helper = SurfaceTextureHelper.create("CaptureThread", root.eglBaseContext)

        capturer!!.initialize(helper, context, source.capturerObserver)
        capturer!!.startCapture(1280, 720, 30)

        //Create rtc peer connection with observer
        val iceServers = getIceServers()//listOf<PeerConnection.IceServer>()
        peer = factory!!.createPeerConnection(getRtcConfig(iceServers), pcObserver)

        //Create local track
        localTrack = factory!!.createVideoTrack(VIDEO_TRACK_ID, source)
        localTrack.setEnabled(true)
        localTrack.addSink(localSink)

        //Add local track to peer
        val mediaStreamLabels = Collections.singletonList("ARDAMS")
        peer!!.addTrack(localTrack, mediaStreamLabels)

    }

    fun dispose() {
        peer?.dispose()
        factory?.stopAecDump()
        try {
            capturer?.stopCapture()
        }
        catch(e: InterruptedException) {
            Log.e(TAG, "Could not stop camera, this may be critical.")
        }
        capturer?.dispose()
        source.dispose()
        helper.dispose()
        localSink = null
        remoteSink = null
        factory?.dispose()
        root.release()
        events.RtcClosedEvent()
    }

    fun addIce(ice:IceCandidate) {
        if(peer?.remoteDescription == null)
            candidates.add(ice)
        else
            peer!!.addIceCandidate(ice)
    }
    fun handleOffer(offer: SessionDescription) {
        peer!!.setRemoteDescription(sdpObserver, offer)
    }
    fun handleReply(reply: SessionDescription) {
        peer!!.setRemoteDescription(sdpObserver, reply)
    }
    fun createReply() {
        peer!!.createAnswer(sdpObserver, sdpMediaConstraints)
    }
    fun createOffer()  {
        isInitiator = true
        peer!!.createOffer(sdpObserver, sdpMediaConstraints)
    }

    fun addStream(media: MediaStream) {
        for(track in media.videoTracks){
            track.addSink(remoteSink)
        }
    }


    private fun initMediaConstraints() {
        sdpMediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "false"))
        sdpMediaConstraints.mandatory.add( MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
    }

    private fun getRtcConfig(servers: List<PeerConnection.IceServer>): PeerConnection.RTCConfiguration {
        val rtcConfig = PeerConnection.RTCConfiguration(servers)
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.BALANCED
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA
        rtcConfig.enableDtlsSrtp = true // Enable DTLS for normal calls and disable for loopback calls.
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        return rtcConfig
    }

    private fun getIceServers(): List<PeerConnection.IceServer> {
        return listOf(
            PeerConnection.IceServer
                .builder("stun:stun.l.google.com:19302")
                .createIceServer(),
            PeerConnection.IceServer
                .builder("turn:192.158.29.39:3478?transport=udp")
                .setUsername("28224511:1379330808")
                .setPassword("JZEOEt2V3Qb0y27GRntt2u2PAYA=")
                .createIceServer(),
            PeerConnection.IceServer
                .builder("turn:192.158.29.39:3478?transport=tcp")
                .setUsername("28224511:1379330808")
                .setPassword("JZEOEt2V3Qb0y27GRntt2u2PAYA=")
                .createIceServer()
            )
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames
        // First, try to find front facing camera
        Logging.d(TAG, "Looking for front facing cameras.")
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.")
                val videoCapturer = enumerator.createCapturer(deviceName, null)

                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.")
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.")
                val videoCapturer = enumerator.createCapturer(deviceName, null)

                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }

    private fun createFactory(): PeerConnectionFactory {

        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(true)
                .createInitializationOptions())


        val adm = JavaAudioDeviceModule.builder(context)
            .setUseHardwareAcousticEchoCanceler(true)
            .setUseHardwareNoiseSuppressor(false)
            .createAudioDeviceModule()

        val encoderFactory = DefaultVideoEncoderFactory(root.eglBaseContext,
            false, true)
        val decoderFactory = DefaultVideoDecoderFactory(root.eglBaseContext)

        return PeerConnectionFactory.builder()
            .setOptions(PeerConnectionFactory.Options())
            .setAudioDeviceModule(adm)
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()
    }

    public fun drainCandidates() {
        peer ?: return
        Log.d(TAG, "Add " + candidates.size + " remote candidates")
        for (candidate in candidates)
            peer!!.addIceCandidate(candidate)
        candidates.clear()
    }


}