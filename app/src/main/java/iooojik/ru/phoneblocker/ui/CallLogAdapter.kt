package iooojik.ru.phoneblocker.ui

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import iooojik.ru.phoneblocker.R
import iooojik.ru.phoneblocker.localData.callLog.CallLogModel

class CallLogAdapter(private val context: Context, private val inflater : LayoutInflater,
                     private var callLogs : MutableList<CallLogModel>,
                     private val activity: Activity)
    : RecyclerView.Adapter<CallLogAdapter.ViewHolder>()  {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.recycler_view_contact_item, parent, false))
    }

    override fun getItemCount(): Int {
        return callLogs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = callLogs[position]
        holder.contactName.text = model.name.toString()
        holder.contactPhoneNumber.text = model.phoneNumber.toString()
        holder.callDate.text = model.time.toString()

        val cornerSize = activity.resources.getDimension(R.dimen.medium_components_dimen)
        val customButtonShapeBuilder = ShapeAppearanceModel.Builder()
        customButtonShapeBuilder.setAllCorners(CornerFamily.CUT, cornerSize)
        val materialShapeDrawable = MaterialShapeDrawable(customButtonShapeBuilder.build())
        if (model.isMyContact!!)
            materialShapeDrawable.fillColor = ContextCompat.getColorStateList(context, R.color.colorAcceptable)
        else
            materialShapeDrawable.fillColor = ContextCompat.getColorStateList(context, R.color.chainItem)

        holder.itemView.background = materialShapeDrawable

    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val contactName : TextView = itemView.findViewById(R.id.contact_name)
        val contactPhoneNumber: TextView = itemView.findViewById(R.id.contact_phone_number)
        val callDate : TextView = itemView.findViewById(R.id.call_time)
    }
}