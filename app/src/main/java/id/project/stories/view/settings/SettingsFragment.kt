package id.project.stories.view.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import id.project.stories.R
import id.project.stories.databinding.FragmentSettingsBinding
import id.project.stories.utils.ViewModelFactory
import id.project.stories.utils.component.CustomAlertDialog
import id.project.stories.view.main.MainViewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var customAlertDialog: CustomAlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customAlertDialog = CustomAlertDialog(requireActivity())

        setupAction()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAction() {
        binding.apply {
            localizationCard.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            logoutCard.setOnClickListener {
                customAlertDialog.apply {
                    create(
                        title = getString(R.string.confirmation),
                        message = getString(R.string.confirm_message),
                        hasNegativeBtn = true,
                        onPositiveButtonClick = { viewModel.logout() },
                        onNegativeButtonClick = { /* Do Nothing */ }
                    )
                    show()
                }
            }
        }
    }
}