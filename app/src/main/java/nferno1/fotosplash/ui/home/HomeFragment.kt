package nferno1.fotosplash.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import nferno1.fotosplash.R
import nferno1.fotosplash.data.Results
import nferno1.fotosplash.databinding.FragmentHomeBinding
import nferno1.fotosplash.utils.Constants.ID_PHOTO
import nferno1.fotosplash.utils.Constants.KEY_TOKEN
import nferno1.fotosplash.utils.Constants.TOKEN


@AndroidEntryPoint
class HomeFragment : Fragment(), PhotoAdapter.OnItemClickListener {

    private var _binding: FragmentHomeBinding? = null

    private val viewModel by viewModels<HomeViewModel>()

    private var token: String? = null

    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recycleView.layoutManager = layoutManager
        val adapter = PhotoAdapter(this)
        binding.recycleView.adapter = adapter
        viewModel.photos.observe(viewLifecycleOwner) {
            adapter.submitData(
                viewLifecycleOwner.lifecycle,
                it
            )
        }
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        token = sharedPref!!.getString(KEY_TOKEN, null)
        if (token == null) {
            token = requireActivity().intent.getStringExtra(TOKEN)
            if (token != null) sharedPref.edit().putString(KEY_TOKEN, token).apply()
        }
        if (token != null) {
            viewModel.searchPhotos("", token!!)
            binding.apply {
                recycleView.setHasFixedSize(true)
                recycleView.itemAnimator = null
                recycleView.adapter = adapter.withLoadStateHeaderAndFooter(
                    header = PhotoLoadStateAdapter { adapter.retry() },
                    footer = PhotoLoadStateAdapter { adapter.retry() }
                )
                buttonRetry.setOnClickListener {
                    adapter.retry()
                }
                startSearch.setOnClickListener {
                    searchLayout.isVisible = true
                    backArrow.isVisible = true
                    titleUnsplash.isVisible = false
                    logoUnsplash.isVisible = false
                    startSearch.isVisible = false
                    searchLayout.isEndIconVisible = search.text.toString() != ""
                }
                backArrow.setOnClickListener {
                    searchLayout.isVisible = false
                    backArrow.isVisible = false
                    titleUnsplash.isVisible = true
                    logoUnsplash.isVisible = true
                    startSearch.isVisible = true
                    viewModel.searchPhotos("", token!!)
                }
                searchLayout.setEndIconOnClickListener {
                    viewModel.searchPhotos(binding.search.text.toString(), token!!)
                }
                search.doOnTextChanged { _, _, _, c ->
                    searchLayout.isEndIconVisible = c >= 3
                }
                search.setOnKeyListener { _, keyCode, _ ->
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (binding.search.text.toString().length > 3)
                            viewModel.searchPhotos(binding.search.text.toString(), token!!)
                        true
                    } else false
                }

            }
            adapter.addLoadStateListener { loadState ->
                binding.apply {
                    progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                    recycleView.isVisible = loadState.source.refresh is LoadState.NotLoading
                    buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                    textError.isVisible = loadState.source.refresh is LoadState.Error
                    if (loadState.source.refresh is LoadState.NotLoading &&
                        loadState.append.endOfPaginationReached &&
                        adapter.itemCount < 1
                    ) {
                        recycleView.isVisible = false
                        textViewEmpty.isVisible = true
                    } else {
                        textViewEmpty.isVisible = false
                    }
                }
            }
        } else {
            val intent = Intent(activity, nferno1.fotosplash.Authorization::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(photo: Results) {
        val bundle = Bundle()
        bundle.putString(ID_PHOTO, photo.id)
        findNavController().navigate(
            R.id.action_navigation_home_to_detailsFragment,
            args = bundle
        )
    }

    override fun onClickOnLikes(id: String, b: Boolean) {
        viewModel.liked(id, token!!, b)
    }
}
