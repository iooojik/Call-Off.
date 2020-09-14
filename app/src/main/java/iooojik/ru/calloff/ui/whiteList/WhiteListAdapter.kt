package iooojik.ru.calloff.ui.whiteList

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import iooojik.ru.calloff.R
import iooojik.ru.calloff.localData.AppDatabase
import iooojik.ru.calloff.localData.whiteList.WhiteListDao
import iooojik.ru.calloff.localData.whiteList.WhiteListModel

class WhiteListAdapter(private val context: Context, private val inflater: LayoutInflater,
                       private var whiteList: MutableList<WhiteListModel>,
                       private val activity: Activity
)
    : RecyclerView.Adapter<WhiteListAdapter.ViewHolder>()  {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.recycler_view_contact_item, parent, false))
    }

    override fun getItemCount(): Int {
        return whiteList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val database : AppDatabase = AppDatabase.getAppDataBase(context)!!
        val whiteListDao : WhiteListDao = database.whiteListDao()

        val model = whiteList[position]
        holder.contactName.text = model.name.toString()
        holder.contactPhoneNumber.text = model.firstPhoneNumber.toString()
        holder.callDate.visibility = View.GONE

        if (model.secondPhoneNumber != "null")
            holder.contactPhoneNumber.text = "${model.firstPhoneNumber}\n${model.secondPhoneNumber}"

        val cornerSize = activity.resources.getDimension(R.dimen.medium_components_dimen)
        val customButtonShapeBuilder = ShapeAppearanceModel.Builder()
        customButtonShapeBuilder.setAllCorners(CornerFamily.CUT, cornerSize)
        val materialShapeDrawable = MaterialShapeDrawable(customButtonShapeBuilder.build())

        if (model.firstPhoneNumber == "null" && model.secondPhoneNumber == "null"){
            materialShapeDrawable.fillColor = ContextCompat.getColorStateList(
                context,
                R.color.colorWarning
            )
        } else {
            materialShapeDrawable.fillColor = ContextCompat.getColorStateList(
                context,
                R.color.chainItem
            )
        }

        holder.callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + model.firstPhoneNumber))
            activity.startActivity(intent)
        }

        holder.itemView.background = materialShapeDrawable


        holder.itemView.setOnLongClickListener {
            //открываем нижнее меню для удаления элемента
            val bottomSheetDialog = BottomSheetDialog(context)
            val bottomView : View = inflater.inflate(
                R.layout.bottom_sheet_delete,
                null
            )
            val deleteButton : Button = bottomView.findViewById(R.id.delete_button)
            deleteButton.setOnClickListener {
                whiteListDao.delete(model)
                whiteList.remove(model)
                notifyDataSetChanged()
                bottomSheetDialog.hide()
            }
            val cancelButton : Button = bottomView.findViewById(R.id.cancel_button)
            cancelButton.setOnClickListener {
                bottomSheetDialog.hide()
            }
            bottomSheetDialog.setContentView(bottomView)
            bottomSheetDialog.show()
            return@setOnLongClickListener true
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val contactName : TextView = itemView.findViewById(R.id.contact_name)
        val contactPhoneNumber: TextView = itemView.findViewById(R.id.contact_phone_number)
        val callDate : TextView = itemView.findViewById(R.id.call_time)
        val callButton : ImageView = itemView.findViewById(R.id.call_button)
    }
}