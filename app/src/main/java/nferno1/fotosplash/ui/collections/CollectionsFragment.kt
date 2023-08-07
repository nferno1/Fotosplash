package nferno1.fotosplash.ui.collections

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import nferno1.fotosplash.R
import nferno1.fotosplash.data.collections.Collections
import nferno1.fotosplash.databinding.FragmentCollectionsBinding
import nferno1.fotosplash.utils.Constants.KEY_COVER_URL
import nferno1.fotosplash.utils.Constants.KEY_DESCRIPTION_COLLECTION
import nferno1.fotosplash.utils.Constants.KEY_ID_COLLECTION
import nferno1.fotosplash.utils.Constants.KEY_SIGN_COLLECTION
import nferno1.fotosplash.utils.Constants.KEY_TAGS_COLLECTION
import nferno1.fotosplash.utils.Constants.KEY_TITLE_COLLECTION
import nferno1.fotosplash.utils.Constants.KEY_TOKEN

@AndroidEntryPoint
class CollectionsFragment : Fragment(), CollectionAdapter.OnItemClickListener {

    private var _binding: FragmentCollectionsBinding? = null

    private var token: String? = null

    private val viewModel by viewModels<CollectionsViewModel>()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCollectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        token = sharedPref!!.getString(KEY_TOKEN, null)
        viewModel.getCollections(token!!)
        val adapter = CollectionAdapter(this)
        binding.recycleView.adapter = adapter
        viewModel.collections.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(collection: Collections) {
        var tags = ""
        collection.tags.forEach {
            tags += "#${it.title} "
        }

        val sign =
            "${collection.totalPhotos ?: 0} ${requireContext().getString(R.string.imagesBY)}${collection.user!!.username ?: ""}"

        val bundle = Bundle()
        bundle.putString(KEY_ID_COLLECTION, collection.id)
        bundle.putString(KEY_TITLE_COLLECTION, collection.title)
        bundle.putString(KEY_TAGS_COLLECTION, tags)
        bundle.putString(KEY_DESCRIPTION_COLLECTION, collection.description ?: "")
        bundle.putString(KEY_SIGN_COLLECTION, sign)
        bundle.putString(KEY_COVER_URL, collection.coverPhoto!!.urls!!.regular ?: "")

        findNavController().navigate(
            R.id.action_navigation_dashboard_to_collectionsOpen,
            args = bundle
        )
    }


}