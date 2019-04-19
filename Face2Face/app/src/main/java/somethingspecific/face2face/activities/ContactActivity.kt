package somethingspecific.face2face.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_contact.*
import somethingspecific.face2face.coms.ClientParameters
import somethingspecific.face2face.R
import somethingspecific.face2face.coms.Client
import somethingspecific.face2face.models.ClientAdapter
import java.lang.Exception


class ContactActivity : AppCompatActivity() {

    private var clients: ClientAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)


        var username = ""
        var email = ""
        if(Client.instance == null) {
            username = intent.getStringExtra("username")
            email = intent.getStringExtra("email")

            Client.instance = Client("https://35.192.33.173:8887", username, email)
        }
        else {
            Client.instance().reInit()
            username = Client.instance().params.username
            email = Client.instance().params.email
        }

        if(username.isNotEmpty())
            userTxt.text = username.capitalize()
        if(email.isNotEmpty())
            emailTxt.text = email


        Client.instance().ClientListEvent += { runOnUiThread{ onContactList(it)} }
        Client.instance().OfferReceivedEvent += { runOnUiThread{ onInboundCall() }}

        call_btn.isEnabled = true
        call_btn.setOnClickListener{ runOnUiThread{ onOutboundCall() }}
        clientLst.setOnItemClickListener{ _, _, position, _ ->
            runOnUiThread{ onClientClick(position) }
        }
    }

    private fun onOutboundCall() {
        //Only switch if the client has a valid target to call
        if(Client.instance().hasTarget()){
            switchToNextView()
        }
    }

    private fun onInboundCall() {
        //Switch no matter what since this is the client requesting not the user
        switchToNextView()
    }

    private fun onClientClick(pos: Int) {
        var target = clients?.data?.get(pos)
            ?: throw Exception("[Contact] Could not parse client parameters from list.")
        if(Client.instance().setTarget(target)) {
            call_btn.isEnabled = true
        }
    }

    private fun onContactList(clients: Array<ClientParameters> ) {
        this.clients = ClientAdapter(this, R.layout.client_listview_item_row, clients)
        clientLst.adapter = this.clients
    }

    private fun switchToNextView() {
        var callIntent = Intent(this, CallActivity::class.java)
        startActivity(callIntent)
    }
}
