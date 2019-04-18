package somethingspecific.face2face.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_contact.*
import somethingspecific.face2face.coms.ClientParameters
import somethingspecific.face2face.R
import somethingspecific.face2face.coms.Client
import somethingspecific.face2face.models.ClientAdapter
import java.lang.Exception


class ContactActivity : AppCompatActivity() {

    private var Clients: ClientAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)




        var username = intent.getStringExtra("username")
        var email = intent.getStringExtra("email")

        if(!username.isNullOrEmpty())
            userTxt.text = username.capitalize()
        if(!email.isNullOrEmpty())
            emailTxt.text = email

        Client.instance = Client("https://35.192.33.173:8887", username, email)
        Client.instance().ClientListEvent += { runOnUiThread{ onContactList(it)} }


        call_btn.isEnabled = true
        call_btn.setOnClickListener{ runOnUiThread{ onCall() }}
        clientLst.setOnItemClickListener{ parent, view, position, id ->
            runOnUiThread{ onClientClick(position) }
        }
    }

    private fun onCall() {
        if(Client.instance().hasTarget()){
            switchToNextView()
        }
    }

    private fun onClientClick(pos: Int) {
        var target = Clients?.data?.get(pos)
            ?: throw Exception("[Contact] Could not parse client parameters from list.")
        if(Client.instance().setTarget(target)) {
            call_btn.isEnabled = true
        }
    }

    private fun onContactList(clients: Array<ClientParameters> ) {
        Clients = ClientAdapter(this, R.layout.client_listview_item_row, clients)
        clientLst.adapter = Clients
    }

    private fun switchToNextView() {
        var callIntent = Intent(this, CallActivity::class.java)
        startActivity(callIntent)
    }
}
