package com.demoapp.ui.signup.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.demoapp.ui.signup.datamodel.UserData

@Dao
interface UserDao {

    @Insert
    fun insert(user: UserData?)

    @Query("SELECT * FROM users")
    fun getAllUsers(): LiveData<List<UserData>>?

    @Query("SELECT * FROM users WHERE id=:userId")
    fun getUserData(userId: String?): LiveData<UserData>?

    @Update
    fun update(user: UserData?)

    @Delete
    fun delete(user: UserData?): Int
}