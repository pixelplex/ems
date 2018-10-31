package io.pixelplex.chatroom.presentation.qr

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.ColorInt
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import io.pixelplex.chatroom.R
import io.pixelplex.chatroom.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_share_data.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Shares room's information as qr code that contains room name,
 * contract id and another participant name
 */
class ShareDataActivity : BaseActivity() {

    companion object {

        private const val EXTRA_CONTRACT_ID = "contract_id"
        private const val EXTRA_ROOM_NAME = "room_name"
        private const val EXTRA_USER_NAME = "user_name"

        /**
         * Configures intent for [ShareDataActivity] starting
         */
        fun newIntent(
            contractId: String, roomName: String, userName: String,
            context: Context
        ): Intent =
            Intent(context, ShareDataActivity::class.java).apply {
                putExtra(EXTRA_CONTRACT_ID, contractId)
                putExtra(EXTRA_ROOM_NAME, roomName)
                putExtra(EXTRA_USER_NAME, userName)
            }
    }

    private lateinit var contractId: String
    private lateinit var roomName: String
    private lateinit var userName: String

    private var qrCodeData = ""

    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_data)

        with(intent) {
            contractId = getStringExtra(EXTRA_CONTRACT_ID) ?: ""
            roomName = getStringExtra(EXTRA_ROOM_NAME) ?: ""
            userName = getStringExtra(EXTRA_USER_NAME) ?: ""
        }

        qrCodeData =
                "$contractId|$roomName|$userName"

        tvContractId.text = contractId

        ivCopyContractId.setOnClickListener {
            copyToClipboard(qrCodeData)
            Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()

        launch(UI) {
            if (bitmap == null) {
                bitmap = generate(qrCodeData, 200)
                ivQrCode.setImageBitmap(bitmap)
            }
        }
    }

    private fun generate(
        data: String,
        sideSize: Int, @ColorInt qrCodeColor: Int = Color.BLACK
    ): Bitmap? {
        val result: BitMatrix
        try {
            result = MultiFormatWriter().encode(
                data,
                BarcodeFormat.QR_CODE, sideSize, sideSize, null
            )

            val w = result.width
            val h = result.height
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                val offset = y * w
                for (x in 0 until w) {
                    pixels[offset + x] =
                            if (result.get(x, y)) qrCodeColor else Color.TRANSPARENT
                }
            }
            return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
                setPixels(pixels, 0, sideSize, 0, 0, w, h)
            }
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            return null
        } catch (e: WriterException) {
            e.printStackTrace()
            return null
        }

    }

    private fun Context.copyToClipboard(message: String) {
        val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("", message)
        clipboard.primaryClip = clip
    }

}