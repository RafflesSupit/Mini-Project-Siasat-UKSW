package com.raffles.siasatuksw

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raffles.siasatuksw.databinding.ItemMataKuliahDosenBinding
import com.raffles.siasatuksw.model.MataKuliah

class MataKuliahDosenAdapter(
    private val mataKuliahList: List<MataKuliah>,
    private val onInputNilaiClick: (MataKuliah) -> Unit
) : RecyclerView.Adapter<MataKuliahDosenAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemMataKuliahDosenBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMataKuliahDosenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mataKuliah = mataKuliahList[position]
        holder.binding.apply {
            tvKode.text = mataKuliah.kode
            tvNama.text = mataKuliah.nama
            tvSks.text = "${mataKuliah.sks} SKS"
            tvSemester.text = "Semester ${mataKuliah.semester}"

            btnInputNilai.setOnClickListener {
                onInputNilaiClick(mataKuliah)
            }
        }
    }

    override fun getItemCount() = mataKuliahList.size
}