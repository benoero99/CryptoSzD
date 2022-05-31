package com.example.cryptoszd.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptoszd.R
import java.util.*
import com.example.cryptoszd.ui.model.UiCertificateList

class CertificateAdapter(private val listener: CertificateItemClickListener) : RecyclerView.Adapter<CertificateAdapter.CertificateViewHolder>() {

    private var data: List<UiCertificateList> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateViewHolder {
        return CertificateViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.certificate, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: CertificateViewHolder, position: Int) =
        holder.bind(data[position])

    fun swapData(data: List<UiCertificateList>) {
        this.data = data
        notifyDataSetChanged()
    }

    interface CertificateItemClickListener {
        fun itemClicked(certificateID: String)
    }

    inner class CertificateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val commonNameTV: TextView = itemView.findViewById(R.id.commonNameTV)
        private val countryTV: TextView = itemView.findViewById(R.id.countryTV)
        private val localityTV: TextView = itemView.findViewById(R.id.localityTV)
        private val emailTV: TextView = itemView.findViewById(R.id.emailTV)

        fun bind(item: UiCertificateList) = with(itemView) {
            commonNameTV.text = item.commonName
            countryTV.text = item.countryCode
            localityTV.text = item.locality
            emailTV.text = item.email

            setOnClickListener {
                listener.itemClicked(item.certificateID)
            }
        }
    }
}