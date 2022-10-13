package com.ram.photoselector.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ram.photoselector.activity.MainActivity
import com.ram.photoselector.databinding.ItemPhotoListBinding
import com.ram.photoselector.model.Photo


class PhotoAdapter(private val onRemoveListener: OnItemRemoveClickListener,private val activity: MainActivity) : RecyclerView.Adapter<PhotoViewHolder>() {

    private lateinit var binding: ItemPhotoListBinding
    private var photoList = mutableListOf<Photo>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        binding = ItemPhotoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photoList[position]
        binding.closeIcon.setOnClickListener {
            onRemoveListener.onItemClick(holder.adapterPosition,photoList[holder.adapterPosition])
        }
        holder.bind(photo)
    }

    fun setPhotoList(photo: List<Photo>) {
        this.photoList = photo.toMutableList()
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int = photoList.size


    interface OnItemRemoveClickListener {
        fun onItemClick(position: Int, photo: Photo)
    }

}
