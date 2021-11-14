package to.tawk.tawktotestapp.customview

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import to.tawk.tawktotestapp.adapters.GithubUserAdapter
import to.tawk.tawktotestapp.helper.EndlessRecyclerOnScrollListener
import to.tawk.tawktotestapp.helper.UtilsLiveData
import java.lang.ref.WeakReference


class GithubUserRecyclerView(context: Context, attrs: AttributeSet? = null) :
    RecyclerView(context, attrs) {
    val pageNoRequested: MutableLiveData<Int> = MutableLiveData()
    val scrollListener: GithubUserScrollListener = GithubUserScrollListener(this)

    private val onRecordUpdated: Observer<in Long> = Observer {
        adapter.onRecordUpdated(it);
    }

    init {
        layoutManager = LinearLayoutManager(context)
        adapter = GithubUserAdapter()
        addOnScrollListener(scrollListener)
    }

    override fun getAdapter(): GithubUserAdapter {
        return if(super.getAdapter() is GithubUserAdapter) {
            super.getAdapter() as GithubUserAdapter
        } else {
            adapter = GithubUserAdapter()
            adapter
        }
    }

    fun setGridMode(gridMode: Boolean) {
        layoutManager = if (gridMode)
            GridLayoutManager(context,2)
        else
            LinearLayoutManager(context)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        UtilsLiveData.updatedRecordId.observeForever(onRecordUpdated)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        UtilsLiveData.updatedRecordId.removeObserver(onRecordUpdated)
    }


    inner class GithubUserScrollListener(githubUserRecyclerView: GithubUserRecyclerView): EndlessRecyclerOnScrollListener() {
        private val recyclerViewReference: WeakReference<GithubUserRecyclerView> = WeakReference(githubUserRecyclerView)

        override fun onLoadMore(page: Int) {
            recyclerViewReference.get()?.pageNoRequested?.value = page
        }

    }


}