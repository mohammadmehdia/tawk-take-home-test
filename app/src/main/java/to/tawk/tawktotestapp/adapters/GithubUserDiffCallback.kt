package to.tawk.tawktotestapp.adapters

import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil
import to.tawk.tawktotestapp.model.GithubUser

class GithubUserDiffCallback : DiffUtil.ItemCallback<GithubUser>() {

    override fun areItemsTheSame(oldItem: GithubUser, newItem: GithubUser): Boolean = ( oldItem.id == newItem.id )

    override fun areContentsTheSame(oldItem: GithubUser, newItem: GithubUser): Boolean = (
                TextUtils.equals(oldItem.login, newItem.login) &&
                        TextUtils.equals(oldItem.name, newItem.name) &&
                        TextUtils.equals(oldItem.avatarUrl, newItem.avatarUrl) &&
                        TextUtils.isEmpty(oldItem.note) == TextUtils.isEmpty(newItem.note)
            )

}