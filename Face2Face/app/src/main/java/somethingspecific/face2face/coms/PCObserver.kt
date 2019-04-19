package somethingspecific.face2face.coms

import android.os.AsyncTask
import android.util.Log
import org.webrtc.*
import somethingspecific.face2face.events.EventManager

// Implementation detail: observe ICE & stream changes and react accordingly.
class PCObserver(eventManager: EventManager) : PeerConnection.Observer {

    private val events = eventManager

    override fun onDataChannel(p0: DataChannel?) {}

    override fun onIceCandidate(candidate: IceCandidate) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            events.IceCandidateEvent(candidate)
        }
    }

    override fun onIceCandidatesRemoved(candidates: Array<IceCandidate>) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            events.IceCandidateRemovedEvent(candidates)
        }
    }

    override fun onSignalingChange(newState: PeerConnection.SignalingState) {
        Log.d("Observer", "SignalingState: $newState")
    }

    override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            Log.d("Observer", "IceConnectionState: $newState")
            when (newState) {
                PeerConnection.IceConnectionState.CONNECTED -> events.IceConnectedEvent()
                PeerConnection.IceConnectionState.DISCONNECTED -> events.IceConnectedEvent()
                PeerConnection.IceConnectionState.FAILED -> Log.e("IceDisconnectedEvent", "ICE connection failed.")
                PeerConnection.IceConnectionState.NEW -> Log.d("Observer", "New connection.")
                PeerConnection.IceConnectionState.CLOSED -> Log.d("Observer", "Ice closed!")
                PeerConnection.IceConnectionState.CHECKING -> Log.d("Observer", "Checking ice...")
                PeerConnection.IceConnectionState.COMPLETED -> Log.d("Observer", "Ice just completed.")
            }
        }
    }

    override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            Log.d("Observer", "PeerConnectionState: " + newState!!)
            when (newState) {
                PeerConnection.PeerConnectionState.CONNECTED -> events.ConnectedEvent()
                PeerConnection.PeerConnectionState.DISCONNECTED -> events.DisconnectedEvent()
                PeerConnection.PeerConnectionState.FAILED -> Log.e("Observer", "DTLS connection failed.")
                PeerConnection.PeerConnectionState.NEW -> Log.d("Observer", "New connection.")
                PeerConnection.PeerConnectionState.CLOSED -> Log.d("Observer", "Connection closed.")
                PeerConnection.PeerConnectionState.CONNECTING -> Log.d("Observer", "Now connecting.")
            }
        }
    }

    override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState) {
        Log.d("Observer", "IceGatheringState: $newState")
    }

    override fun onIceConnectionReceivingChange(receiving: Boolean) {
        Log.d("Observer", "IceConnectionReceiving changed to $receiving")
    }

    override fun onAddStream(stream: MediaStream) {
        Log.d("Observer", "Got stream... (this shouldn't happen!)")
    }

    override fun onRemoveStream(stream: MediaStream) {
        Log.d("Observer", "Stream removed!")
    }

    override fun onRenegotiationNeeded() {
        Log.d("Observer", "Renegotiation needed!")
    }

    override fun onAddTrack(receiver: RtpReceiver, mediaStreams: Array<MediaStream>)
    {
        Log.d("Observer", "Track added!")
        events.AddMediaEvent(mediaStreams)
    }
}