package nferno1.fotosplash.ui.collectionsOpen

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import nferno1.fotosplash.R
import nferno1.fotosplash.data.Results
import nferno1.fotosplash.databinding.ItemUnsplashPhotoWidthFullScreenBinding

class AdapterCollectionsPhotos(private val listner: OnItemClickListener) :
    PagingDataAdapter<Results, AdapterCollectionsPhotos.CollectionPhotosViewHolder>(PHOTO_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionPhotosViewHolder {
        val binding =
            ItemUnsplashPhotoWidthFullScreenBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CollectionPhotosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollectionPhotosViewHolder, position: Int) {
        val currentItem = getItem(position)
        Log.d("collectionsBind", currentItem.toString())
        if (currentItem != null) {
            holder.bind(currentItem)
        }

    }

    inner class CollectionPhotosViewHolder(val binding: ItemUnsplashPhotoWidthFullScreenBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.imageViewWidthFullScreen.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listner.onItemClick(item)
                    }
                }
            }

            binding.likesWidthFullScreen.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        if (item.likedByUser == false) {
                            item.likedByUser = true
                            listner.onClickOnLikes(item.id.toString(), true)
                            binding.likesWidthFullScreen.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.like_yes,
                                0
                            )
                            item.likes = (item.likes ?: 0) + 1
                            binding.likesWidthFullScreen.text = "${item.likes}"
                        } else {
                            item.likedByUser = false
                            listner.onClickOnLikes(item.id.toString(), false)
                            binding.likesWidthFullScreen.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.like_border,
                                0
                            )
                            item.likes = (item.likes ?: 1) - 1
                            binding.likesWidthFullScreen.text = "${item.likes}"
                        }
                    }
                }
            }

            binding.downloadWidthFullScreen.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        item.urls?.raw?.let { it1 -> item.id?.let { it2 ->
                            listner.onItemDownloadClick(
                                it2, it1,binding.downloadWidthFullScreen)
                        } }
                    }
                }
            }

        }

        fun bind(results: Results) {
            Log.d("collectionsBind", results.toString())
            binding.apply {
                likesWidthFullScreen.text = results.likes.toString()
                nameUserWidthFullScreen.text = results.user!!.username ?: ""
                nameFirstAndLastWidthFullScreen.text = results.user?.name ?: ""
                Glide
                    .with(imageViewWidthFullScreen.context)
                    .load(results.urls!!.regular)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            progressBarItemidthFullScreen.isVisible = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            progressBarItemidthFullScreen.isVisible = false
                            return false
                        }

                    })
                    .into(imageViewWidthFullScreen)

                Glide
                    .with(imageProfileItemWidthFullScreen.context)
                    .load(results.user!!.profileImage?.small)
                    .centerCrop()
                    .into(imageProfileItemWidthFullScreen)

                downloadWidthFullScreen.text = ""

                if (results.likedByUser == true) {
                    likesWidthFullScreen.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.like_yes, 0
                    )
                } else {
                    likesWidthFullScreen.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.like_border, 0
                    )
                }
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(photo: Results)
        fun onItemDownloadClick(id: String, url: String, downloadText:TextView)

        fun onClickOnLikes(id: String, b: Boolean)
    }

    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<Results>() {
            override fun areItemsTheSame(oldItem: Results, newItem: Results): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Results, newItem: Results): Boolean {
                return oldItem == newItem
            }
        }

    }


}