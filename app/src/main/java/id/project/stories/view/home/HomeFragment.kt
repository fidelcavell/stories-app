package id.project.stories.view.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import id.project.stories.databinding.FragmentHomeBinding
import id.project.stories.utils.ViewModelFactory
import id.project.stories.utils.adapters.ListStoriesAdapter
import id.project.stories.utils.adapters.LoadingStateAdapter
import id.project.stories.view.main.MainViewModel
import id.project.stories.view.map.MapsActivity

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupAction()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupView() {
        binding.rvStories.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = ListStoriesAdapter()
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        viewModel.getAllStories().observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun setupAction() {
        binding.fabMap.setOnClickListener {
            val intentToMap = Intent(requireActivity(), MapsActivity::class.java)
            startActivity(intentToMap)
        }
    }
}