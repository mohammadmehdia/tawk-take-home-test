package to.tawk.tawktotestapp.model

import androidx.lifecycle.MutableLiveData
import to.tawk.tawktotestapp.helper.SingleLiveEvent

class DbHelper{

    companion object {
        val updatedRecordId: MutableLiveData<Long> = MutableLiveData();
    }


}