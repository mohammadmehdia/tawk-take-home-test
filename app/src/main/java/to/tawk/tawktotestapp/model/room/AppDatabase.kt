package to.tawk.tawktotestapp.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import to.tawk.tawktotestapp.model.GithubUser

@Database(entities = [GithubUser::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun githubUserDao(): GithubUserDao
}
