package to.tawk.tawktotestapp.model.room

import androidx.room.*
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import to.tawk.tawktotestapp.model.GithubUser

@Dao
interface GithubUserDao {

    @Query("select * from users where id > :since limit :size")
    fun loadUsersSince(since: Long, size: Int = 30) : Single<List<GithubUser>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(user: GithubUser) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUsers(users: List<GithubUser>) : LongArray

    @Update
    fun updateUser(user: GithubUser) : Single<Int>

    @Query("select * from users where id = :id limit 1")
    fun getUserById(id: Long): Single<GithubUser>


    @Query("select count(*) from users")
    fun totalCount() : Single<Long>

    @Query("select * from users where login like :keyword or name  like :keyword or note like :keyword")
    fun search(keyword: String) : Single<List<GithubUser>>

    @Delete
    fun delete(user: GithubUser)

    @Query("delete from users where 1")
    fun truncate()

}
