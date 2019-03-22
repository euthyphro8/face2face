package somethingspecific.face2face.net

import org.webrtc.PeerConnection
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import android.R.attr.tag
import android.util.Log


public class DefaultSdbObserver(logTag:String) : SdpObserver
{
    private val tag:String = "${javaClass.canonicalName ?: ""}$logTag"

    override fun onCreateSuccess(sessionDescription: SessionDescription) {
        Log.d(tag, "onCreateSuccess() called with: sessionDescription = [$sessionDescription]")
    }

    override fun onSetSuccess() {
        Log.d(tag, "onSetSuccess() called")
    }

    override fun onCreateFailure(s: String) {
        Log.d(tag, "onCreateFailure() called with: s = [$s]")
    }

    override fun onSetFailure(s: String) {
        Log.d(tag, "onSetFailure() called with: s = [$s]")
    }
}

