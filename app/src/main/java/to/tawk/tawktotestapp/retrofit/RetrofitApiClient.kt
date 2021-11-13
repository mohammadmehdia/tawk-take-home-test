package to.tawk.tawktotestapp.retrofit

import android.content.Context
import android.os.Environment
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import to.tawk.tawktotestapp.config.Constants

import okhttp3.logging.HttpLoggingInterceptor





class RetrofitApiClient {

    companion object {
        fun create (context: Context) : IRetrofitApiService {
            val cacheSize: Long = 15 * 1024 * 1024L // 15 MB
            val cache = Cache(context.cacheDir, cacheSize)

            val okHttpClient = OkHttpClient.Builder().addInterceptor(
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
            )
                .cache(cache)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(IRetrofitApiService::class.java)
        }
    }

}