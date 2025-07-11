package com.raffles.siasatuksw

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raffles.siasatuksw.databinding.ItemTranskripBinding
import com.raffles.siasatuksw.model.Transkrip

class TranskripAdapter(
    private val transkripList: List<Transkrip>
) : RecyclerView.Adapter<TranskripAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemTranskripBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTranskripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = transkripList[position]
        holder.binding.apply {
            tvKodeMataKuliah.text = item.mataKuliah.kode
            tvNamaMataKuliah.text = item.mataKuliah.nama
            tvSks.text = "${item.mataKuliah.sks} SKS"
            tvSemester.text = "Semester ${item.nilai.semester}"
            tvNilai.text = "${item.nilai.nilai}"
            tvGrade.text = item.nilai.grade
            tvTahunAkademik.text = item.nilai.tahunAkademik

            val backgroundColor = when (item.nilai.grade) {
                "A", "A-" -> android.graphics.Color.parseColor("#4CAF50")   // Green
                "B+", "B", "B-" -> android.graphics.Color.parseColor("#2196F3") // Blue
                "C+", "C", "C-" -> android.graphics.Color.parseColor("#FF9800") // Orange
                "D" -> android.graphics.Color.parseColor("#FF5722")         // Deep Orange
                else -> android.graphics.Color.parseColor("#F44336")        // Red
            }
            tvGrade.setBackgroundColor(backgroundColor)
        }
    }


    override fun getItemCount() = transkripList.size
}