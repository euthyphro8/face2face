package somethingspecific.face2face.coms


class StreamParameters(
    var videoCallEnabled: Boolean,
    var loopback: Boolean,
    var tracing: Boolean,
    var videoWidth: Int,
    var videoHeight: Int,
    var videoFps: Int,
    var videoMaxBitrate: Int,
    var videoCodec: String,
    var videoCodecHwAcceleration: Boolean,
    var videoFlexfecEnabled: Boolean,
    var audioStartBitrate: Int,
    var audioCodec: String,
    var noAudioProcessing: Boolean,
    var aecDump: Boolean,
    var saveInputAudioToFile: Boolean,
    var useOpenSLES: Boolean,
    var disableBuiltInAEC: Boolean,
    var disableBuiltInAGC: Boolean,
    var disableBuiltInNS: Boolean,
    var disableWebRtcAGCAndHPF: Boolean,
    var enableRtcEventLog: Boolean
)


