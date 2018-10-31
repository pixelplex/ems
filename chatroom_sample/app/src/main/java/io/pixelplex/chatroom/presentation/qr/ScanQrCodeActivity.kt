package io.pixelplex.chatroom.presentation.qr

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.view.View
import com.google.zxing.Result
import io.pixelplex.chatroom.R
import io.pixelplex.chatroom.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_qr_code_scanner.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

/**
 * Presents qr code scanning feature
 *
 * Reads required qr code data and returns to source activity
 */
class ScanQrCodeActivity : BaseActivity(), ZXingScannerView.ResultHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_scanner)

        initFlash()
        initQrCodeScanner()

        ivBack.setOnClickListener { onBackPressed() }
    }

    private fun initFlash() {
        if (!hasFlash()) {
            ivFlashSwitch.visibility = View.GONE
        }
        ivFlashSwitch.setOnClickListener { toggleFlash() }
    }

    private fun toggleFlash() {
        scannerView.toggleFlash()
        val imageSource = if (scannerView.flash) R.drawable.ic_flash_on else R.drawable.ic_flash_off
        ivFlashSwitch.setImageResource(imageSource)
    }

    private fun initQrCodeScanner() {
        if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            )
        ) {
            scannerView.startCamera()
        } else {
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        val cameraPermission = Manifest.permission.CAMERA
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, cameraPermission)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSIONS
            )
        } else {
            showPermissionError()
        }
    }

    override fun onResume() {
        super.onResume()
        scannerView.startCamera()
        scannerView.setResultHandler(this)
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != REQUEST_CAMERA_PERMISSIONS) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scannerView.startCamera()
            return
        }

        showPermissionError()
    }

    private fun showPermissionError() {
        AlertDialog.Builder(this)
            .setTitle(R.string.error_dialog_title)
            .setMessage(R.string.error_no_camera_permission)
            .setPositiveButton(R.string.ok) { _, _ ->
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            .show()
    }

    override fun handleResult(result: Result?) {
        result?.let {
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(QR_CODE_RESULT, it.text)
            })

        } ?: setResult(Activity.RESULT_CANCELED)

        finish()
    }

    private fun hasFlash(): Boolean {
        return applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    companion object {
        const val REQUEST_CAMERA_PERMISSIONS = 120
        const val QR_CODE_RESULT = "QR_CODE_RESULT"
    }

}