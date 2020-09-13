package iooojik.ru.calloff.localData.blackList

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class BlackListModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id : Long? = null,
    @ColumnInfo(name = "caller_name")
    val callerName : String,
    @ColumnInfo(name = "phone_number")
    val phoneNumber : String
) {

}