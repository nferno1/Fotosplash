package nferno1.fotosplash.ui.userdetails

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import nferno1.fotosplash.R
import nferno1.fotosplash.data.Results
import nferno1.fotosplash.databinding.ItemUnsplashPhotoWidthFullScreenBinding

class AdapterPhotoLikes(
    private val onClick: (Results) -> Unit,
    private val onClickLike: (String, Int) -> Unit,
    private val onClickDownload: (String, String, TextView) -> Unit,
) : RecyclerView.Adapter<MySimpleViewHolder>() {

    private var data: List<Results> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Results>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MySimpleViewHolder {
        return MySimpleViewHolder(
            ItemUnsplashPhotoWidthFullScreenBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MySimpleViewHolder, position: Int) {
        val item = data.getOrNull(position)



        holder.binding.apply {
            likesWidthFullScreen.text = item!!.likes.toString()
            nameUserWidthFullScreen.text = item.user!!.username ?: ""
            nameFirstAndLastWidthFullScreen.text = item.user?.name ?: ""
            Glide
                .with(imageViewWidthFullScreen.context)
                .load(item.urls!!.regular)
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
                .load(item.user!!.profileImage?.small)
                .centerCrop()
                .into(imageProfileItemWidthFullScreen)

            downloadWidthFullScreen.text = ""

            if (item.likedByUser == true) {
                likesWidthFullScreen.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.like_yes, 0
                )
            } else {
                likesWidthFullScreen.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.like_border, 0
                )
            }

        }
        holder.binding.root.setOnClickListener { _ ->
            item?.let { onClick(it) }
        }

        holder.binding.likesWidthFullScreen.setOnClickListener { _ ->
            item?.let { onClickLike(it.id.toString(), position) }
        }

        holder.binding.downloadWidthFullScreen.setOnClickListener { _ ->
            item!!.urls?.raw?.let {
                item.id?.let { id ->
                    onClickDownload(
                        id,
                        it,
                        holder.binding.downloadWidthFullScreen
                    )
                }
            }
        }

    }


}

class MySimpleViewHolder(val binding: ItemUnsplashPhotoWidthFullScreenBinding) :
    RecyclerView.ViewHolder(binding.root)