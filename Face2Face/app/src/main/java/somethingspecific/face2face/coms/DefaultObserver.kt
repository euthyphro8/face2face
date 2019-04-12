package somethingspecific.face2face.coms

import org.webrtc.*

class DefaultObserver(name:String) : PeerConnection.Observer
{
    val observerName:String = name

    override fun onIceCandidate(p0: IceCandidate?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAddStream(p0: MediaStream?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDataChannel(p0: DataChannel?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRemoveStream(p0: MediaStream?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRenegotiationNeeded() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}