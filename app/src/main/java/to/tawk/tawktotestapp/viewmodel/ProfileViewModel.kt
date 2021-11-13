package to.tawk.tawktotestapp.viewmodel

import android.app.Application
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import to.tawk.tawktotestapp.config.App
import to.tawk.tawktotestapp.helper.SingleLiveEvent
import to.tawk.tawktotestapp.model.DbHelper
import to.tawk.tawktotestapp.model.GithubUser

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "ProfileViewModel"
        const val ACTION_BACK: Int = 401
        const val ACTION_SAVED: Int = 402
    }

    private val disposable: CompositeDisposable = CompositeDisposable()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val actionEvent: SingleLiveEvent<Int> = SingleLiveEvent()
    val githubUser: MutableLiveData<GithubUser> = MutableLiveData()
    val note: MutableLiveData<String> = MutableLiveData()



    fun fetch(userId: Long) {
        disposable.add(
            App.db.githubUserDao().getUserById(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoading.postValue(true) }
                .doAfterTerminate { isLoading.postValue(false) }
                .subscribe ( { u ->
                    setUser(u)
                    fetchFromApi()
                }, {
                   actionBack()
                } )
        )
    }

    private fun setUser(user: GithubUser) {
        githubUser.value = user
        note.value = user.note
    }

    private fun fetchFromApi() {
        // load user info from api if not loaded before
        // checking by field {name} in model
        if(TextUtils.isEmpty(githubUser.value?.name)) {
            githubUser.value?.url?.let { url ->
                disposable.add(
                    App.apiService.getUserInfoFromApiUrl(url)
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
                )
            }
        }
    }

    // insert new data in local database
    private fun updateLocalRecord(user: GithubUser) {
        disposable.add(
            App.db.githubUserDao().updateUser(user)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({
                    DbHelper.updatedRecordId.postValue(user.id)
                }, {
                    it.message?.let { msg -> Log.e(TAG, msg) }
                })
        )
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
            disposable.add( App.db.githubUserDao().updateUser(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {_ ->
                    DbHelper.updatedRecordId.postValue(it.id)
                    actionEvent.value = ACTION_SAVED
                }
            )
        }
    }

}