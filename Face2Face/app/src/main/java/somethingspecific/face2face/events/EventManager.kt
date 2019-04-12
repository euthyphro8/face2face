package somethingspecific.face2face.events

import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import org.webrtc.StatsReport



public class EventManager {

    //Signal websocket events
    /**
     * Callback fired once the websocket connection is established.
     */
    public val OpenEvent = EmptyEvent()

    /**
     * Callback fired every time a message is received in the websocket.
     */
    public val MessageEvent = Event<String>()

    /**
     * Callback fired once the websocket connection is closed.
     */
    public val ClosedEvent = EmptyEvent()

    // ----------------- RTC Events

    /**
     * Callback fired once DTLS connection is established (PeerConnectionState
     * is CONNECTED).
     */
    public val ConnectedEvent = EmptyEvent()

    /**
     * Callback fired once DTLS connection is disconnected (PeerConnectionState
     * is DISCONNECTED).
     */
    public val DisconnectedEvent = EmptyEvent()

    /**
     * Callback fired once peer connection is closed.
     */
    public val RtcClosedEvent = EmptyEvent()

    /**
     * Callback fired once connection is established (IceConnectionState is
     * CONNECTED).
     */
    public val IceConnectedEvent = EmptyEvent()

    /**
     * Callback fired once connection is disconnected (IceConnectionState is
     * DISCONNECTED).
     */
    public val IceDisconnectedEvent = EmptyEvent()

    /**
     * Callback fired once local Ice candidate is generated.
     */
    public val IceCandidateEvent = Event<IceCandidate>()

    /**
     * Callback fired once local ICE candidates are removed.
     */
    public val IceCandidateRemovedEvent = Event<Array<IceCandidate>>()

    /**
     * Callback fired once local SDP is created and set.
     */
    public val LocalDescriptionEvent = Event<SessionDescription>()

    /**
     * Callback fired once peer connection statistics is ready.
     */
    public val RtcStatsReady = Event<Array<StatsReport>>()

    /**
     * Callback fired once peer connection error happened.
     */
    public val RtcConnectionError = Event<String>()



}