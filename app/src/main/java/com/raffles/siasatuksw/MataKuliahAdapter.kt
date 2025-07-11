package com.raffles.siasatuksw

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raffles.siasatuksw.databinding.ItemMataKuliahBinding
import com.raffles.siasatuksw.model.MataKuliah

class MataKuliahAdapter(
    private val mataKuliahList: List<MataKuliah>
) : RecyclerView.Adapter<MataKuliahAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemMataKuliahBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMataKuliahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mataKuliah = mataKuliahList[position]
        holder.binding.apply {
            tvKode.text = mataKuliah.kode
            tvNama.text = mataKuliah.nama
            tvSks.text = "${mataKuliah.sks} SKS"
            tvSemester.text = "Semester ${mataKuliah.semester}"
            tvDosenId.text = "Dosen: ${mataKuliah.dosenId}"
        }
    }

    override fun getItemCount() = mataKuliahList.size
}