package somethingspecific.face2face.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_contact.*
import somethingspecific.face2face.R

class ContactActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        call_btn.setOnClickListener {
            var client = ""
            onCall(client)
        }
    }

    private fun onCall(client: String) {
        switchToNextView()
    }

    private fun switchToNextView() {
        var callIntent = Intent(this, CallActivity::class.java)
        startActivity(callIntent)
    }
}
