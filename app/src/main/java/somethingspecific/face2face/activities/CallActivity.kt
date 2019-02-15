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

        //Initialize PeerConnectionFactory globals.
        //Params are context, initAudio,initVideo and videoCodecHwAcceleration
        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true)

        //Create a new PeerConnectionFactory instance.
        val options = PeerConnectionFactory.Options()
        val peerConnectionFactory = PeerConnectionFactory(options)


        //Now create a VideoCapturer instance. Callback methods are there if you want to do something! Duh!
        val videoCapturerAndroid = createVideoCapturer()
        //Create MediaConstraints - Will be useful for specifying video and audio constraints. More on this later!
        val constraints = MediaConstraints()

        //Create a VideoSource instance
        val videoSource = peerConnectionFactory.createVideoSource(videoCapturerAndroid)
        val localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource)

        //create an AudioSource instance
        val audioSource = peerConnectionFactory.createAudioSource(constraints)
        val localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource)

        //we will start capturing the video from the camera
        //params are width,height and fps
        videoCapturerAndroid?.startCapture(1000, 1000, 30)

        //create surface renderer, init it and add the renderer to the track
        val videoView = findViewById<SurfaceViewRenderer>(R.id.localView)
        videoView.setMirror(true)

        val rootEglBase = EglBase.create()
        videoView.init(rootEglBase.eglBaseContext, null)

        localVideoTrack.addRenderer(VideoRenderer(videoView))
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
