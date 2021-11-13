package to.tawk.tawktotestapp.session

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    lateinit var prefs: SharedPreferences
    fun init(context: Context) {
        prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
    }

    fun isNightMode() = prefs.getBoolean("night_mode", false)

    fun setNightMode(nightMode: Boolean) = prefs.edit().putBoolean("night_mode", nightMode).apply()

}