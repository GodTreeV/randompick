package com.app.randompick

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.app.randompick.databinding.FragmentHistoryBinding
import com.app.randompick.databinding.LayoutHistoryBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Date
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding

    private val allSeedGroups = mutableListOf<SeedGroups>()

    private val vm by lazy { ViewModelProvider(requireActivity())[SeedViewModel::class.java] }

    private val historyAdapter by lazy { HistoryAdapter(vm) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            vm.useDao({
                allSeedGroups.clear()
                allSeedGroups.addAll(it.queryGroups())
            }, {
                mainLaunch {
                    historyList.adapter = historyAdapter
                    historyAdapter.apply {
                        history.clear()
                        allSeedGroups.sortByDescending { it.group.groupId }
                        history.addAll(allSeedGroups)
                    }
                }
            })

            clearAll.setOnClickListener {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle("Clear history")
                    setMessage("Do you want to clear all history?")
                    setPositiveButton("Yes") { d, w ->
                        vm.useDao(
                            {
                                allSeedGroups.forEach { sg ->
                                    it.deleteSeedGroup(sg.group)
                                }
                            }, {
                                mainLaunch {
                                    historyAdapter.history.clear()
                                    historyAdapter.notifyDataSetChanged()
                                }
                            }
                        )
                        d.dismiss()
                    }
                    setNegativeButton("Cancel") { d, _ ->
                        d.dismiss()
                    }
                    show()
                }
            }
        }
    }

    class HistoryAdapter(private val viewModel: SeedViewModel) :
        RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
        class HistoryViewHolder(private val binding: LayoutHistoryBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun getBinding() = binding
        }

        val history = mutableListOf<SeedGroups>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
            return HistoryViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_history,
                    parent, false
                )
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
            val seedGroups = history[position]
            with(holder.getBinding()) {
                pickedText.text = "Picked item : ${seedGroups.group.title}"
                reapply.isVisible = seedGroups.group.usedFor2Nums.not()
                reapply.setOnClickListener {
                    viewModel.reApplySeedGroup = seedGroups
                    viewModel.navigateTo(R.id.listFragment)
                }
                dateText.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(seedGroups.group.groupId))
                delete.setOnClickListener {
                    history.removeAt(position)
                    notifyItemRemoved(position)
                    Snackbar.make(delete, "Delete ${seedGroups.group.title}", Snackbar.LENGTH_LONG)
                        .apply {
                            setAction("Undo") {
                                history.add(position, seedGroups)
                                notifyItemInserted(position)
                            }
                            addCallback(object : Snackbar.Callback() {
                                override fun onDismissed(
                                    transientBottomBar: Snackbar?,
                                    event: Int
                                ) {
                                    super.onDismissed(transientBottomBar, event)
                                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                        viewModel.useDao({
                                            it.deleteSeedGroup(seedGroups.group)
                                        }, {
                                            //delete.context.toast { "Delete ${seedGroups.group.title} √" }
                                            log { "Delete $seedGroups cause TIME_OUT" }
                                        })
                                    }
                                }
                            })
                            show()
                        }
                }
            }
        }

        override fun getItemCount(): Int {
            return history.size
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = HistoryFragment()
    }
}