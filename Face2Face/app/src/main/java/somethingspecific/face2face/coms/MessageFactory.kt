package somethingspecific.face2face.coms

import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription


public class MessageFactory {

    companion object {

        public fun Info(id:String, user:String, avatar:String):String {
            val msg = JSONObject()
            msg.put("type", "Info")
            msg.put("sender", id)
            return msg.toString()
        }


        public fun Offer(id:String, target:String, offer:SessionDescription):String {
            val msg = JSONObject()
            msg.put("type", "Offer")
            msg.put("sender", id)
            msg.put("target", target)
            msg.put("offer", offer)
            return msg.toString()
        }

        public fun Reply(id:String, target:String, reply:SessionDescription):String {
            val msg = JSONObject()
            msg.put("type", "Reply")
            msg.put("sender", id)
            msg.put("target", target)
            msg.put("reply", reply)
            return msg.toString()
        }

        public fun Ice(id:String, target:String, ice:IceCandidate):String {
            val msg = JSONObject()
            msg.put("type", "Ice")
            msg.put("sender", id)
            msg.put("target", target)
            msg.put("ice", ice)
            return msg.toString()
        }

        public fun Close(id:String, target:String):String {
            val msg = JSONObject()
            msg.put("type", "Close")
            msg.put("sender", id)
            msg.put("target", target)
            return msg.toString()
        }


    }


}