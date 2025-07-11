package com.raffles.siasatuksw

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raffles.siasatuksw.databinding.ItemNilaiBinding
import com.raffles.siasatuksw.model.Nilai

class NilaiAdapter(
    private val nilaiList: List<Nilai>,
    private val onItemClick: (Nilai) -> Unit
) : RecyclerView.Adapter<NilaiAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemNilaiBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNilaiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nilai = nilaiList[position]
        holder.binding.apply {
            tvMataKuliahId.text = nilai.mataKuliahId
            tvNilai.text = nilai.nilai.toString()
            tvHuruf.text = nilai.grade
            tvSemester.text = "Semester ${nilai.semester}"
            tvTahun.text = nilai.tahunAkademik

            root.setOnClickListener { onItemClick(nilai) }
        }
    }

    override fun getItemCount(): Int = nilaiList.size
}