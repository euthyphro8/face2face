package somethingspecific.face2face.coms

import android.content.Context
import android.util.Log
import org.json.JSONObject
import org.webrtc.*
import somethingspecific.face2face.events.Event
import somethingspecific.face2face.events.EventManager



public class Client(address:String, username:String, email:String) {


    public val ClientListEvent = Event<Array<ClientParameters>>()


    companion object {
        var instance: Client? = null
        fun instance():Client {
            if(instance == null) {
                throw Exception("Client has not be initialized!")
            }
            return instance!!
        }
    }

    private val TAG = "ClientManager"

    private var events: EventManager
    private var signal: SignalClient
    private var stream: StrippedStreamClient?
    private var params: ClientParameters
    private var target: ClientParameters?

    private var peers: ArrayList<ClientParameters>

    init{
        target = null
        peers = ArrayList()
        params = ClientParameters(username, email,
            "https://scontent-lax3-1.xx.fbcdn.net/v/t1.0-9/5616975" +
                    "1_2328522497199217_8528485562888224768_n.jpg?_nc_cat=10" +
                    "6&_nc_ht=scontent-lax3-1.xx&oh=af5534402e7907046a72134c12382" +
                    "81c&oe=5D4AF933",
            "Online")
        events = EventManager()
        signal = SignalClient(events, address)
        stream = null//StreamClient(null,  null, null, Events)

        events.OpenEvent += { onOpen() }
        events.MessageEvent += { msg -> onMessage(msg) }
        events.ClosedEvent += { onClose() }

        signal.connect()

    }


    public fun hasTarget():Boolean {
        if(target == null)
            return false
        return true
    }

    public fun setTarget(client: ClientParameters):Boolean {
        if(client.email == params.email)
            return false
        target = client
        return true
    }

    public fun call(context: Context, root: EglBase, capturer: VideoCapturer?, local: SurfaceViewRenderer, remote: SurfaceViewRenderer) {
        if(target != null) {
            stream = StrippedStreamClient(context, root, getStreamParameters(), events, capturer, local, remote)
            var offer = stream!!.createOffer()
            var msg = MessageFactory.Offer(params.email, target!!.email, offer)
            signal.send(msg)
        }
    }


    private fun getStreamParameters(): StreamParameters{
        return StreamParameters(true, false, false,
            1280, 720, 30, 0, "H264 High",
            true, true, 0, "opus",
            true, false, false, true,
            false, false, false,
            false, false)
    }


    private fun onOpen() {
        Log.d(TAG, "Client connected!")
        signal.send(MessageFactory.Info(params.email, params.username, params.avatar))
    }

    private fun onMessage(raw: String) {
        Log.d(TAG, "Got message: $raw")
        val msg = JSONObject(raw)
        val type = msg.getString("type")
        when (type) {
            "List" -> onList(msg.getJSONObject("clients"))
            "Offer" -> onOffer(msg.getString("target"),
                msg.getJSONObject("offer"))
            "Reply" -> onReply(msg.getString("target"),
                msg.getJSONObject("reply"))
            "Ice" -> onIce(msg.getString("target"),
                msg.getJSONObject("ice"))
        }
    }

    private fun onList(list:JSONObject) {
        peers.clear()
        list.keys().forEach {
            val client = list.getJSONObject(it)
            val email = client.getString("email")
            val user = client.getString("user")
            val avatar = client.getString("avatar")
            val status = client.getString("status")
//            if(email != params.email)
                peers.add(ClientParameters(user, email, avatar, status))
        }
        ClientListEvent(peers.toTypedArray())
    }

    private fun onOffer(target:String, offer:JSONObject) {
        Log.d(TAG, "Got offer")
    }

    private fun onReply(target:String, reply:JSONObject) {

    }

    private fun onIce(target:String, ice:JSONObject) {

    }

    private fun onClose() {
        Log.d(TAG, "Client disconnected!")
        stream?.let {
            //TODO : stream.close()
        }
    }

    //TODO: Implement all of these!
    private fun ConnectedEvent() {}
    private fun DisconnectedEvent() {}
    private fun RtcClosedEvent() {}
    private fun IceConnectedEvent() {}
    private fun IceDisconnectedEvent() {}
    private fun IceCandidateEvent(candidate: IceCandidate){}
    private fun IceCandidateRemovedEvent(candidates: Array<IceCandidate>) {}
    private fun LocalDescriptionEvent(desc: SessionDescription) {}
    private fun RtcStatsReady(stats: Array<StatsReport>) {}
    private fun RtcConnectionError(error: String) {}

}