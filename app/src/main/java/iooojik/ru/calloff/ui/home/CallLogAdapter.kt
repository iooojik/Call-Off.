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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import iooojik.ru.calloff.R
import iooojik.ru.calloff.localData.AppDatabase
import iooojik.ru.calloff.localData.callLog.CallLogDao
import iooojik.ru.calloff.localData.callLog.CallLogModel
import org.w3c.dom.Text


class CallLogAdapter(
    private val context: Context, private val inflater: LayoutInflater,
    private var callLogs: MutableList<CallLogModel>,
    private val activity: Activity
)
    : RecyclerView.Adapter<CallLogAdapter.ViewHolder>()  {

    private lateinit var database : AppDatabase
    private lateinit var callLogDao: CallLogDao


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.recycler_view_contact_item, parent, false))
    }

    override fun getItemCount(): Int {
        return callLogs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        database = AppDatabase.getAppDataBase(context)!!
        callLogDao = database.callLogDao()
        val model = callLogs[position]

        holder.contactName.text = model.name.toString()
        holder.contactPhoneNumber.text = model.firstPhoneNumber
        holder.callDate.text = model.time.toString()

        val cornerSize = activity.resources.getDimension(R.dimen.medium_components_dimen)
        val customButtonShapeBuilder = ShapeAppearanceModel.Builder()
        customButtonShapeBuilder.setAllCorners(CornerFamily.CUT, cornerSize)
        val materialShapeDrawable = MaterialShapeDrawable(customButtonShapeBuilder.build())
        if (model.isMyContact!!)
            materialShapeDrawable.fillColor = ContextCompat.getColorStateList(
                context,
                R.color.colorAcceptable
            )
        else
            materialShapeDrawable.fillColor = ContextCompat.getColorStateList(
                context,
                R.color.chainItem
            )

        holder.callButton.visibility = View.GONE

        holder.itemView.background = materialShapeDrawable

        holder.itemView.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(context)
            val bottomView : View = inflater.inflate(R.layout.bottom_sheet_contact_info, null)
            val contactName = bottomView.findViewById<TextView>(R.id.contact_name)
            val phonesGroup = bottomView.findViewById<ChipGroup>(R.id.phones_chip_group)
            val recViewCallLog = bottomView.findViewById<RecyclerView>(R.id.rec_view_call_log)

            contactName.text = model.name

            //добавляем номера телефона
            if (model.firstPhoneNumber.toString() != "null"){
                val chip1 = Chip(context)
                chip1.text = model.firstPhoneNumber
                chip1.setOnClickListener {
                    MaterialAlertDialogBuilder(
                        context,
                        R.style.ThemeOverlay_App_MaterialAlertDialog
                    )
                        .setTitle("Позвонить абоненту ${model.name}?")
                        .setPositiveButton("Да"){ dialog, _ ->
                            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + model.firstPhoneNumber))
                            activity.startActivity(intent)
                            dialog.cancel()
                        }
                        .setNegativeButton("Нет"){ dialog, _ ->
                            dialog.cancel()
                        }
                        .show()

                }
                phonesGroup.addView(chip1)
            }

            if (model.secondPhoneNumber.toString() != "null"){
                val chip2 = Chip(context)
                chip2.text = model.secondPhoneNumber
                chip2.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked){
                        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + model.secondPhoneNumber))
                        activity.startActivity(intent)
                        chip2.isChecked = false
                    }
                }
                phonesGroup.addView(chip2)
            }

            var callLogs = mutableListOf<CallLogModel>()
            callLogs = callLogDao.findByFirstPhoneNum(model.firstPhoneNumber) as MutableList<CallLogModel>



            bottomSheetDialog.setContentView(bottomView)
            bottomSheetDialog.show()
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val contactName : TextView = itemView.findViewById(R.id.contact_name)
        val contactPhoneNumber: TextView = itemView.findViewById(R.id.contact_phone_number)
        val callDate : TextView = itemView.findViewById(R.id.call_time)
        val callButton : ImageView = itemView.findViewById(R.id.call_button)
    }
}