package to.tawk.tawktotestapp.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
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

class GithubUserAdapter: RecyclerView.Adapter<GithubUserAdapter.ItemVH> () {
    companion object {
        private const val TAG = "GithubUserAdapter"
    }

    private var items : ArrayList<GithubUser> = ArrayList()

    private fun getItemAt(position: Int) : GithubUser? =
        if(items.isNullOrEmpty() || position < 0 || position >= items.size) null
        else items[position]


    fun addItems(list: List<GithubUser>?) {
        if(!list.isNullOrEmpty()) {
            val prevCount = itemCount
            items.addAll(list)
            val size = itemCount - prevCount
            if(size > 0) {
                notifyItemRangeInserted(prevCount, size)
            }
        }
    }

    fun clear() {
        val prevCount = itemCount
        items = ArrayList()
        if(prevCount > 0) {
            notifyItemRangeRemoved(0, prevCount)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVH =
        ItemVH( GithubUserListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false) )

    override fun onBindViewHolder(holder: ItemVH, position: Int) = holder.bind(getItemAt(position), position % 4 == 3)

    override fun getItemCount(): Int = if(items.isNullOrEmpty()) 0 else items.size

    @SuppressLint("CheckResult")
    fun onRecordUpdated(id: Long?) {
        Log.d(TAG, "onRecordUpdated: $id")
        id?.let {
            val index = items.indexOfFirst { e -> e.id == id }
            if(index >= 0) {
                Log.d(TAG, "onRecordUpdated: index -> $index")
                App.db.githubUserDao().getUserById(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { record ->
                        items[index] = record
                        notifyItemChanged(index)
                    }
            }
        }
    }

    inner class ItemVH(_binding: GithubUserListItemBinding) : RecyclerView.ViewHolder(_binding.root) {
        private val binding: GithubUserListItemBinding = _binding

        fun bind(user: GithubUser?, negative: Boolean) {
            binding.navigator = ScreenNavigator.Companion
            binding.negative = negative
            binding.user = user
        }


    }



}