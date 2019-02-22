package somethingspecific.face2face.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import somethingspecific.face2face.R
import org.webrtc.Camera1Enumerator
import org.webrtc.VideoCapturer
import org.webrtc.CameraEnumerator
import org.webrtc.VideoRenderer
import org.webrtc.EglBase
import org.webrtc.SurfaceViewRenderer
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnectionFactory



class CallActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true)
        val options = PeerConnectionFactory.Options()
        val peerConnectionFactory = PeerConnectionFactory(options)
        val videoCapturerAndroid = createVideoCapturer()
        val constraints = MediaConstraints()

        val videoSource = peerConnectionFactory.createVideoSource(videoCapturerAndroid)
        val localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource)
        val audioSource = peerConnectionFactory.createAudioSource(constraints)
        val localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource)

        //Starts the camera
        videoCapturerAndroid?.startCapture(1000, 1000, 24)

        val remoteVideoView = findViewById<SurfaceViewRenderer>(R.id.remoteView)
        remoteVideoView.setMirror(true)

        val remoteRootEglBase = EglBase.create()
        remoteVideoView.init(remoteRootEglBase.eglBaseContext, null)
        localVideoTrack.addRenderer(VideoRenderer(remoteVideoView))


        val localVideoView = findViewById<SurfaceViewRenderer>(R.id.localView)
        remoteVideoView.setMirror(true)

        val localRootEglBase = EglBase.create()
        localVideoView.init(localRootEglBase.eglBaseContext, null)
        localVideoTrack.addRenderer(VideoRenderer(localVideoView))
    }

    private fun createVideoCapturer(): VideoCapturer? {
        return createCameraCapturer(Camera1Enumerator(false))
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames
        // Trying to find a front facing camera!
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                val videoCapturer = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        // We were not able to find a front cam. Look for other cameras
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                val videoCapturer = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }

}
