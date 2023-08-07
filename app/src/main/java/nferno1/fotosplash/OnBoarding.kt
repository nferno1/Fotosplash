package nferno1.fotosplash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import nferno1.fotosplash.databinding.ActivityOnBoardingBinding
import nferno1.fotosplash.ui.onBoarding.OnBoardingItem
import nferno1.fotosplash.ui.onBoarding.OnBoardingItemsAdapter
import nferno1.fotosplash.utils.Constants.KEY_SKIP
import nferno1.fotosplash.utils.Constants.KEY_START


@Suppress("DEPRECATION")
class OnBoarding : AppCompatActivity() {

    private lateinit var onBoardingItemsAdapter: OnBoardingItemsAdapter
    private var _binding: ActivityOnBoardingBinding? = null

    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        _binding = ActivityOnBoardingBinding.inflate(layoutInflater)

        setContentView(binding.root)
        visibleClickNextBackButton(0)
        setOnBoardingItems()
        val skip = intent.getBooleanExtra(KEY_SKIP, false)


        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val firstStart = sharedPref!!.getBoolean(KEY_START, true)

        if (skip) navigate()

        if (firstStart) sharedPref.edit().putBoolean(KEY_START, false).apply()
        else navigate()

        binding.getStarted.setOnClickListener { navigate() }

    }

    private fun navigate() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setOnBoardingItems() {
        onBoardingItemsAdapter = OnBoardingItemsAdapter(
            listOf(
                OnBoardingItem(
                    R.id.cameraImage,
                    R.id.imageCircle,
                    getString(R.string.onBording1)
                ),
                OnBoardingItem(
                    R.id.cameraImage,
                    R.id.imageCircle,
                    getString(R.string.onBording2)
                ),
                OnBoardingItem(
                    R.id.cameraImage,
                    R.id.imageCircle,
                    getString(R.string.onBording3)
                )
            )
        )

        val onBoardingViewPager = findViewById<ViewPager2>(R.id.slider)
        onBoardingViewPager.adapter = onBoardingItemsAdapter

        onBoardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                visibleClickNextBackButton(position)
            }
        })

        (onBoardingViewPager.getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER

        binding.circleNext.setOnClickListener {
            if (onBoardingViewPager.currentItem + 1 < onBoardingItemsAdapter.itemCount) {
                onBoardingViewPager.currentItem += 1
            } else {
                navigate()
            }
        }

        binding.circleBack.setOnClickListener {
            if (onBoardingViewPager.currentItem - 1 >= 0) onBoardingViewPager.currentItem -= 1
        }

    }

    private fun visibleClickNextBackButton(position: Int) {
        when (position) {
            0 -> binding.apply {
                circleBack.isVisible = false
                arrowBack.isVisible = false
                getStarted.isVisible = false
            }

            1 -> binding.apply {
                getStarted.isVisible = false
                circleBack.isVisible = true
                arrowBack.isVisible = true
            }

            2 -> binding.apply {
                getStarted.isVisible = true
                circleBack.isVisible = true
                arrowBack.isVisible = true
            }

            else -> binding.apply {
                circleBack.isVisible = true
                arrowBack.isVisible = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}