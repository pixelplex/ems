package io.pixelplex.chatroom.data.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.echo.mobile.framework.model.Transaction

/**
 * Response on user registration call
 */
class AccountCreateResponse(
    @Expose
    val id: String,
    @SerializedName(KEY_BLOCK_NUM)
    val blockNum: Long,
    @SerializedName(KEY_TRX_NUM)
    val trxNum: Long,
    @Expose
    val trx: Transaction,
    @Expose
    val signatures: List<String>,
    @SerializedName(KEY_OPERATION_RESULTS)
    val operationResults: List<Any>
) {
    companion object {
        const val KEY_BLOCK_NUM = "block_num"
        const val KEY_TRX_NUM = "trx_num"
        const val KEY_OPERATION_RESULTS = "operation_results"
    }
}