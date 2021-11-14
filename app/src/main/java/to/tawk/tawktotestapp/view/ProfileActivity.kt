package to.tawk.tawktotestapp.view

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import to.tawk.tawktotestapp.R
import to.tawk.tawktotestapp.databinding.ActivityProfileBinding
import to.tawk.tawktotestapp.helper.Utils
import to.tawk.tawktotestapp.helper.UtilsLiveData
import to.tawk.tawktotestapp.viewmodel.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    companion object {
        const val KEY_USER_ID = "userId"
    }

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
    }

    private fun setupViewModel() {
        // initialize viewmodel
        viewModel = defaultViewModelProviderFactory.create(ProfileViewModel::class.java)
        // attac viewmodel to view
        binding.viewModel = viewModel
        binding.utilsLiveData = UtilsLiveData.Companion
        binding.lifecycleOwner = this
        // listen for user actions (back, ...)
        viewModel.actionEvent.observe(this, {
            when(it) {
                ProfileViewModel.ACTION_BACK -> finish()
                ProfileViewModel.ACTION_SAVED -> {
                    Utils.hideSoftKeyboard(this, currentFocus)
                    Snackbar.make(binding.root, R.string.saved, Snackbar.LENGTH_SHORT).show()
                }
            }
        })
        // fetch data
        viewModel.fetch(intent.getLongExtra(KEY_USER_ID, -1))

        // observe for internet connection
        UtilsLiveData.internetConnectionStatus.observe(this, { status ->
            if(status) {
                viewModel.sendPendingRequest()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear();
    }


}