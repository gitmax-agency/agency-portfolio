package com.hypelist.data.repositories

import android.app.Application
import android.graphics.Bitmap
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.util.DisplayMetrics
import com.hypelist.data.extensions.tryScaleForWidth
import com.hypelist.domain.common.BaseRepository
import io.realm.Realm
import io.realm.RealmConfiguration
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.SecureRandom
import java.util.Calendar
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.security.auth.x500.X500Principal


open class BaseRepositoryImpl(private val application: Application) : BaseRepository {

    private fun cachedPath(filename: String): String {
        if (!File(cacheDir()).exists())
            File(cacheDir()).mkdir()

        return cacheDir() + File.separator + filename
    }

    private fun cacheDir(): String = application.filesDir.absolutePath + File.separator + "CachedAttachments"

    private val key: ByteArray by lazy {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        if (
            keyStore.containsAlias("EncryptedCache")
            && File(cachedPath("cache_key")).exists()
        ) {
            decryptRSA("cache_key")
        } else {
            val kg = KeyGenerator.getInstance("AES")
            kg.init(256, randomInstance())

            val secretKey: SecretKey = kg.generateKey()
            val secretKey2: SecretKey = kg.generateKey()
            val realmKey = ByteArray(64)
            for (i in 0 until 32) {
                realmKey[i] = secretKey.encoded[i]
                realmKey[i + 32] = secretKey2.encoded[i]
            }

            encryptWithRSA(realmKey, "cache_key")
            realmKey
        }
    }


    init {
        val realmKey = key

        Realm.init(application)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .encryptionKey(realmKey)
            .build())
    }

    private fun encryptWithRSA(payload: ByteArray, filename: String) {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        if (!keyStore.containsAlias("EncryptedCache")) {
            val notBefore = Calendar.getInstance()
            val notAfter = Calendar.getInstance()
            notAfter.add(Calendar.YEAR, 1)
            val spec = KeyPairGeneratorSpec.Builder(application)
                .setAlias("EncryptedCache")
                //.setKeyType(KeyProperties.KEY_ALGORITHM_RSA)
                .setKeySize(2048)
                .setSubject(X500Principal("CN=test"))
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(notBefore.time)
                .setEndDate(notAfter.time)
                .build()
            val generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore")
            generator.initialize(spec)
            generator.generateKeyPair()
        }

        val privateKeyEntry = keyStore.getEntry("EncryptedCache", null) as KeyStore.PrivateKeyEntry
        val publicKey = privateKeyEntry.certificate.publicKey //as RSAPublicKey

        val inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidKeyStoreBCWorkaround")
        inCipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val encryptedDataFilePath = cachedPath(filename)

        val cipherOutputStream = CipherOutputStream(FileOutputStream(encryptedDataFilePath), inCipher)
        cipherOutputStream.write(payload)
        cipherOutputStream.close()
    }

    private fun decryptRSA(filename: String): ByteArray {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        val privateKeyEntry = keyStore.getEntry("EncryptedCache", null) as KeyStore.PrivateKeyEntry
        val privateKey = privateKeyEntry.privateKey //as RSAPrivateKey

        val outCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidKeyStoreBCWorkaround")
        outCipher.init(Cipher.DECRYPT_MODE, privateKey)

        val encryptedDataFilePath = cachedPath(filename)

        val cipherInputStream = CipherInputStream(FileInputStream(encryptedDataFilePath), outCipher)
        val roundTrippedBytes = ArrayList<Byte>() //ByteArray(500)

        var next = cipherInputStream.read()
        while (next != -1) {
            roundTrippedBytes.add(next.toByte())
            next = cipherInputStream.read()
        }

        return roundTrippedBytes.toByteArray()
    }

    private fun randomInstance(): SecureRandom {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SecureRandom.getInstanceStrong()
        } else {
            SecureRandom()
        }
    }

    override suspend fun cacheHypelistCover(coverImage: Bitmap, coverFile: File) {
        coverFile.createNewFile()

        val fos = FileOutputStream(coverFile)
        val bos = BufferedOutputStream(fos, 1024)
        coverImage.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        bos.flush()
        bos.close()
        fos.close()
    }

    override suspend fun cacheSmallHypelistCover(coverImage: Bitmap, coverFile: File) {
        val displayMetrics: DisplayMetrics = application.resources.displayMetrics
        val smallCover = coverImage.tryScaleForWidth(displayMetrics.widthPixels)

        coverFile.createNewFile()

        val fos = FileOutputStream(coverFile)
        val bos = BufferedOutputStream(fos, 1024)
        smallCover.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        bos.flush()
        bos.close()
        fos.close()
    }

    override suspend fun cacheHypelistItemCover(
        hypelistID: String, hypelistItemID: String, cover: Bitmap
    ) {
        val cacheDir = application.filesDir.absolutePath + "/CachedHypelists"
        if (!File(cacheDir).exists()) {
            File(cacheDir).mkdir()
        }

        val hypelistCacheDir = "$cacheDir/Hypelist_$hypelistID"
        if (!File(hypelistCacheDir).exists()) {
            File(hypelistCacheDir).mkdir()
        }

        val hypelistItemsCacheDir = "$cacheDir/Hypelist_$hypelistID/Items"
        if (!File(hypelistItemsCacheDir).exists()) {
            File(hypelistItemsCacheDir).mkdir()
        }

        val hypelistItemCacheDir = "$cacheDir/Hypelist_$hypelistID/Items/Item_$hypelistItemID"
        if (!File(hypelistItemCacheDir).exists()) {
            File(hypelistItemCacheDir).mkdir()
        }

        val coverFile = File("$hypelistItemCacheDir/HypelistItemCover.jpg")
        coverFile.createNewFile()

        val fos = FileOutputStream(coverFile)
        val bos = BufferedOutputStream(fos, 1024)
        cover.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        bos.flush()
        bos.close()
        fos.close()
    }

    override suspend fun cacheSmallHypelistItemCover(
        hypelistID: String, hypelistItemID: String, cover: Bitmap
    ) {
        val cacheDir = application.filesDir.absolutePath + "/CachedHypelists"
        if (!File(cacheDir).exists()) {
            File(cacheDir).mkdir()
        }

        val hypelistCacheDir = "$cacheDir/Hypelist_$hypelistID"
        if (!File(hypelistCacheDir).exists()) {
            File(hypelistCacheDir).mkdir()
        }

        val hypelistItemsCacheDir = "$cacheDir/Hypelist_$hypelistID/Items"
        if (!File(hypelistItemsCacheDir).exists()) {
            File(hypelistItemsCacheDir).mkdir()
        }

        val hypelistItemCacheDir = "$cacheDir/Hypelist_$hypelistID/Items/Item_$hypelistItemID"
        if (!File(hypelistItemCacheDir).exists()) {
            File(hypelistItemCacheDir).mkdir()
        }

        val displayMetrics: DisplayMetrics = application.resources.displayMetrics
        val smallCover = cover.tryScaleForWidth((50 * displayMetrics.density).toInt())

        val coverFile = File("$hypelistItemCacheDir/SmallHypelistItemCover.jpg")
        coverFile.createNewFile()

        val fos = FileOutputStream(coverFile)
        val bos = BufferedOutputStream(fos, 1024)
        smallCover.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        bos.flush()
        bos.close()
        fos.close()
    }
}