package iooojik.ru.calloff.ui.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import iooojik.ru.calloff.R
import iooojik.ru.calloff.localData.callLog.CallLogModel

class ContactAdapter(private val context: Context, private val inflater: LayoutInflater,
                private var callLogs: MutableList<CallLogModel>,
                private val activity: Activity
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.recycler_view_contact_item, parent, false))
    }

    override fun getItemCount(): Int {
        return callLogs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = callLogs[position]

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

}