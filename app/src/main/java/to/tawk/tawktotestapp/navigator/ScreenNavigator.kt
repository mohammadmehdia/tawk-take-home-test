package to.tawk.tawktotestapp.navigator

import android.content.Context
import android.content.Intent
import to.tawk.tawktotestapp.model.GithubUser
import to.tawk.tawktotestapp.view.ProfileActivity

open class ScreenNavigator {

    companion object {
        fun navigateToUserProfileScreen (context: Context?, userId: Long) {
            context?.startActivity(Intent(context, ProfileActivity::class.java).putExtra(ProfileActivity.KEY_USER_ID, userId))
        }
    }


}