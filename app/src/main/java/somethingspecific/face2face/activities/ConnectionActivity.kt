package somethingspecific.face2face.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
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
    }

    private fun onReceivedServerEndpoint(endpoint: String) {
        switchToNextView()
    }

    private fun switchToNextView() {
        var loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }

}
