package somethingspecific.face2face.models

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import somethingspecific.face2face.R
import somethingspecific.face2face.coms.ClientParameters

class ClientAdapter(internal var context: Context, internal var layoutResourceId: Int, data: Array<ClientParameters>) :
    ArrayAdapter<ClientParameters>(context, layoutResourceId, data) {

    public var data: Array<ClientParameters>? = null

    init {
        this.data = data
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var row = convertView
        var holder: ClientHolder? = null

        if (row == null) {
            val inflater = (context as Activity).layoutInflater
            row = inflater.inflate(layoutResourceId, parent, false)


            holder = ClientHolder()
            holder.avatarIco = row.findViewById(R.id.avatarIco) as ImageView
            holder.userTxt = row.findViewById(R.id.userTxt)
            holder.emailTxt = row.findViewById(R.id.emailTxt)
            holder.statusTxt = row.findViewById(R.id.statusTxt)

            row.tag = holder
        } else {
            holder = row.tag as ClientHolder
        }

        val client = data!![position]
        holder.userTxt!!.text = client.username
        holder.emailTxt!!.text = client.email
        holder.statusTxt!!.text = client.status

//TODO: https://github.com/jkwiecien/EasyImage
        holder.avatarIco!!.setImageResource(R.drawable.me)

        return row
    }

    public class ClientHolder {
        var avatarIco: ImageView? = null
        var userTxt: TextView? = null
        var emailTxt: TextView? = null
        var statusTxt: TextView? = null

    }
}