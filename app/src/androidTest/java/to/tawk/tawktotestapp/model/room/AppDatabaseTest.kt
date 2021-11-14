package to.tawk.tawktotestapp.model.room

import android.app.Application
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


@RunWith(AndroidJUnit4::class)
class AppDatabaseTest : TestCase() {

    private lateinit var db: AppDatabase

    @Before
    public override fun setUp() {
        val context: Application = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
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
    fun doCrudOperation() = runBlocking {
        // Check Single Insert
        val obj = createTestObject(1L)
        val result = db.githubUserDao().insertUser(obj)
        assert(result == 1L)
    }




}