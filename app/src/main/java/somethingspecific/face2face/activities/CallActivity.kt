package somethingspecific.face2face.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.webrtc.*
import somethingspecific.face2face.R
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import somethingspecific.face2face.net.Connection


class CallActivity : AppCompatActivity() {


    private var client: Connection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val localVideoView = findViewById<SurfaceViewRenderer>(R.id.localView)
//        localVideoView.setMirror(true)

        val remoteVideoView = findViewById<SurfaceViewRenderer>(R.id.remoteView)
//        remoteVideoView.setMirror(true)

        client = Connection(this, localVideoView, remoteVideoView)
    }
}
