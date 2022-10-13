package com.ram.photoselector.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ram.photoselector.model.Photo

class MainViewModel : ViewModel() {
    var lst = MutableLiveData<ArrayList<Photo>>()
    var photolist = arrayListOf<Photo>()

    fun add(photo: Photo){
        photolist.add(photo)
        lst.value=photolist
    }

    fun remove(blog: Photo){
        photolist.remove(blog)
        lst.value=photolist
    }

}