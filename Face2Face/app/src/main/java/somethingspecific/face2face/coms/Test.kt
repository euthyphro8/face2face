
import android.R.attr.start
import android.os.ParcelFileDescriptor
import java.io.File.separator
import android.os.Environment.getExternalStorageDirectory
//import sun.font.LayoutPathImpl.getPath
import org.webrtc.VideoSink
import java.util.Collections.singletonList
import org.webrtc.Logging
import android.R.id
import org.webrtc.DataChannel
import org.webrtc.PeerConnection

//package somethingspecific.face2face.coms
//
//import org.webrtc.StatsReport
//import org.webrtc.IceCandidate
//import org.webrtc.SessionDescription
//import org.webrtc.PeerConnectionFactory
//import android.os.AsyncTask.execute
//import somethingspecific.face2face.coms.PeerConnectionClient.PeerConnectionEvents
//import somethingspecific.face2face.coms.PeerConnectionClient.PeerConnectionParameters
//import org.webrtc.EglBase
//
//
///**
// * Create a PeerConnectionClient with the specified parameters. PeerConnectionClient takes
// * ownership of |eglBase|.
// */
//fun PeerConnectionClient(
//    appContext: Context, eglBase: EglBase,
//    peerConnectionParameters: PeerConnectionParameters, events: PeerConnectionEvents
//): ??? {
//    this.rootEglBase = eglBase
//    this.appContext = appContext
//    this.events = events
//    this.peerConnectionParameters = peerConnectionParameters
//    this.dataChannelEnabled = peerConnectionParameters.dataChannelParameters != null
//
//    Log.d(TAG, "Preferred video codec: " + getSdpVideoCodecName(peerConnectionParameters))
//
//    val fieldTrials = getFieldTrials(peerConnectionParameters)
//    executor.execute({
//        Log.d(TAG, "Initialize WebRTC. Field trials: $fieldTrials")
//        PeerConnectionFactory.initialize(
//            PeerConnectionFactory.InitializationOptions.builder(appContext)
//                .setFieldTrials(fieldTrials)
//                .setEnableInternalTracer(true)
//                .createInitializationOptions()
//        )
//    })
//}
