package iooojik.ru.calloff.localData.callLog

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

    @Query("DELETE FROM calllogmodel")
    fun deleteAll()

    @Query("SELECT * FROM calllogmodel WHERE first_phone_number = :firstPhoneNumber")
    fun findByFirstPhoneNum(firstPhoneNumber : String) : List<CallLogModel>

    @Query("SELECT * FROM calllogmodel WHERE first_phone_number = :secondPhoneNumber")
    fun findBySecondPhoneNum(secondPhoneNumber : String) : List<CallLogModel>

}