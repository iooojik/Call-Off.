package iooojik.ru.phoneblocker.localData.whiteList

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class WhiteListModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id : Long? = null,
    @ColumnInfo(name = "name")
    val name : String,
    @ColumnInfo(name = "phone_number")
    val phoneNumber : String,
    @ColumnInfo(name = "is_my_contact")
    val isMyContact : Boolean? = false
) {

}