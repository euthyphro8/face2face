package somethingspecific.face2face.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_contact.*
import somethingspecific.face2face.coms.ClientParameters
import somethingspecific.face2face.R
import somethingspecific.face2face.coms.Client
import somethingspecific.face2face.models.ClientAdapter




class ContactActivity : AppCompatActivity() {

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
        Client.instance().ClientListEvent += { onContactList(it) }

        var clients = arrayOf(
            ClientParameters(username, email,
                "https://scontent-lax3-1.xx.fbcdn.net/v/t1.0-9/56169751_2328522497199217_8528485562888224768_n.jpg?_nc_cat=106&_nc_ht=scontent-lax3-1.xx&oh=af5534402e7907046a72134c1238281c&oe=5D4AF933",
                "Online")
        )

        var adapter = ClientAdapter(this, R.layout.client_listview_item_row, clients)
        clientLst.adapter = adapter

//        val header = layoutInflater.inflate(R.layout.client_listview_header_row, null) as View
//        clientLst.addHeaderView(header)




        call_btn.setOnClickListener {
            onCall()
        }
    }

    private fun onCall() {
//        switchToNextView()
    }

    private fun onContactList(clients: ArrayList<ClientParameters> ) {
//        val arrayAdapter = ArrayAdapter<String>(
//            this,
//            android.R.layout.simple_list_item_1,
//            clients
//        )
    }

    private fun switchToNextView() {
        var callIntent = Intent(this, CallActivity::class.java)
        startActivity(callIntent)
    }
}
