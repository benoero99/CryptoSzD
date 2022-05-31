package com.example.cryptoszd.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptoszd.R
import com.example.cryptoszd.ui.model.UiPassword
import java.util.*

class PasswordAdapter(private val listener: PasswordItemClickListener) : RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder>() {

    var data = mutableListOf<UiPassword>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordViewHolder {
        return PasswordViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.password, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) =
        holder.bind(data[position])

    fun swapData(data: MutableList<UiPassword>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun addItem(item: UiPassword) {
        this.data.add(item)
        notifyItemInserted(itemCount)
    }

    fun removeItem(position: Int ) {
        data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, data.size)
    }

    fun updateItem(newWebsite: String, newPassword: String, position: Int) {
        val newDomainPassword = UiPassword(
            website = newWebsite,
            password = newPassword
        )
        data[position] = newDomainPassword
        notifyItemChanged(position)
    }

    interface PasswordItemClickListener {
        fun itemClicked(website: String, password: String, position: Int)
    }

    inner class PasswordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val websiteTV: TextView = itemView.findViewById(R.id.websiteTV)
        private val removeButton: ImageButton = itemView.findViewById(R.id.removePasswordButton)

        fun bind(item: UiPassword) = with(itemView) {
            websiteTV.text = context.getString(R.string.website_, item.website)

            removeButton.setOnClickListener {
                removeItem(absoluteAdapterPosition)
            }

            setOnClickListener {
                listener.itemClicked(item.website, item.password, absoluteAdapterPosition)
            }
        }
    }
}