package com.app.randompick

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.app.randompick.databinding.LayoutItemBinding

class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val binding: LayoutItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun getBinding() = binding
    }

    var isFillFromHistory: Boolean = false
        private set
    private val ADD_ITEM = Seed(seedId = -1L, value = "")

    private val items = mutableListOf<Seed>(ADD_ITEM)
    private val checkItems = mutableMapOf<Int, Boolean>()

    private var recyclerView: RecyclerView? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.layout_item,
                parent,
                false
            )
        )
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val seed = items[position]
        with(holder.getBinding()) {
            if (seed.seedId == -1L) {
                input.onFocusChangeListener = null
                input.clearFocus()
                input.isVisible = false
                checkbox.isVisible = false
                delete.isVisible = false
                add.isVisible = true
                add.setOnClickListener {
                    performAdd()
                }
            } else {

                input.setText(seed.value)

                container.background = ContextCompat.getDrawable(root.context, if (input.hasFocus())R.drawable.shape_focus else R.drawable.s1)

                add.isVisible = false
                input.isVisible = true
                input.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus.not()) {
                        seed.value = input.editableText.toString()
                    }

                    //TransitionManager.beginDelayedTransition(holder.itemView as ViewGroup, Fade())
                    container.background = ContextCompat.getDrawable(root.context, if (hasFocus)R.drawable.shape_focus else R.drawable.s1)
                }
                checkbox.setOnCheckedChangeListener { v, isChecked ->
                    //log { "${recyclerView!!.findContainingViewHolder(v)!!.adapterPosition} $position, $isChecked" }
                    checkItems[recyclerView!!.findContainingViewHolder(v)!!.adapterPosition] = isChecked
                }

                delete.setOnClickListener {
                    items.removeAt(position)
                    notifyItemRemoved(position)
                }

                if (position == 0) {
                    input.requestFocus()
                    val imm = holder.itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(input, 0)
                }
            }
        }
    }

    private fun performAdd() {
        val newSeed = Seed(seedId = System.currentTimeMillis(), value = "")
        items.add(0, newSeed)
        notifyItemInserted(0)
    }

    fun fillFromGroup(groups: SeedGroups) {
        isFillFromHistory = true
        items.addAll(0, groups.seeds)
        log { "fillFromGroup : ${groups.seeds}" }
        if (debug) {
            AlertDialog.Builder(recyclerView!!.context).apply {
                setMessage("${groups.seeds}")
                show()
            }
        }
        notifyItemRangeInserted(0, itemCount)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return items[position].seedId
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    fun getSelectedList(): List<Seed> {
        recyclerView?.clearFocus()

        val new = mutableListOf<Seed>()
        /*new.addAll(items)
        new.removeIf { it.seedId == -1L }*/
        checkItems.keys.forEach {
            log { "checked position = $it" }
            if (checkItems[it] == true) {
                new.add(items[it])
            }
        }
        return new
    }

    @SuppressLint("NotifyDataSetChanged")
    fun reset() {
        items.removeAll { it.seedId != -1L }
        notifyDataSetChanged()
    }
}