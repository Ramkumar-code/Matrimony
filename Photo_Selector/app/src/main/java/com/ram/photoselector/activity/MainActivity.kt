package com.ram.photoselector.activity

import android.Manifest.permission.*
import android.app.Dialog
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ram.photoselector.R
import com.ram.photoselector.adapter.PhotoAdapter
import com.ram.photoselector.databinding.ActivityMainBinding
import com.ram.photoselector.databinding.BottomSheetDialogBinding
import com.ram.photoselector.databinding.CustomDialogBinding
import com.ram.photoselector.model.Photo
import com.ram.photoselector.viewModel.MainViewModel
import com.ram.photoselector.viewModelFactory.MainViewModelFactory
import java.io.File


class MainActivity : AppCompatActivity(), PhotoAdapter.OnItemRemoveClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomSheetDialogBinding: BottomSheetDialogBinding
    private lateinit var customDialogBinding: CustomDialogBinding
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var tempImageUri: Uri
    private var tempImageFilePath = ""
    private lateinit var viewModel: MainViewModel
    companion object {
        const val PICK_CAMERA_REQUEST_CODE = 1000
        const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 1001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.mainContainer.cardView.setOnClickListener {
            bottomSheetDialog()
        }
        binding.mainContainer.addPhoto.setOnClickListener {
            bottomSheetDialog()
        }
        viewModel = ViewModelProvider(this, MainViewModelFactory())[MainViewModel::class.java]
        initialiseAdapter()

    }

    private fun initialiseAdapter() {
        val recyclerViewPhotoLst = binding.mainContainer.photoLst
        recyclerViewPhotoLst.layoutManager = GridLayoutManager(this, 2)
        photoAdapter = PhotoAdapter(this,this)
        recyclerViewPhotoLst.adapter = photoAdapter
        observeData()
    }

    private fun observeData() {
        viewModel.lst.observe(this, Observer {
            photoAdapter.setPhotoList(it)
            if (it.size > 0) {
                binding.mainContainer.cardView.visibility = View.GONE
                binding.mainContainer.photoLst.visibility = View.VISIBLE
            } else {
                binding.mainContainer.cardView.visibility = View.VISIBLE
                binding.mainContainer.photoLst.visibility = View.GONE
            }
        })
    }

    private val filesChooserContract =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList ->
            if (viewModel.photolist.size < 6) {
                for (count in uriList.indices) {
                    if (viewModel.photolist.size < 6) {
                        val photo = Photo(
                            uriList[count]
                        )
                        viewModel.add(photo)
                    } else {
                        Toast.makeText(this, getString(R.string.limit_txt), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.limit_txt), Toast.LENGTH_SHORT).show()
            }

        }
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                if (viewModel.photolist.size < 6) {
                    val photo = Photo(
                        tempImageUri
                    )
                    viewModel.add(photo)
                } else {
                    Toast.makeText(this, getString(R.string.limit_txt), Toast.LENGTH_SHORT).show()
                }
            }

        }

    private fun createImageFile(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("temp_image", ".jpg", storageDir)
    }

    private fun pickImage() {
        if (ActivityCompat.checkSelfPermission(
                this,
                READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            filesChooserContract.launch("image/*")
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_REQUEST_CODE
            )
        }

    }

    private fun pickCamera() {
        if (ActivityCompat.checkSelfPermission(
                this,
                CAMERA
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            tempImageUri = FileProvider.getUriForFile(
                this,
                "com.ram.photoselector.provider",
                createImageFile().also {
                    tempImageFilePath = it.absolutePath
                })
            cameraLauncher.launch(tempImageUri)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE),
                PICK_CAMERA_REQUEST_CODE
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage()
                }
            }
            PICK_CAMERA_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tempImageUri = FileProvider.getUriForFile(
                        this,
                        "com.ram.photoselector.provider",
                        createImageFile().also {
                            tempImageFilePath = it.absolutePath
                        })
                    cameraLauncher.launch(tempImageUri)
                }
            }

        }
    }

    private fun bottomSheetDialog() {
        bottomSheetDialogBinding = BottomSheetDialogBinding.inflate(layoutInflater)

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetDialogBinding.root)
        bottomSheetDialogBinding.galleryLayout.setOnClickListener {
            pickImage()
            dialog.dismiss()
        }
        bottomSheetDialogBinding.cameraLayout.setOnClickListener {
            pickCamera()
            dialog.dismiss()

        }
        bottomSheetDialogBinding.whatsappLayout.setOnClickListener {
            openWhatsApp()
            dialog.dismiss()
        }
        dialog.setCancelable(true)
        dialog.show()
    }

    override fun onItemClick(position: Int, photo: Photo) {
        try {
            alertDialog(photo)
        } catch (ex: Exception) {
            ex.fillInStackTrace()
        }
    }

    private fun alertDialog(photo: Photo) {
        try {
            customDialogBinding = CustomDialogBinding.inflate(layoutInflater)
            val dialog = Dialog(this)
            dialog.setContentView(customDialogBinding.root)
            dialog.setCancelable(true)
            dialog.show()
            val mLayoutParams = WindowManager.LayoutParams()
            if (Build.VERSION.SDK_INT < 30) {
                val displayMetrics = Resources.getSystem().displayMetrics
                mLayoutParams.width = displayMetrics.widthPixels / 1
                mLayoutParams.height = displayMetrics.heightPixels / 3
            } else {
                val mDisplayMetrics = windowManager.currentWindowMetrics
                val mDisplayWidth = mDisplayMetrics.bounds.width()
                val mDisplayHeight = mDisplayMetrics.bounds.height()
                mLayoutParams.width = (mDisplayWidth * 1f).toInt()
                mLayoutParams.height = (mDisplayHeight * 0.25f).toInt()
            }
            //window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.window?.attributes = mLayoutParams
            customDialogBinding.positiveBtn.setOnClickListener {
                dialog.dismiss()
                Toast.makeText(this, getString(R.string.deleted_txt), Toast.LENGTH_SHORT).show()
                viewModel.remove(photo)
                photoAdapter.notifyDataSetChanged()


            }
            customDialogBinding.negativeBtn.setOnClickListener {
                dialog.dismiss()
            }
        } catch (ex: Exception) {
            ex.fillInStackTrace()
        }
    }

    private fun openWhatsApp() {
        try {
            val intent = this.packageManager.getLaunchIntentForPackage("com.whatsapp")
            startActivity(intent)
        } catch (e: NameNotFoundException) {
            Toast.makeText(this, getString(R.string.whatsapp_error_txt), Toast.LENGTH_SHORT).show()
        } catch (exception: NullPointerException) {
            exception.fillInStackTrace()
        }
    }
}





