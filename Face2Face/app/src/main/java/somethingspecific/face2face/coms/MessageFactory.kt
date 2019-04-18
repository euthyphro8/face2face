package somethingspecific.face2face.coms

import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import java.util.Arrays.asList
import android.os.AsyncTask.execute
import org.webrtc.PeerConnectionFactory
import android.R.string.cancel
import org.webrtc.MediaConstraints
import org.webrtc.Logging
import org.webrtc.SoftwareVideoDecoderFactory
import org.webrtc.SoftwareVideoEncoderFactory
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.VideoDecoderFactory
import org.webrtc.VideoEncoderFactory
import org.webrtc.audio.AudioDeviceModule
import java.io.File.separator
import android.os.Environment.getExternalStorageDirectory
import android.os.AsyncTask.execute
import org.webrtc.VideoCapturer
import org.webrtc.VideoSink
import java.util.Collections.singletonList
import org.webrtc.EglBase



public class MessageFactory {

    companion object {

        public fun Info(id:String, user:String, avatar:String):String {
            val msg = JSONObject()
            msg.put("type", "Info")
            msg.put("sender", id)
            msg.put("username", user)
            msg.put("avatar", avatar)
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