package com.demoapp.datasource

import com.demoapp.network.APIinterface

class UserRepository(apiInterface: APIinterface) {

    private var apiInterface: APIinterface? = apiInterface

//    fun login(loginDataModel: LoginDataModel): MutableLiveData<LoginData> {
//        val data = MutableLiveData<LoginData>()
//        val call = apiInterface!!.login(loginDataModel)
//        Debug.e("API", call.request().url.encodedPath)
//        Debug.e("body", Gson().toJson(loginDataModel))
//        call.enqueue(object : Callback<LoginData> {
//            override fun onFailure(call: Call<LoginData>, t: Throwable) {
//                data.value = LoginData(Constant.getFailureCode())
//                Debug.e("login isFailed", t.message)
//            }
//
//            override fun onResponse(call: Call<LoginData>, response: Response<LoginData>) {
//                val loginStatus = response.code()
//                try {
//                    if (response.isSuccessful) {
//                        val loginData = response.body()
//                        loginData!!.statusCode = loginStatus
//                        data.value = loginData
//                        Debug.e("login isSuccessful", Gson().toJson(loginData))
//                    } else {
//                        try {
//                            val inputAsString =
//                                response.errorBody()!!.source().inputStream().bufferedReader()
//                                    .use { it.readText() }
//                            Debug.e("Input", inputAsString)
//                            val sb = StringBuilder()
//                            sb.append(inputAsString)
//                            val loginData = Gson().fromJson<LoginData>(
//                                JSONObject(inputAsString).toString(),
//                                object : TypeToken<LoginData>() {}.type
//                            )
//                            loginData.statusCode = loginStatus
//                            data.value = loginData
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                            data.value = LoginData(loginStatus)
//                        }
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    data.value = LoginData(loginStatus)
//                }
//            }
//        })
//        return data
//    }

}