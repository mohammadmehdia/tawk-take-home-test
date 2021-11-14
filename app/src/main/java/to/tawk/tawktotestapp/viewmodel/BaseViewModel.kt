package to.tawk.tawktotestapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import to.tawk.tawktotestapp.helper.SingleLiveEvent

open class BaseViewModel(application: Application) : AndroidViewModel(application){

    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    // used for loading indicators
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    // used for user actions (BACK, SAVED, ...) -> send to observers (view)
    val actionEvent: SingleLiveEvent<Int> = SingleLiveEvent()

    fun clear () {
        compositeDisposable.dispose()
        compositeDisposable.clear()
    }


}