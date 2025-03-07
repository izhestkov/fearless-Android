package jp.co.soramitsu.core.storage

import jp.co.soramitsu.core.model.StorageEntry
import kotlinx.coroutines.flow.Flow
import java.math.BigInteger

interface StorageCache {

    suspend fun isPrefixInCache(prefixKey: String): Boolean

    suspend fun isFullKeyInCache(fullKey: String): Boolean

    suspend fun insert(entry: StorageEntry)

    suspend fun insert(entries: List<StorageEntry>)

    suspend fun observeEntry(key: String): Flow<StorageEntry>

    /**
     * Should be not empty
     */
    suspend fun observeEntries(keyPrefix: String): Flow<List<StorageEntry>>

    /**
     * Should suspend until any matched result found
     */
    suspend fun getEntry(key: String): StorageEntry

    /**
     * Should suspend until any matched result found
     * Thus, will not be empty
     */
    suspend fun getEntries(keyPrefix: String): List<StorageEntry>

    suspend fun currentRuntimeVersion(): BigInteger
}