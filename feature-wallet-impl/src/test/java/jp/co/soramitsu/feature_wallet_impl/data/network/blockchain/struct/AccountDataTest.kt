package jp.co.soramitsu.feature_wallet_impl.data.network.blockchain.struct

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import io.emeraldpay.polkaj.scale.ScaleCodecWriter
import jp.co.soramitsu.feature_wallet_impl.data.network.blockchain.struct.account.AccountData
import jp.co.soramitsu.feature_wallet_impl.data.network.blockchain.struct.account.AccountInfoSchemaV27
import junit.framework.Assert.assertEquals
import org.bouncycastle.util.encoders.Hex
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.io.ByteArrayOutputStream

@RunWith(MockitoJUnitRunner::class)
class AccountDataTest {
    @Test
    fun `should decode and encode account info`() {
        val response = "3a00000002000000c0fc292a7b01000000000000000000000000000000000000000000000000000000e40b5402000000000000000000000000e40b54020000000000000000000000"
        val actualBalance = 1628499999936.toBigInteger()
        val actualRefCount = 58.toUInt()

        val decode = Hex.decode(response)

        val reader = ScaleCodecReader(decode)

        val struct = AccountInfoSchemaV27.read(reader)

        val balanceInPlanks = struct[AccountInfoSchemaV27.data][AccountData.free]
        val nonce = struct[AccountInfoSchemaV27.nonce]

        assert(balanceInPlanks == actualBalance)
        assert(nonce == actualRefCount)

        val outputStream = ByteArrayOutputStream()
        val writer = ScaleCodecWriter(outputStream)
        writer.write(AccountInfoSchemaV27, struct)
        val bytes = outputStream.toByteArray()

        val encodedResponse = Hex.toHexString(bytes)

        assertEquals(response, encodedResponse)
    }
}