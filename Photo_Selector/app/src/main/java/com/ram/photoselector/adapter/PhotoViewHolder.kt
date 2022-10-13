package com.ram.photoselector.adapter

import androidx.recyclerview.widget.RecyclerView
import com.ram.photoselector.databinding.ItemPhotoListBinding
import com.ram.photoselector.model.Photo

class PhotoViewHolder(
    private val binding: ItemPhotoListBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(photos: Photo) {
        binding.photo = photos
    }
}