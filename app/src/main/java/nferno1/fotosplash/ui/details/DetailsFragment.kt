package nferno1.fotosplash.ui.details

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.EXTRA_TEXT
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import nferno1.fotosplash.R
import nferno1.fotosplash.data.Location
import nferno1.fotosplash.databinding.PhotoDetailsBinding
import nferno1.fotosplash.utils.Constants
import nferno1.fotosplash.utils.Constants.ID_PHOTO
import nferno1.fotosplash.utils.Constants.KEY_TOKEN
import nferno1.fotosplash.utils.LaunchSetWorkers.checkPermission
import nferno1.fotosplash.utils.MyWorker
import java.util.*


@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var idPhoto: String? = null
    private var _binding: PhotoDetailsBinding? = null
    private val binding get() = _binding!!
    private var token: String? = null
    private val viewModel by viewModels<DetailsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idPhoto = it.getString(ID_PHOTO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = PhotoDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        token = sharedPref!!.getString(KEY_TOKEN, null)
        idPhoto?.let { id -> token?.let { token -> viewModel.getPhoto(id, token) } }
        viewModel.photo.observe(viewLifecycleOwner) { photo ->
            val workRequestCommon by lazy { MyWorker.createWorkRequest(photo.urls!!.raw ?: "") }
            binding.apply {
                Glide.with(imageViewDetailsPhoto)
                    .load(photo!!.urls?.regular)
                    .error(R.drawable.ic_error)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            progressBarPhotoDetails.isVisible = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            progressBarPhotoDetails.isVisible = false
                            return false
                        }

                    })
                    .into(imageViewDetailsPhoto)

                Glide.with(imageProfileItemWidthFullScreen)
                    .load(photo.user?.profileImage?.small)
                    .error(R.drawable.ic_error)
                    .centerCrop()
                    .into(imageProfileItemWidthFullScreen)

                nameFirstAndLastDetailsPhoto.text = photo.user?.name ?: ""
                nameUserDetailsPhoto.text = photo.user?.username ?: ""
                likesDetailsPhoto.text = (photo.likes ?: 0).toString()
                if (photo.likedByUser == true) likesDetailsPhoto.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.like_yes, 0
                )
                else likesDetailsPhoto.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.like_border, 0
                )
                locationDetailsPhoto.text = location(photo.location!!)
                locationDetailsPhoto.setOnClickListener {
                    val uri: String
                    if (photo.location!!.position!!.longitude != null && photo.location!!.position!!.latitude != null) {
                        uri =
                            "${Constants.GEO}${photo.location!!.position!!.latitude},${photo.location!!.position!!.longitude}(${photo.location!!.name ?: ""})"
                        val gmmIntentUri =
                            Uri.parse(uri)
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage(Constants.GOOGLE_MAPS)
                        startActivity(mapIntent)
                    } else {
                        if (locationDetailsPhoto.text.toString() !== "") {
                            uri = locationDetailsPhoto.text.toString()
                            val gmmIntentUri =
                                Uri.parse(uri)
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            mapIntent.setPackage(Constants.GOOGLE_MAPS)
                            startActivity(mapIntent)
                        } else {
                            Toast.makeText(requireContext(), "No Address", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

                var tags = ""
                photo.tags.forEach { tags += "#${it.title} " }
                tagsDetailsPhoto.text = tags

                cameraPhotoDetails.text =
                    "${requireContext().getString(R.string.madeWith)}${photo.exif!!.name ?: ""}\n${
                        requireContext().getString(R.string.model)
                    }${photo.exif!!.model ?: ""}\n${requireContext().getString(R.string.exposure)}${photo.exif!!.exposureTime ?: ""}\n${
                        requireContext().getString(
                            R.string.aperture
                        )
                    }${photo.exif!!.aperture ?: ""}\n${requireContext().getString(R.string.focal)}${photo.exif!!.focalLength ?: ""}\nISO: ${photo.exif!!.iso ?: ""}"



                aboutUserPhotoDetails.text =
                    "${requireContext().getString(R.string.about)} @${photo.user?.username ?: ""}"
                aboutTextUserPhotoDetails.text = photo.user?.bio ?: ""
                downloadPhotoDetails.text = "${photo.downloads}"

                likesDetailsPhoto.setOnClickListener {
                    if (photo.likedByUser == false) {
                        photo.likedByUser = true
                        viewModel.liked(photo.id!!, token!!, true)
                        binding.likesDetailsPhoto
                            .setCompoundDrawablesRelativeWithIntrinsicBounds(
                                0, 0, R.drawable.like_yes, 0
                            )
                        photo.likes = (photo.likes ?: 0) + 1
                    } else {
                        photo.likedByUser = false
                        viewModel.liked(photo.id!!, token!!, false)
                        binding.likesDetailsPhoto
                            .setCompoundDrawablesRelativeWithIntrinsicBounds(
                                0, 0, R.drawable.like_border, 0
                            )
                        photo.likes = (photo.likes ?: 1) - 1
                    }
                    binding.likesDetailsPhoto.text = "${photo.likes}"
                }

                backArrowPhotoDetails.setOnClickListener {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }

                sharedPhotoDetails.setOnClickListener {
                    val url = "https://unsplash.com/photos/${idPhoto}"
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(EXTRA_TEXT, url)
                    intent.type = "text/plain"
                    startActivity(Intent.createChooser(intent, "Share To:"))
                }

                downloadPhotoDetails.setOnClickListener {
                    viewModel.getDownload(photo.id!!, token!!)
                    checkPermission(
                        requireContext(),
                        photo.urls!!.raw ?: "",
                        photo.id ?: "",
                        workRequestCommon.id,
                        workRequestCommon,
                        this@DetailsFragment,
                        binding.frameLayoutPhotoDetails,
                        binding.downloadPhotoDetails,
                        photo.downloads
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun location(location: Location): String {
        val loc = mutableListOf<String>()
        if (location.name != null) loc.add(location.name!!)
        if (location.city != null) loc.add(location.city!!)
        if (location.country != null) loc.add(location.country!!)
        return loc.joinToString(", ")
    }


}