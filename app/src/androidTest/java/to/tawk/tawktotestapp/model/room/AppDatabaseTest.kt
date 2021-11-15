package to.tawk.tawktotestapp.model.room

import android.app.Application
import androidx.room.EmptyResultSetException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import to.tawk.tawktotestapp.model.GithubUser
import java.io.IOException
import java.lang.Exception
import java.util.*


@RunWith(AndroidJUnit4::class)
class AppDatabaseTest : TestCase() {

    private lateinit var db: AppDatabase
    private lateinit var githubUserDao: GithubUserDao
    @Before
    public override fun setUp() {
        val context: Application = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        githubUserDao = db.githubUserDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private fun createTestObject(id: Long) = GithubUser(
        id = id,
        login = "user#$id",
        avatarUrl = "avatar#$id",
        url = "url#$id",
        blog = "blog#$id",
        company = "company$id",
        name = "name$id",
        location = "location$id",
        followers = id.toInt() * 1000,
        following = id.toInt() * 15,
        note = "note$id"
    )



    @Test
    fun doCrudOperation() {
        testSingleInsertionDeletion()
        testMultipleRecordInsertion()
    }

    private fun testClearData() {
        githubUserDao.truncate()
        val totalCount = githubUserDao.totalCount().blockingGet()
        assertTrue(totalCount == 0L)
    }

    private fun testSingleInsertionDeletion() {
        testClearData()

        val testId = 1L
        // Insert
        val obj = createTestObject(testId)
        val insertionId = githubUserDao.insertUser(obj)
        assertTrue(insertionId == testId)
        // Delete
        githubUserDao.delete(obj)
        try {
            val foundRecord = githubUserDao.getUserById(testId).blockingGet()
            assertTrue(foundRecord == null)
        } catch (e: Throwable) {
            assertTrue(e is EmptyResultSetException)
        }
        // clear all records
        githubUserDao.truncate()
        val totalCount = githubUserDao.totalCount().blockingGet()
        assertTrue(totalCount == 0L)
    }

    private fun testMultipleRecordInsertion() {
        val testIds = LongArray(50) { (it+1).toLong() } // 1..50
        val objectList = List(50) { createTestObject((it + 1).toLong()) }
        val insertedIds = githubUserDao.insertUsers(objectList)
        assertTrue(testIds.contentEquals(insertedIds))
        // read all users
        val resultList = githubUserDao.loadUsersSince(0, 1000).blockingGet()
        assertTrue(resultList != null && resultList.size == 50)
        resultList?.forEach {
            assert(testIds.contains(it.id))
        }
        // clear all records
        githubUserDao.truncate()
        val totalCount = githubUserDao.totalCount().blockingGet()
        assertTrue(totalCount == 0L)
    }


}