package nferno1.fotosplash.ui.onBoarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nferno1.fotosplash.R

class OnBoardingItemsAdapter(private val onBoardingItems: List<OnBoardingItem>) :
    RecyclerView.Adapter<OnBoardingItemsAdapter.OnBoardingItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingItemViewHolder {
        return OnBoardingItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.slides_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: OnBoardingItemViewHolder, position: Int) {
        holder.bind(onBoardingItems[position])
    }

    override fun getItemCount() = onBoardingItems.size


    inner class OnBoardingItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val textOnBoarding = view.findViewById<TextView>(R.id.textOnBoarding)

        fun bind(onBoardingItem: OnBoardingItem) {
            textOnBoarding.text = onBoardingItem.textOnBoarding
        }


    }

}