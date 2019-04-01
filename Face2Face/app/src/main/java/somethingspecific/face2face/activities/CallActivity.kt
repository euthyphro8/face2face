package somethingspecific.face2face.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.webrtc.*
import somethingspecific.face2face.R
import somethingspecific.face2face.net.StreamClient


class CallActivity : AppCompatActivity() {


    private var client: StreamClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val localVideoView = findViewById<SurfaceViewRenderer>(R.id.localView)
//        localVideoView.setMirror(true)

        val remoteVideoView = findViewById<SurfaceViewRenderer>(R.id.remoteView)
//        remoteVideoView.setMirror(true)

        client = StreamClient(this, localVideoView, remoteVideoView)
    }
}
