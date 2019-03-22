package somethingspecific.face2face.net

import android.app.Activity
import org.webrtc.*
import org.webrtc.IceCandidate
import org.webrtc.PeerConnection
import org.webrtc.VideoTrack
import org.webrtc.MediaStream
import org.webrtc.MediaConstraints
import java.security.KeyStore


class StreamConnection(parent:Activity, local:SurfaceViewRenderer, remote:SurfaceViewRenderer) {

    private val parentView:Activity = parent

    private var localPeer:PeerConnection? = null
    private val localVideoTrack:VideoTrack
    private val localAudioTrack:AudioTrack
    private val localVideoView:SurfaceViewRenderer = local

    private var remotePeer:PeerConnection? = null
    private lateinit var remoteVideoTrack:VideoTrack
    private lateinit var remoteAudioTrack:AudioTrack
    private val remoteVideoView:SurfaceViewRenderer = remote

    init {
        val peerConnectionFactory = PeerConnectionFactory.builder().createPeerConnectionFactory()

//        val videoCapturerAndroid = createVideoCapturer(Camera1Enumerator(false))
        val constraints = MediaConstraints()

        val videoSource = peerConnectionFactory.createVideoSource(false)
        localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource)


        val audioSource = peerConnectionFactory.createAudioSource(constraints)
        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource)

//        videoCapturerAndroid?.startCapture(1000, 1000, 24)

//        val localRootEglBase = EglRenderer()//EglBase.create()
//        localVideoView.init(localRootEglBase.eglBaseContext, null)
//        localVideoTrack.addRenderer(VideoRenderer(localVideoView))


//        call(peerConnectionFactory)
    }


//    private fun call(peerConnectionFactory:PeerConnectionFactory) {
//        //we already have video and audio tracks. Now create peerconnections
//        val iceServers = ArrayList<PeerConnection.IceServer>()
//
//        //create sdpConstraints
//        var sdpConstraints = MediaConstraints()
//        sdpConstraints.mandatory.add(MediaConstraints.KeyValuePair("offerToReceiveAudio", "true"))
//        sdpConstraints.mandatory.add(MediaConstraints.KeyValuePair("offerToReceiveVideo", "true"))
//
//
//        //creating localPeer
//        localPeer = peerConnectionFactory.createPeerConnection(
//            iceServers,
//            sdpConstraints,
//            DefaultObserver("localPeerCreation"))
//
//        //creating remotePeer
//        remotePeer = peerConnectionFactory.createPeerConnection(
//            iceServers,
//            sdpConstraints,
//            DefaultObserver("remotePeerCreation"))
//
//
//
//        //creating local media stream
//        val stream = peerConnectionFactory.createLocalMediaStream("102")
//        stream.addTrack(localAudioTrack)
//        stream.addTrack(localVideoTrack)
//
//        localPeer?.addStream(stream)
//
//        //creating Offer
//        localPeer?.createOffer(
////            object : CustomSdpObserver("localCreateOffer") {
////            fun onCreateSuccess(sessionDescription: SessionDescription) {
////                //we have localOffer. Set it as local desc for localpeer and remote desc for remote peer.
////                //try to create answer from the remote peer.
////                super.onCreateSuccess(sessionDescription)
////                localPeer?.setLocalDescription(CustomSdpObserver("localSetLocalDesc"), sessionDescription)
////                remotePeer?.setRemoteDescription(CustomSdpObserver("remoteSetRemoteDesc"), sessionDescription)
////                remotePeer?.createAnswer(object : CustomSdpObserver("remoteCreateOffer") {
////                    fun onCreateSuccess(sessionDescription: SessionDescription) {
////                        //remote answer generated. Now set it as local desc for remote peer and remote desc for local peer.
////                        super.onCreateSuccess(sessionDescription)
////                        remotePeer?.setLocalDescription(CustomSdpObserver("remoteSetLocalDesc"), sessionDescription)
////                        localPeer?.setRemoteDescription(CustomSdpObserver("localSetRemoteDesc"), sessionDescription)
////                    }
////                }, MediaConstraints())
////            }
////        }
//            DefaultSdbObserver("localCreateOffer"), sdpConstraints)
//    }


//    private fun hangup() {
//        localPeer?.close()
//        remotePeer?.close()
//        localPeer = null
//        remotePeer = null
//    }
//
//    private fun gotRemoteStream(stream: MediaStream) {
//        //we have remote video stream. add to the renderer.
//        val videoTrack = stream.videoTracks.first
//        val audioTrack = stream.audioTracks.first
//
//        parentView.runOnUiThread {
//            try {
//                val remoteRootEglBase = EglBase.create()
//                remoteVideoView.init(remoteRootEglBase.eglBaseContext, null)
//                localVideoTrack.addRenderer(VideoRenderer(remoteVideoView))
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    fun onIceCandidateReceived(peer: PeerConnection, iceCandidate: IceCandidate) {
//        //we have received ice candidate. We can set it to the other peer.
//        if (peer === localPeer) {
//            remotePeer?.addIceCandidate(iceCandidate)
//        } else {
//            localPeer?.addIceCandidate(iceCandidate)
//        }
//    }
//
//
//    private fun createVideoCapturer(enumerator: CameraEnumerator): VideoCapturer? {
//        val deviceNames = enumerator.deviceNames
//        // Trying to find a front facing camera!
//        for (deviceName in deviceNames) {
//            if (enumerator.isFrontFacing(deviceName)) {
//                val videoCapturer = enumerator.createCapturer(deviceName, null)
//                if (videoCapturer != null) {
//                    return videoCapturer
//                }
//            }
//        }
//        // We were not able to find a front cam. Look for other cameras
//        for (deviceName in deviceNames) {
//            if (!enumerator.isFrontFacing(deviceName)) {
//                val videoCapturer = enumerator.createCapturer(deviceName, null)
//                if (videoCapturer != null) {
//                    return videoCapturer
//                }
//            }
//        }
//        return null
//    }
}


