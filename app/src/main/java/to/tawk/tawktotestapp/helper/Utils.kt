package to.tawk.tawktotestapp.helper

import android.content.Context
import android.hardware.input.InputManager
import android.inputmethodservice.InputMethodService
import android.net.ConnectivityManager
import android.renderscript.ScriptGroup
import android.view.View
import android.view.inputmethod.InputMethodManager

object Utils {

    fun hideSoftKeyboard(context: Context, view: View?) {
        view?.let { v ->
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    fun isNetworkConnected(context: Context?) : Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return cm?.activeNetworkInfo?.isConnected ?: false
    }


}