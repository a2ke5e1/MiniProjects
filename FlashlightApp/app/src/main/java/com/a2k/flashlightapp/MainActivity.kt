package com.a2k.flashlightapp

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.a2k.flashlightapp.databinding.ActivityMainBinding
import com.google.android.material.color.DynamicColors


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraManager: CameraManager
    private var torchState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicColors.applyToActivityIfAvailable(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.navigationBarDividerColor =
            ContextCompat.getColor(this, android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setSupportActionBar(binding.toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }


        binding.button.isEnabled = this.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
        cameraManager = this.getSystemService(Context.CAMERA_SERVICE) as CameraManager


        binding.button.setOnClickListener {
            try {
                val cameraID = cameraManager.cameraIdList[0]
                if (!torchState) {
                    cameraManager.setTorchMode(cameraID, true)
                    torchState = true
                    (it as Button).text = getString(R.string.off)
                } else {
                    cameraManager.setTorchMode(cameraID, false)
                    torchState = false
                    (it as Button).text = getString(R.string.on)
                }
            } catch (e: Exception) {
                Toast.makeText(baseContext, "Failed to start flashlight", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val cameraID = cameraManager.cameraIdList[0]
        cameraManager.setTorchMode(cameraID, false)
        torchState = false
        binding.button.text = getString(R.string.on)
    }

}