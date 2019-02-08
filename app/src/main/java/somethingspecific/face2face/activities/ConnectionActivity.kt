package somethingspecific.face2face.activities

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.View
import somethingspecific.face2face.R

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cnt_btn.setOnClickListener {
            var endpoint = server_address_label.text?.toString() ?? ""
            onReceivedServerEndpoint()
        }
    }



    private fun onReceivedServerEndpoint(endpoint: String) {

    }


}
