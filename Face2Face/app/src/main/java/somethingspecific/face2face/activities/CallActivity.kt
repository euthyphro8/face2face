package somethingspecific.face2face.activities

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_call.*
import org.webrtc.*
import somethingspecific.face2face.R
import somethingspecific.face2face.coms.Client
import org.webrtc.EglBase.create as createEgl
import org.webrtc.VideoCapturer
import org.webrtc.Logging
import org.webrtc.CameraEnumerator
import org.webrtc.VideoSink
import org.webrtc.VideoFrame






class CallActivity : AppCompatActivity() {


    private class ProxyVideoSink : VideoSink {
        private val TAG = "ProxyVideoSink"
        private var target: VideoSink? = null

        @Synchronized override fun onFrame(frame: VideoFrame) {
            if (target == null) {
                Logging.d(TAG, "Dropping frame in proxy because target is null.")
                return
            }
            target!!.onFrame(frame)
        }

        @Synchronized fun setTarget(target: VideoSink) {
            this.target = target
        }
    }

    private val TAG = "CallActivity"

    private var capturer: VideoCapturer? = null
    private var remoteSink = ProxyVideoSink()
    private var localSink = ProxyVideoSink()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val localVideoView = localView//findViewById<SurfaceViewRenderer>(R.id.localView)
//        localVideoView.setMirror(true)

//        val remoteVideoView = //findViewById<SurfaceViewRenderer>(R.id.remoteView)
//        remoteVideoView.setMirror(true)

        var root = createEgl()

        localVideoView.init(root.eglBaseContext, null)
//        remoteVideoView.init(root.eglBaseContext, null)
        capturer = createCameraCapturer(Camera1Enumerator())

        localSink.setTarget(localVideoView)
        Client.instance().call(this.baseContext, root, capturer, localVideoView, localVideoView)
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
}
