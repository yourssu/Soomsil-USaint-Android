package com.yourssu.soomsil.usaint

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yourssu.soomsil.usaint.core.model.StudentCredential
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentCredentialDataSource
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentCredentialSerializer
import com.yourssu.soomsil.usaint.proto.StudentCredentialProto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class StudentCredentialDataSourceTest {
    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher + Job())
    private val testContext = ApplicationProvider.getApplicationContext<Context>()
    private val testDataStore = DataStoreFactory.create(
        serializer = StudentCredentialSerializer,
        scope = testCoroutineScope,
        produceFile = { testContext.dataStoreFile("test_student_credential.pb") }
    )
    private val dataSource = StudentCredentialDataSource(testDataStore)

    @Before
    fun setup() {
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    @Test
    fun test() {
        testCoroutineScope.runBlockingTest {
            val credential = StudentCredential("test_id", "test_password")
            dataSource.setStudentCredential(credential)
            assert(dataSource.getStudentCredential() == credential)
            dataSource.setLoggedIn(true)
            assert(dataSource.getStudentCredential() == credential)
        }
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
        testCoroutineScope.runBlockingTest {
            testDataStore.updateData { StudentCredentialProto.getDefaultInstance() }
        }
        testCoroutineScope.cancel()
    }
}