package to.tawk.tawktotestapp.view

import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import to.tawk.tawktotestapp.R
import to.tawk.tawktotestapp.databinding.ActivityMainBinding
import to.tawk.tawktotestapp.helper.EndlessRecyclerOnScrollListener
import to.tawk.tawktotestapp.helper.UtilsLiveData
import to.tawk.tawktotestapp.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupViewModel()
    }


    private fun setupView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    private fun setupViewModel() {
        // initialize viewmodel
        viewModel = defaultViewModelProviderFactory.create(MainViewModel::class.java)
        // bind viewmodel to view
        binding.viewModel = viewModel
        binding.utilsLiveData = UtilsLiveData.Companion
        binding.lifecycleOwner = this
        // observe for internet connection
        UtilsLiveData.internetConnectionStatus.observe(this, { status ->
            if(status) {
                viewModel.sendPendingRequest()
            }
        })
        // observe new data received from api
        viewModel.dataReceived.observe(this, binding.recyclerView.adapter::addItems)
        // observe events by user, (swipe refresh , ...)
        viewModel.actionEvent.observe(this, {
            if(it == MainViewModel.ACTION_RESET_LIST) {
                binding.recyclerView.scrollListener.resetPageCount()
                binding.recyclerView.adapter.clear()
            }
        })
        // set the grid mode on landscape orientation
        binding.recyclerView.setGridMode(requestedOrientation == Configuration.ORIENTATION_LANDSCAPE)
        // bind scroll listener to viewmodel
        binding.recyclerView.pageNoRequested.observe(this, { page ->
            if(TextUtils.isEmpty(viewModel.searchKeyword.value)) { // pagination enabled if search keywork is empty -> disable in search mode
                viewModel.fetch(page)
            }
        })

        // observe for search keyword (input) to perform search
        viewModel.searchKeyword.observe(this, viewModel::performSearch)
        // set search result to recycler view
        viewModel.searchResult.observe(this, binding.recyclerView.adapter::submitList)

        // fetch data
        viewModel.fetch()
    }

    // for landscape orientation support
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // switch to grid mode on landscape orientation
        binding.recyclerView.setGridMode(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear();
    }

}