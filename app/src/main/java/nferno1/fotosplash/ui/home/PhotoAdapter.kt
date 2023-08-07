package nferno1.fotosplash.ui.home

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import nferno1.fotosplash.R
import nferno1.fotosplash.data.Results
import nferno1.fotosplash.databinding.ItemUnsplashPhotoBinding


class PhotoAdapter(private val listner: OnItemClickListener) :
    PagingDataAdapter<Results, PhotoAdapter.PhotoViewHolder>(PHOTO_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding =
            ItemUnsplashPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class PhotoViewHolder(private val binding: ItemUnsplashPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.imageView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listner.onItemClick(item)
                    }
                }
            }

            binding.likes.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        if (item.likedByUser == false) {
                            item.likedByUser = true
                            listner.onClickOnLikes(item.id.toString(), true)
                            binding.likes.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.like_yes,
                                0
                            )
                            item.likes = (item.likes ?: 0) + 1
                            binding.likes.text = "${item.likes}"
                        } else {
                            item.likedByUser = false
                            listner.onClickOnLikes(item.id.toString(), false)
                            binding.likes.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.like_border,
                                0
                            )
                            item.likes = (item.likes ?: 1) - 1
                            binding.likes.text = "${item.likes}"
                        }
                    }
                }
            }

        }

        @SuppressLint("SetTextI18n")
        fun bind(photo: Results) {
            binding.apply {
                Glide.with(itemView)
                    .load(photo.urls!!.regular)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            progressBarItemUnsplashPhoto.isVisible = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            progressBarItemUnsplashPhoto.isVisible = false
                            return false
                        }

                    })
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error)
                    .into(imageView)
                nameFirstAndLast.text = photo.user!!.name ?: ""
                nameUser.text = "@${photo.user!!.username ?: ""}"
                likes.text = photo.likes!!.toString()

                Glide.with(imageProfileItem)
                    .load(photo.user!!.profileImage!!.small)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error)
                    .into(imageProfileItem)

                if (photo.likedByUser == true) {
                    likes.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.like_yes, 0
                    )
                } else {
                    likes.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.like_border, 0
                    )
                }
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(photo: Results)
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