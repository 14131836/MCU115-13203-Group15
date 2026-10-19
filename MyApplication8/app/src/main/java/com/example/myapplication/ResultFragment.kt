package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.taskStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                TaskStatus.IDLE -> {
                    binding.tvResultStatus.text = "Idle"
                }
                TaskStatus.PREPARING -> {
                    binding.tvResultStatus.text = "Preparing..."
                }
                TaskStatus.WORKING -> {
                    binding.tvResultStatus.text = "Working..."
                }
                TaskStatus.FINISHED -> {
                    binding.tvResultStatus.text = "背景工作結束！"
                }
                TaskStatus.CANCELED -> {
                    binding.tvResultStatus.text = "Canceled"
                }
                null -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
