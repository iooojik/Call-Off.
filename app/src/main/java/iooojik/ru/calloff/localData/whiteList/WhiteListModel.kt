package iooojik.ru.calloff.localData.whiteList

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class WhiteListModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id : Long? = null,
    @ColumnInfo(name = "name")
    var name : String,
    @ColumnInfo(name = "first_phone_number")
    var firstPhoneNumber : String,
    @ColumnInfo(name = "second_phone_number")
    var secondPhoneNumber : String,
    @ColumnInfo(name = "is_my_contact")
    var isMyContact : Boolean? = false
) {

}