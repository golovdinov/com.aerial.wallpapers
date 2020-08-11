package com.aerial.wallpapers.ui.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_welcome.*
import com.aerial.wallpapers.App
import com.aerial.wallpapers.R
import com.aerial.wallpapers.viewmodel.WelcomeViewModel
import javax.inject.Inject

class WelcomeFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: WelcomeViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as? App)?.appComponent?.inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(WelcomeViewModel::class.java)

        if (viewModel.isWelcomePassed()) {
            findNavController().navigate(R.id.action_welcomeDest_to_wrapperDest_urgin)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnNext.setOnClickListener {
            viewModel.setWelcomePassed()

            val hero = view.findViewById<ImageView>(R.id.ivHero)

            val extras = FragmentNavigatorExtras(
                hero to "hero_image"
            )

            findNavController().navigate(R.id.action_welcomeDest_to_wrapperDest, null, null, extras)
        }

        val ivIcon = view.findViewById<ImageView>(R.id.ivHero)
        val tvWelcome = view.findViewById<TextView>(R.id.tvWelcome)
        val tvText = view.findViewById<TextView>(R.id.tvText)
        val btnNext = view.findViewById<Button>(R.id.btnNext)

        ivIcon.clipToOutline = true
        ivIcon.alpha = 0f
        tvWelcome.translationX = -30f
        tvWelcome.alpha = 0f
        tvText.translationX = -30f
        tvText.alpha = 0f
        btnNext.alpha = 0f

        val singleDuration = 250L

        val animIconAlpha = ObjectAnimator.ofFloat(ivIcon, "alpha", 0f, 1f).apply {
            duration = singleDuration
        }

        val animWelcomeAlpha = ObjectAnimator.ofFloat(tvWelcome, "alpha", 0f, 1f).apply {
            duration = singleDuration
        }
        val animWelcomeTrans = ObjectAnimator.ofFloat(tvWelcome, "translationX", -30f, 0f).apply {
            duration = singleDuration
        }

        val animTextAlpha = ObjectAnimator.ofFloat(tvText, "alpha", 0f, 1f).apply {
            duration = singleDuration
        }
        val animTextTrans = ObjectAnimator.ofFloat(tvText, "translationX", -30f, 0f).apply {
            duration = singleDuration
        }

        val animBtnAlpha = ObjectAnimator.ofFloat(btnNext, "alpha", 0f, 1f).apply {
            duration = singleDuration
        }

        AnimatorSet().apply {
            startDelay = 1000L

            play(animIconAlpha).before(animWelcomeAlpha)
            play(animWelcomeAlpha).with(animWelcomeTrans).before(animTextAlpha)
            play(animTextAlpha).with(animTextTrans).before(animBtnAlpha)
            play(animBtnAlpha)

            start()
        }

    }

}