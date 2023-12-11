package com.bignerdranch.android.sunset

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.drawable.ColorDrawable
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bignerdranch.android.sunset.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var isSunset = true
    private var isAnimating = false

    private lateinit var sunsetAnimatorSet: AnimatorSet
    private lateinit var sunriseAnimatorSet: AnimatorSet
    private lateinit var pulsatingAnimator: ObjectAnimator

    private lateinit var binding: ActivityMainBinding

    private val blueSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.blue_sky)
    }
    private val sunsetSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.sunset_sky)
    }
    private val nightSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.night_sky)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.scene.setOnClickListener {
            startAnimation()
        }

        binding.scene.setOnClickListener {
            if (isSunset) {
                startAnimation()
            } else {
                startSunriseAnimation()
            }
            isSunset = !isSunset
        }


        createPulsatingAnimation()
        pulsatingAnimator.start()
    }

    private fun createPulsatingAnimation() {
        pulsatingAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.sun,
            PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.2f, 1.0f),
            PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.2f, 1.0f)
        ).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
    }


    private fun startAnimation() {
        val sunYStart = binding.sun.top.toFloat()
        val sunYEnd = binding.sky.height.toFloat()

        val heightAnimator = ObjectAnimator
            .ofFloat(binding.sun, "y", sunYStart, sunYEnd)
            .setDuration(3000)
        heightAnimator.interpolator = AccelerateInterpolator()

        val sunsetSkyAnimator = ObjectAnimator
            .ofInt(binding.sky, "backgroundColor", blueSkyColor, sunsetSkyColor)
            .setDuration(3000)
        sunsetSkyAnimator.setEvaluator(ArgbEvaluator())

        val nightSkyAnimator = ObjectAnimator
            .ofInt(binding.sky, "backgroundColor", sunsetSkyColor, nightSkyColor)
            .setDuration(1500)
        nightSkyAnimator.setEvaluator(ArgbEvaluator())

        val animatorSet = AnimatorSet()
        animatorSet.play(heightAnimator)
            .with(sunsetSkyAnimator)
            .before(nightSkyAnimator)
        animatorSet.start()

        val currentColor = (binding.sky.background as ColorDrawable).color
        val nextColor = if (isSunset) sunsetSkyColor else blueSkyColor
        startTransitionAnimation(currentColor, nextColor, nightSkyColor)
    }

    private fun startSunriseAnimation() {
        val sunYStart = binding.sky.height.toFloat()
        val sunYEnd = binding.sun.top.toFloat()

        val heightAnimator = ObjectAnimator
            .ofFloat(binding.sun, "y", sunYStart, sunYEnd)
            .setDuration(1500)
        heightAnimator.interpolator = AccelerateInterpolator()

        val sunriseSkyAnimator = ObjectAnimator
            .ofInt(binding.sky, "backgroundColor", nightSkyColor, sunsetSkyColor)
            .setDuration(1500)
        sunriseSkyAnimator.setEvaluator(ArgbEvaluator())

        val daySkyAnimator = ObjectAnimator
            .ofInt(binding.sky, "backgroundColor", sunsetSkyColor, blueSkyColor)
            .setDuration(3000)
        daySkyAnimator.setEvaluator(ArgbEvaluator())

        val animatorSet = AnimatorSet()
        animatorSet.play(heightAnimator)
            .with(sunriseSkyAnimator)
            .after(daySkyAnimator)
        animatorSet.start()

        val currentColor = (binding.sky.background as ColorDrawable).color
        val sunriseColor = if (isSunset) nightSkyColor else sunsetSkyColor
        val dayColor = blueSkyColor

        startTransitionAnimation(currentColor, sunriseColor, dayColor)
    }

    private fun startTransitionAnimation(currentColor: Int, intermediateColor: Int, finalColor: Int) {
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), currentColor, intermediateColor, finalColor)
        colorAnimation.duration = 4500
        colorAnimation.addUpdateListener { animator ->
            binding.sky.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()
    }
}
