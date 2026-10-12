package com.example.nfcreader

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(saved: Bundle?) {
        super.onCreate(saved)
        setContentView(R.layout.activity_main)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "此裝置不支援 NFC", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        val flags = NfcAdapter.FLAG_READER_NFC_A or 
                    NfcAdapter.FLAG_READER_NFC_B or 
                    NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
        nfcAdapter?.enableReaderMode(this, this, flags, null)
    }

    override fun onPause() {
        super.onPause()
        nfc_adapter?.disableReaderMode(this)
    }

    override fun onTagDiscovered(tag: Tag?) {
        tag?.let {
            val rawId = it.id
            val hexCardId = bytesToHexString(rawId)
            val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            runOnUiThread {
                Toast.makeText(this, "讀取卡號: $hexCardId", Toast.LENGTH_SHORT).show()
            }

            saveToCsv(currentTime, hexCardId)
        }
    }

    private fun bytesToHexString(src: ByteArray?): String {
        if (src == null || src.isEmpty()) return ""
        val sb = StringBuilder()
        for (b in src) {
            sb.append(String.format("%02X", b))
        }
        return sb.toString()
    }

    private fun saveToCsv(timestamp: String, cardId: String) {
        val file = File(getExternalFilesDir(null), "nfc_records.csv")
        val isNewFile = !file.exists()

        try {
            val writer = FileWriter(file, true)
            if (isNewFile) {
                writer.append("\uFEFF時間,卡號(UID)\n")
            }
            writer.append("$timestamp,$cardId\n")
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}