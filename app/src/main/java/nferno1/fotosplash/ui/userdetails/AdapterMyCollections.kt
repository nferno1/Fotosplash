package nferno1.fotosplash.ui.userdetails

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import nferno1.fotosplash.R
import nferno1.fotosplash.data.collections.Collections
import nferno1.fotosplash.databinding.ItemUnsplashCollectionBinding

class AdapterMyCollections(
    private val onClickOnCollection: (Collections) -> Unit,
) : RecyclerView.Adapter<MyViewHolder>() {

    private var data: List<Collections> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Collections>) {
        this.data = data
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemUnsplashCollectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data.getOrNull(position)
        holder.binding.apply {
            Glide.with(imageViewInCollections)
                .load(item?.coverPhoto!!.urls!!.regular)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_error)
                .into(imageViewInCollections)
            nameUserCollections.text = "@${item.user!!.username}"
            nameFirstAndLastInCollections.text = item.user!!.name
            nameCollections.text = item.title
            countPhotoInCollections.text =
                "${item.totalPhotos.toString()} ${holder.binding.root.context.getString(R.string.photograf)}"

            Glide.with(imageProfileItemInCollectiuons)
                .load(item.user!!.profileImage!!.medium)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_error)
                .into(imageProfileItemInCollectiuons)
        }





        holder.binding.root.setOnClickListener {

            item?.let { it1 -> onClickOnCollection(it1) }

        }
    }
}

class MyViewHolder(val binding: ItemUnsplashCollectionBinding) :
    RecyclerView.ViewHolder(binding.root)