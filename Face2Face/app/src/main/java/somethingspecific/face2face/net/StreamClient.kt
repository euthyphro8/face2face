package somethingspecific.face2face.net

import android.app.Activity
import org.webrtc.*
import org.webrtc.IceCandidate
import org.webrtc.PeerConnection
import org.webrtc.VideoTrack
import org.webrtc.MediaStream
import org.webrtc.MediaConstraints
import java.security.KeyStore


class StreamClient() {

    private var peer: PeerConnection

    init {

        val peerFactory = NativePeerConnectionFactory { 0 } //TODO : Find docs!
        peer = PeerConnection(peerFactory)




//        val videoCapturerAndroid = createVideoCapturer(Camera1Enumerator(false))
//        val constraints = MediaConstraints()
//
//        val videoSource = peerConnectionFactory.createVideoSource(false)
//        localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource)
//
//
//        val audioSource = peerConnectionFactory.createAudioSource(constraints)
//        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource)

//        videoCapturerAndroid?.startCapture(1000, 1000, 24)



//        call(peerConnectionFactory)
    }



    public fun sendOffer() {

    }

    public fun sendReply() {

    }

    public fun setRemoteDescription() {

    }

    public fun attachView() {

//        val localRootEglBase = EglRenderer()//EglBase.create()
//        localVideoView.init(localRootEglBase.eglBaseContext, null)
//        localVideoTrack.addRenderer(VideoRenderer(localVideoView))
    }


}


