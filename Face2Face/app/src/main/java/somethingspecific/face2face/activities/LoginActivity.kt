package somethingspecific.face2face.activities

import android.Manifest
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(somethingspecific.face2face.R.layout.activity_login)



        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.CAMERA),
            101)


        login_btn.setOnClickListener {
            var username = user_text.text?.toString() ?: ""
            var email = email_text.text?.toString() ?: ""
            onLogin(username, email)
        }
    }

    private fun onLogin(username: String, email: String) {
        switchToNextView(username, email)
    }

    private fun switchToNextView(username:String, email:String) {
        var contactIntent = Intent(this, ContactActivity::class.java)
        contactIntent.putExtra("username", username).putExtra("email", email)
        startActivity(contactIntent)
    }
}
