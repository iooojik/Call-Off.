package iooojik.ru.calloff.ui.whiteList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import iooojik.ru.calloff.R
import iooojik.ru.calloff.localData.AppDatabase
import iooojik.ru.calloff.localData.whiteList.WhiteListDao
import iooojik.ru.calloff.localData.whiteList.WhiteListModel
import kotlinx.android.synthetic.main.fragment_white_list.*


class WhiteList : Fragment(), View.OnClickListener {

    private lateinit var rootView: View
    private lateinit var database : AppDatabase
    private lateinit var whiteListDao: WhiteListDao
    private lateinit var adapter: WhiteListAdapter
    private lateinit var whiteList : MutableList<WhiteListModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_white_list, container, false)
        return rootView
    }

    override fun onStart() {
        super.onStart()
        //получаем текущую базу данных
        database = AppDatabase.getAppDataBase(requireContext())!!
        whiteListDao = database.whiteListDao()
        //показываем фаб
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)
        fab.show()
        fab.setOnClickListener(this)
        //загружаем и показываем информацию о контактах
        loadInformation()
    }

    private fun loadInformation() {
        //получаем список контактов и ставим адаптер на recView
        whiteList = whiteListDao.getAll() as MutableList
        val warningText = rootView.findViewById<TextView>(R.id.textWarning)
        if (whiteList.size > 0) {
            warningText.visibility = View.GONE
            whiteList.sortBy { it.name }
            val recView = rootView.findViewById<RecyclerView>(R.id.rec_view_white_list)
            recView.layoutManager = LinearLayoutManager(context)
            adapter = WhiteListAdapter(
                requireContext(),
                requireActivity().layoutInflater,
                whiteList,
                requireActivity()
            )
            recView.adapter = adapter
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.fab -> {
                //элементы нижнего меню
                val bottomSheetDialog = BottomSheetDialog(requireContext())
                val bottomView : View = requireActivity().layoutInflater.inflate(R.layout.bottom_sheet_white_list, null)
                val nameField = bottomView.findViewById<EditText>(R.id.nameField)
                val phoneField = bottomView.findViewById<EditText>(R.id.phoneField)
                val addButton = bottomView.findViewById<Button>(R.id.addButton)

                addButton.setOnClickListener{
                    //слушатель на кнопку "добавить"
                    //если поля не пустые, то добавляем контакт в бд
                    if (nameField.text.toString().isNotEmpty() && phoneField.text.isNotEmpty()){
                        val model = WhiteListModel(null, nameField.text.toString(),
                            phoneField.text.toString(), false)
                        whiteListDao.insert(model)
                        whiteList.add(model)
                        adapter.notifyDataSetChanged()
                        val warningText = rootView.findViewById<TextView>(R.id.textWarning)
                        warningText.visibility = View.GONE
                        Toast.makeText(requireContext(), "Добавлено", Toast.LENGTH_SHORT).show()
                        bottomSheetDialog.hide()
                    } else {
                        Toast.makeText(requireContext(), "Не все поля заполнены", Toast.LENGTH_SHORT).show()
                    }
                }
                bottomSheetDialog.setContentView(bottomView)
                bottomSheetDialog.show()
            }
        }
    }
}