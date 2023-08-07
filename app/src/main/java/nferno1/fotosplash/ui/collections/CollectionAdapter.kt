package nferno1.fotosplash.ui.collections

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import nferno1.fotosplash.R
import nferno1.fotosplash.data.collections.Collections
import nferno1.fotosplash.databinding.ItemUnsplashCollectionBinding

class CollectionAdapter(private val listner: OnItemClickListener) :
    PagingDataAdapter<Collections, CollectionAdapter.CollectionViewHolder>(PHOTO_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val binding =
            ItemUnsplashCollectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CollectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null) {
            holder.bind(currentItem, holder.binding.root.context)
        }

    }

    inner class CollectionViewHolder(val binding: ItemUnsplashCollectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.imageViewInCollections.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listner.onItemClick(item)
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(collection: Collections, context: Context) {
            binding.apply {
                Glide.with(itemView)
                    .load(collection.coverPhoto!!.urls!!.regular)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error)
                    .into(imageViewInCollections)
                nameUserCollections.text = "@${collection.user!!.username}"
                nameFirstAndLastInCollections.text = collection.user!!.name
                nameCollections.text = collection.title
                countPhotoInCollections.text =
                    "${collection.totalPhotos.toString()} ${context.getString(R.string.photograf)}"

                Glide.with(itemView)
                    .load(collection.user!!.profileImage!!.medium)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error)
                    .into(imageProfileItemInCollectiuons)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(collection: Collections)

    }

    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<Collections>() {
            override fun areItemsTheSame(oldItem: Collections, newItem: Collections): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Collections, newItem: Collections): Boolean {
                return oldItem == newItem
            }
        }

    }


}