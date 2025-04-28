package com.yourssu.soomsil.usaint

import com.yourssu.soomsil.usaint.core.model.StudentCredential
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.RusaintApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class RusaintApiTest {
    private lateinit var rusaintApi: RusaintApi

    @Before
    fun init() {
        rusaintApi = RusaintApi()
    }

    @Test
    fun getUSaintSession(): Unit = runBlocking {
        rusaintApi.getUSaintSession(StudentCredential(id = "20222904", password = ""))
    }
}