package io.pixelplex.chatroom.presentation.addingroom.jointoroom

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import io.pixelplex.chatroom.R
import io.pixelplex.chatroom.presentation.base.BaseErrorActivity
import io.pixelplex.chatroom.presentation.main.MainActivity
import io.pixelplex.chatroom.presentation.qr.ScanQrCodeActivity
import io.pixelplex.chatroom.support.toast
import kotlinx.android.synthetic.main.activity_join_to_room.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Presents feature of joining existing chat room
 */
class JoinToRoomActivity : BaseErrorActivity() {

    private val viewModel by viewModel<JoinToRoomViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_to_room)

        btnSubmit.setOnClickListener {
            val contractId = etContractId.text.toString()
            if (contractId.isEmpty() || contractId.isBlank()) {
                tvError.setText(R.string.error_field_is_required)
            } else {
                showLoader()
                viewModel.join(
                    etContractId.text.toString(),
                    etRoomName.text.toString(),
                    etCompanionName.text.toString()
                )
            }
        }

        ivScanQrCode.setOnClickListener {
            startActivityForResult(
                Intent(this, ScanQrCodeActivity::class.java),
                REQUEST_QR_CODE_SCAN
            )
        }

        ivBack.setOnClickListener {
            super.onBackPressed()
        }

        viewModel.joined.observe(this, Observer {
            hideLoader()
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        })

        viewModel.error.observe(this, Observer { error ->
            error?.let {
                hideLoader()
                handleJoinToRoomException(it)
            }
        })

        etContractId.addTextChangedListener(textWatcher)
        etRoomName.addTextChangedListener(textWatcher)
        etCompanionName.addTextChangedListener(textWatcher)
    }

    private fun handleJoinToRoomException(exception: JoinToRoomException) {
        when (exception.type) {
            JoinToRoomException.Type.ACCOUNT_NOT_FOUND ->
                tvError.setText(R.string.error_account_not_found)

            JoinToRoomException.Type.JOIN_ERROR ->
                tvError.setText(R.string.error_join_error)

            JoinToRoomException.Type.WRONG_CONTRACT_ID ->
                tvError.setText(R.string.error_contract_join_error)

        }
    }

    private fun updateSubmitButtonEnabling() {
        btnSubmit.isEnabled =
                etContractId.text.isNotEmpty() &&
                etRoomName.text.isNotEmpty() &&
                etCompanionName.text.isNotEmpty()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_QR_CODE_SCAN && resultCode == Activity.RESULT_OK && data != null) {
            parseQrCode(data.getStringExtra(ScanQrCodeActivity.QR_CODE_RESULT))
        }
    }

    private fun parseQrCode(data: String) {
        val parts = data.split(QR_CODE_DELIMITER)

        if (parts.size != QR_CODE_PARTS_SIZE) {
            toast(R.string.wrong_qrcode_data_error)
            return
        }

        val contractId = parts[0]
        val roomName = parts[1]
        val companion = parts[2]

        etContractId.setText(contractId)
        etRoomName.setText(roomName)
        etCompanionName.setText(companion)
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            updateSubmitButtonEnabling()
        }
    }

    companion object {
        private const val REQUEST_QR_CODE_SCAN = 123
        private const val QR_CODE_DELIMITER = "|"

        private const val QR_CODE_PARTS_SIZE = 3
    }

}