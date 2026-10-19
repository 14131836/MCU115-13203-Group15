package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication.databinding.FragmentProgressBinding

class ProgressFragment : Fragment() {

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.taskStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                TaskStatus.IDLE -> {
                    binding.circularProgressBar.progress = 0
                    binding.tvProgressStatus.text = "Idle"
                }
                TaskStatus.PREPARING -> {
                    binding.circularProgressBar.progress = 0
                    binding.tvProgressStatus.text = "Preparing..."
                }
                TaskStatus.WORKING -> {
                    val currProgress = viewModel.progress.value ?: 0
                    binding.circularProgressBar.progress = currProgress
                    binding.tvProgressStatus.text = "Working... $currProgress%"
                }
                TaskStatus.FINISHED -> {
                    binding.circularProgressBar.progress = 100
                    binding.tvProgressStatus.text = "背景工作結束！"
                }
                TaskStatus.CANCELED -> {
                    binding.tvProgressStatus.text = "Canceled"
                }
                null -> {}
            }
        }

        viewModel.progress.observe(viewLifecycleOwner) { progress ->
            if (viewModel.taskStatus.value == TaskStatus.WORKING) {
                binding.circularProgressBar.progress = progress
                binding.tvProgressStatus.text = "Working... $progress%"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
