package com.example.foodsure.ui.backup

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.foodsure.MainActivity
import com.example.foodsure.databinding.FragmentBackupBinding
import com.example.foodsure.ui.BaseFragment
import com.example.foodsure.ui.BaseViewModel
import kotlin.system.exitProcess

class BackupFragment() : BaseFragment<FragmentBackupBinding, BackupViewModel>() {
    override val viewModel: BackupViewModel by activityViewModels {
        BaseViewModel.provideFactory(repository, {
            BackupViewModel(repository)
        })
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBackupBinding {
        return FragmentBackupBinding.inflate(inflater, container, false)
    }

    override fun setupUI() {
        // Observe navigation
        viewModel.navigateBack.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigateUp()
                viewModel.onDoneNavigation()
            }
        }
        viewModel.toastMsg.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.onDoneToast()
            }
        }

        viewModel.restartApp.observe(viewLifecycleOwner) {
            if (it) {
                restartApp()
                viewModel.onDoneRestartApp()
            }
        }

        val backup = (activity as MainActivity).backup
        binding.btnUpload.setOnClickListener { viewModel.uploadBackup(backup) }
        binding.btnRestore.setOnClickListener { viewModel.restoreBackup(backup) }
    }

    private fun restartApp() {
        val intent = Intent(requireActivity().applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        requireActivity().finish()
        exitProcess(0)
    }
}