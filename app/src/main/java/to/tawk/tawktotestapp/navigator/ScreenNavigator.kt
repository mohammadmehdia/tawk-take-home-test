package to.tawk.tawktotestapp.navigator

import android.content.Context
import android.content.Intent
import android.util.Log
import to.tawk.tawktotestapp.model.GithubUser
import to.tawk.tawktotestapp.view.ProfileActivity

 class ScreenNavigator {

    companion object {
        private const val TAG = "ScreenNavigator"
        fun navigateToUserProfileScreen (context: Context?, userId: Long) {
            Log.d(TAG, "navigateToUserProfileScreen: $userId")
            context?.startActivity(Intent(context, ProfileActivity::class.java).putExtra(ProfileActivity.KEY_USER_ID, userId))
        }
    }

}