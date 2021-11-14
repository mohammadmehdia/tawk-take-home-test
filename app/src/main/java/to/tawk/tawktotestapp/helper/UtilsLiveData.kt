package to.tawk.tawktotestapp.helper

import androidx.lifecycle.MutableLiveData
import to.tawk.tawktotestapp.helper.SingleLiveEvent

object UtilsLiveData {

    val updatedRecordId: MutableLiveData<Long> = MutableLiveData();
    val internetConnectionStatus: MutableLiveData<Boolean> = MutableLiveData(true)

}