package com.yourssu.soomsil.usaint

import com.yourssu.soomsil.usaint.proto.StudentInformation
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun proto_test() {
        val stu = StudentInformation.newBuilder()
            .build()
        assert(stu.name == "")

        val stu2 = StudentInformation.newBuilder()
            .setName("abc").build()
        assert(stu2.name == "abc")

        val stu3 = StudentInformation.newBuilder(stu2).build()
        assert(stu3.name == "abc")

        val stu4 = StudentInformation.newBuilder(stu2).setName("aaaa").build()
        assert(stu4.name == "aaaa")
    }
}