package somethingspecific.face2face.activities

import android.Manifest
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.Window
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_call.*
import org.webrtc.*
import somethingspecific.face2face.R
import somethingspecific.face2face.coms.Client
import org.webrtc.EglBase.create as createEgl
import somethingspecific.face2face.coms.StreamClient


class CallActivity : AppCompatActivity() {


    private val TAG = "CallActivity"
    private var client: StreamClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)


        //TODO : I guess at some point I should find out which ones I actually need... maybe.
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE
                ),101)

        hangupBtn.setOnClickListener{ hangup(true) }
        Client.instance().PartnerCloseEvent += { hangup(false) }

    }


    fun hangup(notifyPartner: Boolean) {
        if(notifyPartner)
            Client.instance().closePartner()
        Client.instance().hangup()
        runOnUiThread{
            var contactIntent = Intent(this, ContactActivity::class.java)
            startActivity(contactIntent)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Client.instance().call(this.baseContext, EglBase.create(), localView, remoteView)
    }




}
