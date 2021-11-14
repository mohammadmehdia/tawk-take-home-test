package to.tawk.tawktotestapp.config

import android.app.Application
import androidx.room.AutoMigration
import androidx.room.Room
import androidx.room.migration.AutoMigrationSpec
import to.tawk.tawktotestapp.helper.Utils
import to.tawk.tawktotestapp.helper.UtilsLiveData
import to.tawk.tawktotestapp.model.room.AppDatabase
import to.tawk.tawktotestapp.retrofit.IRetrofitApiService
import to.tawk.tawktotestapp.retrofit.RetrofitApiClient
import to.tawk.tawktotestapp.session.SessionManager

class App : Application() {
    companion object {
        lateinit var apiService: IRetrofitApiService
        lateinit var db: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        // Initial Session Manager (Shared Preferences)
        SessionManager.init(this)
        // Instantiate Room Database
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "github-db"
        ).build()
        // Create Retrofit Api Client Service
        apiService = RetrofitApiClient.create(this)
        // inititalize Intent Status
        UtilsLiveData.internetConnectionStatus.postValue(Utils.isNetworkConnected(this))
    }


}