package to.tawk.tawktotestapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import to.tawk.tawktotestapp.config.App
import to.tawk.tawktotestapp.helper.SingleLiveEvent
import to.tawk.tawktotestapp.model.GithubUser
import to.tawk.tawktotestapp.session.SessionManager

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "MainViewModel"
        const val ACTION_RESET_LIST: Int = 301
    }

    private val disposable: CompositeDisposable = CompositeDisposable()
    val actionEvent: SingleLiveEvent<Int> = SingleLiveEvent()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val dataReceived: MutableLiveData<List<GithubUser>> = MutableLiveData()
    val isNightMode: MutableLiveData<Boolean> = MutableLiveData(SessionManager.isNightMode());
    val searchKeyword: MutableLiveData<String> = MutableLiveData()
    val searchResult: MutableLiveData<List<GithubUser>> = MutableLiveData()

    private var since: Long = 0

    fun fetch(pageNo: Int = 0) {
        val sinceParam = if(pageNo<=0) 0 else since
        // Try to load from local databae, on failure or empty data, load from api
        disposable.add(
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
        disposable.add(
            App.apiService.getUsers(_since)
                .subscribeOn(Schedulers.io())
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
        )
    }

    // fetch data from the first (since = 0)
    fun refresh() {
        fetch(0)
    }

    // Reset The :since parameter to reload data, and fire ACTION_RESET_LIST event to the view
    private fun resetList() {
        since = 0
        actionEvent.value = ACTION_RESET_LIST
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
            disposable.add(
                Observable.fromCallable {
                    App.db.githubUserDao().insertUsers(list)
                }
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(
                        {
                            Log.d(TAG, "Saved Users To Database")
                        }, {
                            Log.e(TAG, "Error On Database Insertion: ${it.message}")
                        })
            )
        }
    }

    


}