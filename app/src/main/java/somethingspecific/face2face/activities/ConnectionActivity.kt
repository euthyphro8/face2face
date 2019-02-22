package somethingspecific.face2face.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import somethingspecific.face2face.R

import kotlinx.android.synthetic.main.activity_connection.*

class ConnectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)

        cnt_btn.setOnClickListener {
            var endpoint = server_address_label.text?.toString() ?: ""
            onReceivedServerEndpoint(endpoint)
        }

        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.CAMERA),
            101)
    }


    private fun onReceivedServerEndpoint(endpoint: String) {
        switchToNextView()
    }

    private fun switchToNextView() {
        var loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }

}
