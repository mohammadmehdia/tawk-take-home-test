package to.tawk.tawktotestapp.retrofit

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import to.tawk.tawktotestapp.model.GithubUser

interface IRetrofitApiService {

    @GET("users")
    fun getUsers (@Query("since") since: Long): Single<List<GithubUser>>

    @GET
    fun getUserInfoFromApiUrl(@Url url: String) : Single<GithubUser>

}