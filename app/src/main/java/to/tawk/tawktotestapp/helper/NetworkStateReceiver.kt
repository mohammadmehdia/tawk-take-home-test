package to.tawk.tawktotestapp.helper

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NetworkStateReceiver: BroadcastReceiver() {

    companion object {
        const val ACTION  = "android.net.conn.CONNECTIVITY_CHANGE"
    }


    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        UtilsLiveData.internetConnectionStatus.postValue(Utils.isNetworkConnected(context))
    }


}