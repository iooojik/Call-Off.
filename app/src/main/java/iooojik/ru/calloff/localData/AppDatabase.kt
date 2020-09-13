package iooojik.ru.calloff.localData

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import iooojik.ru.calloff.localData.blackList.BlackListDao
import iooojik.ru.calloff.localData.blackList.BlackListModel
import iooojik.ru.calloff.localData.callLog.CallLogDao
import iooojik.ru.calloff.localData.callLog.CallLogModel
import iooojik.ru.calloff.localData.whiteList.WhiteListDao
import iooojik.ru.calloff.localData.whiteList.WhiteListModel


@Database(entities = [WhiteListModel::class, BlackListModel::class, CallLogModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blackListDao() : BlackListDao
    abstract fun whiteListDao() : WhiteListDao
    abstract fun callLogDao() : CallLogDao

    companion object {
        var INSTANCE: AppDatabase? = null

        fun getAppDataBase(context: Context): AppDatabase? {
            if (INSTANCE == null){
                synchronized(AppDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "database").allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}