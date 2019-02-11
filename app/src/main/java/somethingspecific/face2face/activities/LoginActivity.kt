package somethingspecific.face2face.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_connection.*
import kotlinx.android.synthetic.main.activity_login.*
import somethingspecific.face2face.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_btn.setOnClickListener {
            var username = user_text.text?.toString() ?: ""
            var email = email_text.text?.toString() ?: ""
            onLogin(username, email)
        }
    }

    private fun onLogin(username: String, email: String) {
        switchToNextView()
    }

    private fun switchToNextView() {
        var contactIntent = Intent(this, ContactActivity::class.java)
        startActivity(contactIntent)
    }
}
