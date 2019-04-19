package somethingspecific.face2face.activities

import android.Manifest
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_login.*
import somethingspecific.face2face.R


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(somethingspecific.face2face.R.layout.activity_login)


        login_btn.setOnClickListener {
            var username = user_text.text?.toString() ?: ""
            var email = email_text.text?.toString() ?: ""
            onLogin(username, email)
        }

        startAnim()
    }

    private fun startAnim() {
        rtcFlowerImg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely))
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
