package com.demoapp.ui.signup.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.demoapp.ui.signup.dao.UserDao
import com.demoapp.ui.signup.datamodel.UserData

@Database(entities = arrayOf(UserData::class), version = 1)
abstract class UserRoomDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao?

    companion object {
        @Volatile
        private var userRoomInstance: UserRoomDatabase? = null
        fun getDatabase(context: Context): UserRoomDatabase? {
            if (userRoomInstance == null) {
                synchronized(UserRoomDatabase::class.java) {
                    if (userRoomInstance == null) {
                        userRoomInstance = Room.databaseBuilder(
                            context.applicationContext,
                            UserRoomDatabase::class.java, "signup_database"
                        )
                            .build()
                    }
                }
            }
            return userRoomInstance
        }
    }
}