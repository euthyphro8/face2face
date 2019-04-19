package somethingspecific.face2face.coms

import android.util.Log
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

// Implementation detail: handle offer creation/signaling and answer setting,
// as well as adding remote ICE candidates once the answer SDP is set.
class SdpObserver(owner: StreamClient) : SdpObserver {

    private val TAG = "SdpObserver"
    private val client = owner

    override fun onCreateSuccess(origSdp: SessionDescription) {
        if (client.localSdp != null) {
            Log.e("Observer", "Multiple SDP create.")
            return
        }
        val sdpDescription = origSdp.description
//                sdpDescription = preferCodec(sdpDescription, VIDEO_CODEC_H264_HIGH, false)
        val sdp = SessionDescription(origSdp.type, sdpDescription)
        client.localSdp = sdp

        client.executor.execute {
            client.peer?.let {
                Log.d("Observer", "Set local SDP from " + sdp.type)
                it.setLocalDescription(client.sdpObserver, sdp)
            }
        }
    }

    override fun onSetSuccess() {
        client.peer ?: return
        client.executor.execute {
            if (client.isInitiator) {
                // For offering peer connection we first create offer and set
                // local SDP, then after receiving answer set remote SDP.
                if (client.peer!!.remoteDescription == null) {
                    // We've just set our local SDP so time to send it.
                    Log.d("Observer", "Local SDP set successfully")
                    client.peer!!.localDescription?.let {
                        client.events.OfferEvent(it)
                    }
                } else {
                    // We've just set remote description, so drain remote
                    // and send local ICE candidates.
                    Log.d("Observer", "Remote SDP set successfully")
                    client.drainCandidates()
                }
            } else {
                // For answering peer connection we set remote SDP and then
                // create answer and set local SDP.
                if (client.peer!!.localDescription != null) {
                    // We've just set our local SDP so time to send it, drain
                    // remote and send local ICE candidates.
                    Log.d("Observer", "Local SDP set successfully")
                    client.peer!!.localDescription?.let {
                        client.events.ReplyEvent(it)
                    }

                    client.drainCandidates()
                } else {
                    // We've just set remote SDP - do nothing for now -
                    // answer will be created soon.
                    Log.d("Observer", "Remote SDP set succesfully")
                    client.createReply()
                }
            }
        }
    }

    override fun onCreateFailure(error: String) {
        Log.e(TAG, "createSDP error: $error")
    }

    override fun onSetFailure(error: String) {
        Log.e(TAG, "setSDP error: $error")
    }
}