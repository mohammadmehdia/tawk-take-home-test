package to.tawk.tawktotestapp.viewmodel

import android.app.Application
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import to.tawk.tawktotestapp.config.App
import to.tawk.tawktotestapp.config.Constants
import to.tawk.tawktotestapp.extensions.retryExponential
import to.tawk.tawktotestapp.helper.UtilsLiveData
import to.tawk.tawktotestapp.model.GithubUser
import java.util.concurrent.TimeUnit

class ProfileViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        private const val TAG = "ProfileViewModel"
        const val ACTION_BACK: Int = 401
        const val ACTION_SAVED: Int = 402
    }

    val githubUser: MutableLiveData<GithubUser> = MutableLiveData()
    val note: MutableLiveData<String> = MutableLiveData()
    private var hasPendingRequest = false


    fun fetch(userId: Long) {
        App.db.githubUserDao().getUserById(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ( { u ->
                setUser(u)
                fetchFromApi()
            }, {
                Log.e(TAG, "fetch: error -> " + it.message, it)
                actionBack()
            } )
            .addTo(compositeDisposable)
    }

    private fun setUser(user: GithubUser) {
        githubUser.value = user
        note.value = user.note
    }

    private fun fetchFromApi() {
        // load user info from api if not loaded before
        // checking by field {name} in model
        if(TextUtils.isEmpty(githubUser.value?.name)) {
            // Check For Internet Connection Status
            if(UtilsLiveData.internetConnectionStatus.value == true) {
                hasPendingRequest = false
                githubUser.value?.url?.let { url ->
                    App.apiService.getUserInfoFromApiUrl(url)
                        .delay(Constants.NETWORK_DELAY, TimeUnit.SECONDS)   // Delay to see shimer
                        .retryExponential(Constants.MAX_RETRY_COUNT)        // Exponential BackOff Retry (max retry count: Constants.MAX_RETRY_COUNT)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe {
                            isLoading.postValue(true)
                        }
                        .doAfterTerminate {
                            isLoading.postValue(false)
                        }
                        .subscribe({ res ->
                            updateLocalRecord(res)
                            githubUser.value = res
                        }, {
                            Log.e(TAG, "Error : " + it.message)
                        })
                        .addTo(compositeDisposable)
                }
            } else {
                hasPendingRequest = true
            }

        }
    }

    // insert new data in local database
    private fun updateLocalRecord(user: GithubUser) {
        // put user note on model before persist on database
        note.value?.let { user.note = it }
        // update record to local database
        App.db.githubUserDao().updateUser(user)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                UtilsLiveData.updatedRecordId.postValue(user.id)
            }, {
                it.message?.let { msg -> Log.e(TAG, msg) }
            })
            .addTo(compositeDisposable)
    }

    // exit this screen
    fun actionBack() {
        actionEvent.value = ACTION_BACK
    }

    // save note to database
    fun saveNote() {
        Log.d(TAG, "saveNote: " + note.value)
        githubUser.value?.let {
            it.note = note.value
            compositeDisposable.add( App.db.githubUserDao().updateUser(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {_ ->
                    UtilsLiveData.updatedRecordId.postValue(it.id)
                    actionEvent.value = ACTION_SAVED
                }
            )
        }
    }

    fun sendPendingRequest() {
        if(hasPendingRequest)
            fetchFromApi()
    }

}