package to.tawk.tawktotestapp.viewmodel

import android.app.Application
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import to.tawk.tawktotestapp.config.App
import to.tawk.tawktotestapp.config.Constants
import to.tawk.tawktotestapp.helper.SingleLiveEvent
import to.tawk.tawktotestapp.helper.UtilsLiveData
import to.tawk.tawktotestapp.model.GithubUser
import to.tawk.tawktotestapp.session.SessionManager
import java.util.concurrent.TimeUnit

class MainViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        private const val TAG = "MainViewModel"
        const val ACTION_RESET_LIST: Int = 301
    }

    val dataReceived: MutableLiveData<List<GithubUser>> = MutableLiveData()
    val isNightMode: MutableLiveData<Boolean> = MutableLiveData(SessionManager.isNightMode());
    val searchKeyword: MutableLiveData<String> = MutableLiveData()
    val searchResult: MutableLiveData<List<GithubUser>> = MutableLiveData()

    private var pendingSinceParam = -1L
    private var since: Long = 0

    fun fetch(pageNo: Int = 0) {
        val sinceParam = if(pageNo<=0) 0 else since
        // Try to load from local databae, on failure or empty data, load from api
        compositeDisposable.add(
            App.db.githubUserDao().loadUsersSince(sinceParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    isLoading.postValue(true)
                }
                .doAfterTerminate {
                    isLoading.postValue(false)
                }
                .subscribe ( {
                    if(it.isNullOrEmpty()) {
                        // There is no more data in local database, try to load from api
                        fetchFromApi(sinceParam)
                    } else {
                        Log.d(TAG, "fetch: data loaded from local database")
                        if(since <= 0)  {
                            resetList()
                        }
                        this.since = if(it.isNullOrEmpty()) 0 else it[it.size-1].id
                        dataReceived.value = it
                    }
                } ,{
                    Log.e(TAG, it.message ?: "")
                    fetchFromApi(sinceParam)
                } )
        )

    }

    // load data from api
    private fun fetchFromApi(_since: Long = 0) {
        if(UtilsLiveData.internetConnectionStatus.value == true) {
            // Is Online -> perform api search
            App.apiService.getUsers(_since)
                .subscribeOn(Schedulers.io())
                .delay(Constants.NETWORK_DELAY, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    isLoading.postValue(true)
                }
                .doAfterTerminate {
                    isLoading.postValue(false)
                }
                .subscribe ( {
                    if(since <= 0)  {
                        resetList()
                    }
                    // store the id of last received item into :since
                    this.since = if(it.isNullOrEmpty()) 0 else it[it.size-1].id
                    // store recevied items to local database, to use in next load time
                    storeUsersListToDatabase(it)
                    dataReceived.value = it
                } ,{
                    Log.d(TAG, it.message ?: "")
                } )
                .addTo(compositeDisposable)
            pendingSinceParam = -1
        } else {
            // No Internet Connection -> save as perform disposable
            pendingSinceParam = _since
        }
    }

    // fetch data from the first (since = 0)
    fun refresh() {
        fetch(0)
    }

    // Reset The :since parameter to reload data, and fire ACTION_RESET_LIST event to the view
    private fun resetList() {
        actionEvent.value = ACTION_RESET_LIST
        since = 0
    }

    // Triggers the event to toggle night mode value
    fun toggleNightMode() {
        val night = isNightMode.value ?: false
        AppCompatDelegate.setDefaultNightMode( if(night) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES )
        isNightMode.value = !night
        SessionManager.setNightMode(!night)
    }

    // Store Received Items To Local Database
    private fun storeUsersListToDatabase(list: List<GithubUser>) {
        if(!list.isNullOrEmpty()) {
            compositeDisposable.add(
                Observable.fromCallable {
                    App.db.githubUserDao().insertUsers(list)
                }
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(
                        {
                            Log.d(TAG, "Saved Users To Database -> ${it.size}")
                        }, {
                            Log.e(TAG, "Error On Database Insertion: ${it.message}")
                        })
            )
        }
    }

    fun sendPendingRequest() {
        if(pendingSinceParam >= 0)
            fetchFromApi(pendingSinceParam)
    }

    // perform search
    fun performSearch(keyword: String?) {
        if(TextUtils.isEmpty(keyword)) {
            searchResult.value = null
            resetList()
            fetch(0)
        } else {
            App.db.githubUserDao().search("%${keyword}%")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                           searchResult.value = it
                }, {
                    searchResult.value = null
                })
                .addTo(compositeDisposable)
        }
    }


}