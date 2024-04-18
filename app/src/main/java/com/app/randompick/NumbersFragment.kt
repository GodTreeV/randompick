package com.app.randompick

import android.animation.IntEvaluator
import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.app.randompick.databinding.FragmentNumbersBinding
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.TriangleEdgeTreatment
import kotlin.random.Random

class NumbersFragment : Fragment() {

    private lateinit var binding: FragmentNumbersBinding
    private var minValue = Long.MIN_VALUE
    private var maxValue = Long.MAX_VALUE

    private val vm by lazy { ViewModelProvider(requireActivity())[SeedViewModel::class.java] }

    private val shapeDrawable = MaterialShapeDrawable(ShapeAppearanceModel.builder().apply {
        setAllCornerSizes(20f)
        setTopEdge(TriangleEdgeTreatment(18f, false))
    }.build())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNumbersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            smallInput.addTextChangedListener {
                it?.let {
                    if (it.toString() != "") {
                        minValue = it.toString().toLong()
                        pickBt.isEnabled = (minValue > maxValue).not()
                        if (minValue > maxValue) {
                            requireContext().toast { "too big" }
                        }
                    }
                }
            }

            bigInput.addTextChangedListener {
                it?.let {
                    if (it.toString() != "") {
                        maxValue = it.toString().toLong()
                        pickBt.isEnabled = (minValue > maxValue).not()
                        if (minValue > maxValue) {
                            requireContext().toast { "too small" }
                        }
                    }
                }
            }

            pickBt.setOnClickListener {
                if (minValue == Long.MIN_VALUE || maxValue == Long.MAX_VALUE) {
                    requireContext().toast { "Please determine the range value ~" }
                    return@setOnClickListener
                }
                if (it.isEnabled) {

                    val result = Random.nextLong(minValue, maxValue)

                    ObjectAnimator.ofObject(LongEvaluator(), minValue, maxValue).apply {
                        duration = 2500L
                        interpolator = BounceInterpolator()
                        addUpdateListener {
                            binding.pickedText.text = /*(it.animatedValue as Long).toString()*/Random.nextLong(minValue, maxValue).toString()
                        }
                        doOnEnd {
                            showPickedText(result)
                            val newGroup = SeedGroup(groupId = System.currentTimeMillis(), title = result.toString(), usedFor2Nums = true)
                            val minSeed = Seed(
                                seedId = System.currentTimeMillis(),
                                value = minValue.toString(),
                                isNumber = true,
                                groupId = newGroup.groupId
                            )
                            val maxSeed = Seed(
                                seedId = System.currentTimeMillis() + 10086,
                                value = maxValue.toString(),
                                isNumber = true,
                                groupId = newGroup.groupId
                            )
                            vm.useDao({
                                it.addSeedGroup(newGroup)
                                it.addSeed(minSeed)
                                it.addSeed(maxSeed)
                            }, {
                                mainLaunch {
                                    (requireContext().getSystemService(InputMethodManager::class.java) as InputMethodManager).hideSoftInputFromWindow(
                                        requireView().windowToken,
                                        0
                                    )
                                    (requireView() as ViewGroup).clearFocus()

                                    TransitionManager.beginDelayedTransition(binding.container, Fade().apply {
                                        duration = 300L
                                    })
                                    pickedTextDescription.background = shapeDrawable
                                    pickedTextDescription.text = "A random number is $result"
                                    pickedTextDescription.isVisible = true
                                }
                            })
                        }
                        start()
                    }
                }
            }

        }
    }

    private fun showPickedText(picked: Long) {
        binding.pickedText.text = picked.toString()
    }

    companion object {
        @JvmStatic
        fun newInstance() = NumbersFragment()
    }

    class LongEvaluator : TypeEvaluator<Long> {
        override fun evaluate(fraction: Float, startValue: Long, endValue: Long): Long {
            val startInt: Long = startValue
            return (startInt + fraction * (endValue - startInt)).toLong()
        }
    }
}