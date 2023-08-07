package nferno1.fotosplash.ui.userdetails

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dagger.hilt.android.AndroidEntryPoint
import nferno1.fotosplash.Authorization
import nferno1.fotosplash.R
import nferno1.fotosplash.data.Results
import nferno1.fotosplash.data.collections.Collections
import nferno1.fotosplash.databinding.FragmentNotificationsBinding
import nferno1.fotosplash.utils.Constants.GEO
import nferno1.fotosplash.utils.Constants.GOOGLE_MAPS
import nferno1.fotosplash.utils.Constants.ID_PHOTO
import nferno1.fotosplash.utils.Constants.KEY_AUTORIZATION
import nferno1.fotosplash.utils.Constants.KEY_CLICK
import nferno1.fotosplash.utils.Constants.KEY_COVER_URL
import nferno1.fotosplash.utils.Constants.KEY_DESCRIPTION_COLLECTION
import nferno1.fotosplash.utils.Constants.KEY_ID_COLLECTION
import nferno1.fotosplash.utils.Constants.KEY_SIGN_COLLECTION
import nferno1.fotosplash.utils.Constants.KEY_SKIP
import nferno1.fotosplash.utils.Constants.KEY_TAGS_COLLECTION
import nferno1.fotosplash.utils.Constants.KEY_TITLE_COLLECTION
import nferno1.fotosplash.utils.Constants.KEY_TOKEN
import nferno1.fotosplash.utils.Constants.TAB_LIKES
import nferno1.fotosplash.utils.Constants.TAB_PHOTO
import nferno1.fotosplash.utils.LaunchSetWorkers
import nferno1.fotosplash.utils.MyWorker
import java.io.File
import java.util.*

const val UNIQUE_WORK_NAME = "DownloadImage"

@AndroidEntryPoint
class UserDetails : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    private val viewModel by viewModels<UserDetailsViewModel>()

    private val adapter =
        AdapterPhotoLikes(
            { onItemClick(it) },
            { id, position -> onItemLikeClick(id, position) },
            { id, url, download -> onClickDownload(id, url, download) })

    private val adapterCollection = AdapterMyCollections { onCollectionClick(it) }

    private var totalLikes: Int = 0

    private val binding get() = _binding!!
    private var sharedPref: SharedPreferences? = null

    var token: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("CommitPrefEdits", "ResourceAsColor", "QueryPermissionsNeeded", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        token = sharedPref!!.getString(KEY_TOKEN, null)
        token?.let { viewModel.getInfoMe(it) }
        binding.recycleView.adapter = adapter
        viewModel.user.observe(viewLifecycleOwner) {
            totalLikes = it.totalLikes ?: 0
            binding.apply {
                Glide.with(root)
                    .load(it.profileImage!!.large)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error)
                    .into(imageView)
                nameInUserFragment.text = it.name
                userNameInUserFragment.text = "@${it.username}"
                follow.text =
                    "${requireContext().getString(R.string.follow)} ${it.followersCount} ${
                        requireContext().getString(
                            R.string.follow2
                        )
                    }"
                location.text = it.location
                mail.text = it.email
                download.text = it.downloads.toString()
                tabFoto.text =
                    "${it.totalPhotos}\n${requireContext().getString(R.string.photograf)}"
                tabLikes.text = "$totalLikes\n${requireContext().getString(R.string.liked)}"
                collection.text =
                    "${it.totalCollections}\n${requireContext().getString(R.string.collections)}"
            }
            if (token != null) {
                it.username?.let { it1 -> viewModel.getPhoto(it1, token!!) }
                binding.apply {
                    tabFoto.setTextAppearance(R.style.Theme_nferno1_fotosplash_Text_Grey)
                    collection.setTextAppearance(R.style.Theme_nferno1_fotosplash_Text_Grey)
                    tabLikes.setTextAppearance(R.style.Theme_nferno1_fotosplash_Text)
                }
            }
            binding.tabFoto.setOnClickListener { _ ->
                viewModel.getPhoto(it.username!!, token!!, TAB_PHOTO)
                binding.recycleView.adapter = adapter
                binding.apply {

                    tabLikes.setTextAppearance(R.style.Theme_nferno1_fotosplash_Text_Grey)
                    collection.setTextAppearance(R.style.Theme_nferno1_fotosplash_Text_Grey)
                    tabFoto.setTextAppearance(R.style.Theme_nferno1_fotosplash_Text)
                }

            }
            binding.tabLikes.setOnClickListener { _ ->
                viewModel.getPhoto(it.username!!, token!!, TAB_LIKES)
                binding.recycleView.adapter = adapter
                binding.apply {
                    tabFoto.setTextAppearance(R.style.Theme_nferno1_fotosplash_Text_Grey)
                    collection.setTextAppearance(R.style.Theme_nferno1_fotosplash_Text_Grey)
                    tabLikes.setTextAppearance(R.style.Theme_nferno1_fotosplash_Text)
                }
            }

            binding.collection.setOnClickListener { _ ->
                viewModel.getCollections(it.username!!, token!!)
                binding.recycleView.adapter = adapterCollection
                binding.apply {
                    tabFoto.setTextAppearance(R.style.Theme_nferno1_fotosplash_Text_Grey)
                    tabLikes.setTextAppearance(R.style.Theme_nferno1_fotosplash_Text_Grey)
                    collection.setTextAppearance(R.style.Theme_nferno1_fotosplash_Text)
                }
            }
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.setData(it)
            }
        }

        viewModel.collections.observe(viewLifecycleOwner) {
            it?.let { it1 -> adapterCollection.setData(it1) }
        }

        binding.location.setOnClickListener {
            val gmmIntentUri =
                Uri.parse("$GEO${Uri.encode(binding.location.text.toString())}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage(GOOGLE_MAPS)
            startActivity(mapIntent)
        }

        binding.buttonYes.setOnClickListener {
            try {
                val dirCache = requireContext().cacheDir
                deleteDir(dirCache)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error ${e.message}", Toast.LENGTH_LONG).show()
            }
            binding.apply {
                viewGradient.isVisible = false
                confirmation.isVisible = false
            }
            sharedPref!!.edit().putString(KEY_TOKEN, null).apply()
            sharedPref!!.edit().putBoolean(KEY_CLICK, false).apply()
            sharedPref!!.edit().putBoolean(KEY_AUTORIZATION, true).apply()
            sharedPref!!.edit().putBoolean("qwe", false).apply()
            viewModel.deleteAllInfo()
            val intent = Intent(requireContext(), Authorization::class.java)
            intent.putExtra(KEY_SKIP, true)
            startActivity(intent)
            requireActivity().finish()
        }
        binding.buttonNo.setOnClickListener {
            binding.apply {
                viewGradient.isVisible = false
                confirmation.isVisible = false
            }
        }
        binding.logoutButton.setOnClickListener {
            binding.apply {
                viewGradient.isVisible = true
                confirmation.isVisible = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onItemClick(photo: Results) {
        val bundle = Bundle()
        bundle.putString(ID_PHOTO, photo.id)
        findNavController().navigate(
            R.id.action_navigation_notifications_to_detailsFragment,
            args = bundle
        )
    }

    @SuppressLint("SetTextI18n")
    private fun onItemLikeClick(id: String, position: Int) {
        viewModel.likedOut(id, token!!)
        adapter.notifyItemRemoved(position)
        totalLikes -= 1
        binding.tabLikes.text = "$totalLikes\n${requireContext().getString(R.string.liked)}"
    }

    private fun onClickDownload(id: String, url: String, download: TextView) {
        viewModel.getDownload(id, token!!)
        val workRequestCommon by lazy { MyWorker.createWorkRequest(url) }
        LaunchSetWorkers.checkPermission(
            requireContext(),
            url,
            id,
            workRequestCommon.id,
            workRequestCommon,
            this@UserDetails,
            binding.constraintLayoutUserDetails,
            download,
            null
        )
    }

    private fun onCollectionClick(collection: Collections) {
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
            R.id.action_navigation_notifications_to_collectionsOpen,
            args = bundle
        )
    }

    private fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children: Array<String> = dir.list() as Array<String>
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }


}