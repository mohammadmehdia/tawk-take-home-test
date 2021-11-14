package to.tawk.tawktotestapp.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import to.tawk.tawktotestapp.config.App
import to.tawk.tawktotestapp.databinding.GithubUserListItemBinding
import to.tawk.tawktotestapp.model.GithubUser
import to.tawk.tawktotestapp.navigator.ScreenNavigator

class GithubUserAdapter() : ListAdapter<GithubUser, GithubUserAdapter.ItemVH>(GithubUserDiffCallback()) {

    companion object {
        private const val TAG = "GithubUserAdapter"
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int) : Long = getItem(position).id

    fun addItems(items: List<GithubUser>?) {
        if(!items.isNullOrEmpty()) {
            val newlist = currentList.toMutableList()
            newlist.addAll(items)
            submitList(newlist)
        }
    }

    private fun replaceItemAt(index: Int, user: GithubUser) {
        val newlist = currentList.toMutableList()
        newlist[index] = user
        submitList(newlist)
    }

    fun clear() {
        submitList(null)
    }

    @SuppressLint("CheckResult")
    fun onRecordUpdated(id: Long?) {
        // Update The list, if any item updated in local database
        Log.d(TAG, "onRecordUpdated: $id")

        id?.let {
            val index = currentList.indexOfFirst { e -> e.id == id }
            if(index >= 0) {
                Log.d(TAG, "onRecordUpdated: index -> $index")
                App.db.githubUserDao().getUserById(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { record -> replaceItemAt(index, record) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVH =
        ItemVH( GithubUserListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false) )

    override fun onBindViewHolder(holder: ItemVH, position: Int) = holder.bind(getItem(position), position % 4 == 3)

    inner class ItemVH(_binding: GithubUserListItemBinding) : RecyclerView.ViewHolder(_binding.root) {
        private val binding: GithubUserListItemBinding = _binding

        init {
            // handle click on item
            itemView.setOnClickListener { v ->
                ScreenNavigator.navigateToUserProfileScreen(v.context, getItem(layoutPosition).id)
            }
        }

        fun bind(user: GithubUser?, negative: Boolean) {
            binding.negative = negative
            binding.user = user
        }

    }




}