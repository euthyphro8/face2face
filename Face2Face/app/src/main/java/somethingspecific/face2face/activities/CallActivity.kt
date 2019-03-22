package somethingspecific.face2face.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.webrtc.*
import somethingspecific.face2face.R
import somethingspecific.face2face.net.StreamConnection


class CallActivity : AppCompatActivity() {


    private var client: StreamConnection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val localVideoView = findViewById<SurfaceViewRenderer>(R.id.localView)
//        localVideoView.setMirror(true)

        val remoteVideoView = findViewById<SurfaceViewRenderer>(R.id.remoteView)
//        remoteVideoView.setMirror(true)

        client = StreamConnection(this, localVideoView, remoteVideoView)
    }
}
