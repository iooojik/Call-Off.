package iooojik.ru.phoneblocker.localData.blackList

import androidx.room.*

@Dao
interface BlackListDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(blackListModel: BlackListModel)

    @Update
    fun update(blackListModel: BlackListModel)

    @Delete
    fun delete(blackListModel: BlackListModel)

    @Query("SELECT * FROM blacklistmodel")
    fun getAll() : List<BlackListModel>

    @Query("SELECT * FROM blacklistmodel WHERE _id = :id")
    fun getById(id : Long) : BlackListModel

    @Query("SELECT * FROM blacklistmodel ORDER BY _id DESC LIMIT 1")
    fun getLastTask() : BlackListModel

}