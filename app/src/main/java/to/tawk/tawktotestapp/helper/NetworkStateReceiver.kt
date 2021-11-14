package to.tawk.tawktotestapp.helper

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NetworkStateReceiver: BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        UtilsLiveData.internetConnectionStatus.postValue(Utils.isNetworkConnected(context))
    }


}