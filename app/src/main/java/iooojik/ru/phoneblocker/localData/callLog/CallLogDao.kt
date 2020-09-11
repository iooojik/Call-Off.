package iooojik.ru.phoneblocker.localData.callLog

import androidx.room.*

@Dao
interface CallLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(callLogModel: CallLogModel)

    @Update
    fun update(callLogModel: CallLogModel)

    @Delete
    fun delete(callLogDao: CallLogModel)

    @Query("SELECT * FROM calllogmodel")
    fun getAll() : List<CallLogModel>

    @Query("SELECT * FROM calllogmodel WHERE _id = :id")
    fun getById(id : Long) : CallLogModel

    @Query("SELECT * FROM calllogmodel ORDER BY _id DESC LIMIT 1")
    fun getLastTask() : CallLogModel

}