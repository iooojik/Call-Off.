package iooojik.ru.calloff.localData.callLog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class CallLogModel(
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
    var isMyContact : Boolean? = false,
    @ColumnInfo(name = "call_time")
    val time : String,
    @ColumnInfo(name = "call_info")
    val callInfo : String
) {
}