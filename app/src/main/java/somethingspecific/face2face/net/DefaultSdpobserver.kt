package somethingspecific.face2face.net

import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

class DefaultSdbObserver(name:String) : SdpObserver
{
    val observerName:String = name

    override fun onSetFailure(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSetSuccess() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateSuccess(p0: SessionDescription?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateFailure(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

