package iooojik.ru.calloff.localData.whiteList

import androidx.room.*

@Dao
interface WhiteListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(whiteListModel: WhiteListModel)

    @Update
    fun update(whiteListModel: WhiteListModel)

    @Delete
    fun delete(whiteListModel: WhiteListModel)

    @Query("SELECT * FROM whitelistmodel")
    fun getAll() : List<WhiteListModel>

    @Query("SELECT * FROM whitelistmodel WHERE _id = :id")
    fun getById(id : Long) : WhiteListModel

    @Query("SELECT * FROM whitelistmodel ORDER BY _id DESC LIMIT 1")
    fun getLastTask() : WhiteListModel

    @Query("SELECT * FROM whitelistmodel WHERE is_my_contact = :state")
    fun getMyContacts(state : Boolean) : List<WhiteListModel>
}