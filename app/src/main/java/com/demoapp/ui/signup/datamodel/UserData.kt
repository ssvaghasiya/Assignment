package com.demoapp.ui.signup.datamodel

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class UserData {
    @PrimaryKey
    @NonNull
    var id: String

    @NonNull
    @ColumnInfo(name = "fname")
    var fname: String? = null

    @NonNull
    @ColumnInfo(name = "lname")
    var lname: String? = null

    @NonNull
    @ColumnInfo(name = "email")
    var email: String? = null

    @NonNull
    @ColumnInfo(name = "dob")
    var dob: String? = null

    @NonNull
    @ColumnInfo(name = "username")
    var username: String? = null

    @NonNull
    @ColumnInfo(name = "password")
    var password: String? = null

    @NonNull
    @ColumnInfo(name = "securityQuestion")
    var securityQuestion: String? = null

    @NonNull
    @ColumnInfo(name = "securityAnswer")
    var securityAnswer: String? = null

    @NonNull
    @ColumnInfo(name = "address")
    var address: String? = null

    @NonNull
    @ColumnInfo(name = "profilePic", typeAffinity = ColumnInfo.BLOB)
    var profilePic: ByteArray? = null

    constructor(
        id: String,
        fname: String?,
        lname: String?,
        email: String?,
        dob: String?,
        username: String?,
        password: String?,
        securityQuestion: String?,
        securityAnswer: String?,
        address: String?,
        profilePic: ByteArray?
    ) {
        this.id = id
        this.fname = fname
        this.lname = lname
        this.email = email
        this.dob = dob
        this.username = username
        this.password = password
        this.securityQuestion = securityQuestion
        this.securityAnswer = securityAnswer
        this.address = address
        this.profilePic = profilePic
    }

}
