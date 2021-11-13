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
    fun insertUsers(users: List<GithubUser>) : Single<Void>

    @Update
    fun updateUser(user: GithubUser) : Single<Int>

//    @Update(entity = GithubUser::class)
//    fun updateUserNote(obj: GithubUserNoteUpdate) : Single<Int>

    @Query("select * from users where id = :id")
    fun getUserById(id: Long): Single<GithubUser>

}