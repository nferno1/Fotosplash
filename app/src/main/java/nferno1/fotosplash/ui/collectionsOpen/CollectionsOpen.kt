package nferno1.fotosplash.ui.collectionsOpen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dagger.hilt.android.AndroidEntryPoint
import nferno1.fotosplash.R
import nferno1.fotosplash.data.Results
import nferno1.fotosplash.databinding.FragmentCollectionsOpenBinding
import nferno1.fotosplash.utils.Constants.ID_PHOTO
import nferno1.fotosplash.utils.Constants.KEY_COVER_URL
import nferno1.fotosplash.utils.Constants.KEY_DESCRIPTION_COLLECTION
import nferno1.fotosplash.utils.Constants.KEY_ID_COLLECTION
import nferno1.fotosplash.utils.Constants.KEY_SIGN_COLLECTION
import nferno1.fotosplash.utils.Constants.KEY_TAGS_COLLECTION
import nferno1.fotosplash.utils.Constants.KEY_TITLE_COLLECTION
import nferno1.fotosplash.utils.Constants.KEY_TOKEN
import nferno1.fotosplash.utils.LaunchSetWorkers
import nferno1.fotosplash.utils.MyWorker

@AndroidEntryPoint
class CollectionsOpen : Fragment(), AdapterCollectionsPhotos.OnItemClickListener {

    private var _binding: FragmentCollectionsOpenBinding? = null
    private val binding get() = _binding!!
    private var token: String? = null
    private val viewModel by viewModels<CollectionsOpenViewModel>()
    private var id: String? = null
    private var title: String? = null
    private var tags: String? = null
    private var description: String? = null
    private var sign: String? = null
    private var coverUrl: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActivityTitle(title.toString().uppercase())
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        token = sharedPref!!.getString(KEY_TOKEN, null)
        viewModel.getPhoto(id!!, token!!)
        val adapter = AdapterCollectionsPhotos(this)
        binding.apply {
            recycleViewCollectionsOpen.adapter = adapter
            Glide.with(imageViewCollectionsOpen)
                .load(coverUrl)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_error)
                .into(imageViewCollectionsOpen)

            titleCollectionsOpen.text = title.toString().uppercase()
            tagsCollectionsOpen.text = tags ?: ""
            descriptionCollectionsOpen.text = description ?: ""
            usernameCollectionsOpen.text = sign


        }

        viewModel.collectionsPhotos.observe(viewLifecycleOwner) {
            Log.d("collectionsBind", it.toString())
            adapter.submitData(viewLifecycleOwner.lifecycle, it)

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString(KEY_ID_COLLECTION)
            title = it.getString(KEY_TITLE_COLLECTION)
            tags = it.getString(KEY_TAGS_COLLECTION)
            description = it.getString(KEY_DESCRIPTION_COLLECTION)
            sign = it.getString(KEY_SIGN_COLLECTION)
            coverUrl = it.getString(KEY_COVER_URL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCollectionsOpenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClick(photo: Results) {
        val bundle = Bundle()
        bundle.putString(ID_PHOTO, photo.id)
        findNavController().navigate(
            R.id.action_collectionsOpen_to_detailsFragment,
            args = bundle
        )
    }

    override fun onItemDownloadClick(id: String, url: String, downloadText: TextView) {
        viewModel.getDownload(id, token!!)
        val workRequestCommon by lazy { MyWorker.createWorkRequest(url) }
        LaunchSetWorkers.checkPermission(
            requireContext(),
            url,
            id,
            workRequestCommon.id,
            workRequestCommon,
            this@CollectionsOpen,
            binding.constraintLayoutCollectionOpen,
            downloadText,
            null
        )
    }

    override fun onClickOnLikes(id: String, b: Boolean) {
        viewModel.liked(id, token!!, b)
    }

    private fun Fragment.setActivityTitle(title: String) {
        (activity as AppCompatActivity?)?.supportActionBar?.title = title
    }

}