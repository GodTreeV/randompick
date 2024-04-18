package com.app.randompick

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionManager
import com.app.randompick.databinding.FragmentListBinding
import com.app.randompick.databinding.FragmentNumbersBinding
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.TriangleEdgeTreatment
import com.google.android.material.transition.MaterialFade
import kotlin.random.Random


class ListFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() = ListFragment()
    }

    private lateinit var binding: FragmentListBinding

    private val itemAdapter = ItemAdapter()

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
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            listRv.adapter = itemAdapter

            if (vm.reApplySeedGroup != null) {
                itemAdapter.fillFromGroup(vm.reApplySeedGroup!!)
                vm.reApplySeedGroup = null
            }

            pickBt.setOnClickListener {
                val selected = itemAdapter.getSelectedList()

                if (selected.isEmpty() || selected.size == 1) {
                    requireContext().toast { "Please select 2 items at least !" }
                    return@setOnClickListener
                }

                if (itemAdapter.isFillFromHistory) {
                    // need reset seedId
                    selected.forEach {
                        it.seedId = System.currentTimeMillis() + Random.nextInt(1, Int.MAX_VALUE)
                    }
                }

                log { "selected = $selected" }

                if (selected.isEmpty()) {
                    requireContext().toast { "input something first" }
                    return@setOnClickListener
                }

                val result = Random.nextInt(0, selected.lastIndex)

                TransitionManager.beginDelayedTransition(descriptionContainer, MaterialFade())
                pickedText.text = selected[result].value
                pickedTextDescriotion.isVisible = true
                pickedTextDescriotion.background = shapeDrawable
                pickedTextDescriotion.text = "Look at the random item ~ "
                val newGroup = SeedGroup(groupId = System.currentTimeMillis(), title = selected[result].value, usedFor2Nums = false)
                // set selected seed's groupId
                selected.forEach {
                    it.groupId = newGroup.groupId
                }
                // save to db
                vm.useDao({
                          it.addSeedGroup(newGroup)
                    selected.forEach { seed ->
                        it.addSeed(seed)
                    }
                }, {
                    mainLaunch {
                        requireContext().toast { "save group √" }

                        itemAdapter.reset()
                    }
                })
            }
        }
    }
}