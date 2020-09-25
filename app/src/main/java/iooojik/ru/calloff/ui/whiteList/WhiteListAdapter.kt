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
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import iooojik.ru.calloff.R
import iooojik.ru.calloff.StaticVars
import iooojik.ru.calloff.localData.AppDatabase
import iooojik.ru.calloff.localData.callLog.CallLogModel
import iooojik.ru.calloff.localData.whiteList.WhiteListDao
import iooojik.ru.calloff.localData.whiteList.WhiteListModel
import java.lang.StringBuilder

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

        holder.contactPhoneNumber.text = getNumbers(model.phoneNumbers)
        holder.callDate.visibility = View.GONE

        val cornerSize = activity.resources.getDimension(R.dimen.medium_components_dimen)
        val customButtonShapeBuilder = ShapeAppearanceModel.Builder()
        customButtonShapeBuilder.setAllCorners(CornerFamily.CUT, cornerSize)
        val materialShapeDrawable = MaterialShapeDrawable(customButtonShapeBuilder.build())

        materialShapeDrawable.fillColor = ContextCompat.getColorStateList(
            context,
            R.color.chainItem
        )

        holder.itemView.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(context)
            val bottomView : View = inflater.inflate(R.layout.bottom_sheet_contact_info, null)
            val contactName = bottomView.findViewById<TextView>(R.id.contact_name)
            val phonesGroup = bottomView.findViewById<ChipGroup>(R.id.phones_chip_group)
            val recViewCallLog = bottomView.findViewById<RecyclerView>(R.id.rec_view_call_log)

            contactName.text = model.name
            val numbers = model.phoneNumbers.split(StaticVars().regex)
            for (num in numbers) {
                if (!num.isNullOrBlank()) {
                    //добавляем номера телефона
                    val chip = Chip(context)
                    chip.text = num
                    chip.setOnClickListener {
                        MaterialAlertDialogBuilder(
                            context,
                            R.style.ThemeOverlay_App_MaterialAlertDialog
                        )
                            .setTitle("Позвонить абоненту ${model.name}, используя телефон $num?")
                            .setPositiveButton("Да") { dialog, _ ->
                                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$num"))
                                activity.startActivity(intent)
                                dialog.cancel()
                            }
                            .setNegativeButton("Нет") { dialog, _ ->
                                dialog.cancel()
                            }
                            .show()

                    }
                    phonesGroup.addView(chip)
                }

                //var callLogs = mutableListOf<CallLogModel>()
                //callLogs = callLogDao.findByFirstPhoneNum(model.firstPhoneNumber) as MutableList<CallLogModel>


                bottomSheetDialog.setContentView(bottomView)
                bottomSheetDialog.show()
            }
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

    private fun getNumbers(nums : String): String {
        val builder = StringBuilder()
        val numsList = nums.split(StaticVars().regex)
        for (n in numsList)
            builder.append("$n      ")
        return builder.toString()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val contactName : TextView = itemView.findViewById(R.id.contact_name)
        val contactPhoneNumber: TextView = itemView.findViewById(R.id.contact_phone_number)
        val callDate : TextView = itemView.findViewById(R.id.call_time)
    }
}