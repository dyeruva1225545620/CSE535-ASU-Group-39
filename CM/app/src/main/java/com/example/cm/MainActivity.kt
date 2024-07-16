package com.example.cm

import android.content.Intent
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import android.graphics.Bitmap
import android.widget.Toast
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {


    lateinit var pB: ProgressBar
    lateinit var pbText: TextView
    private var accX = FloatArray(1280)
    private var accY = FloatArray(1280)
    private var accZ = FloatArray(1280)
    lateinit var heartRateRes: TextView
    lateinit var respRes: TextView
    lateinit var uploadBt: Button
    lateinit var goToThirdActivityButton: Button
    lateinit var sleepActivityButton: Button


    fun buttonClick(btID: Int, action:() -> Unit){
        findViewById<Button>(btID).setOnClickListener{
            action()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        heartRateRes = findViewById(R.id.heartRateRes)
        respRes = findViewById(R.id.respiratoryRateResult)
        pB = findViewById(R.id.progressBar)
        pbText = findViewById(R.id.percentageTextView)



        buttonClick(R.id.respiratoryRateButton) {
            Log.d("RespiratoryCalc", "Total changes: ")
            processRespiratoryRate()
        }

        buttonClick(R.id.heartRateButton) {
            val video = Uri.parse("android.resource://${packageName}/${R.raw.heart}")
            processHeartRate(video)
        }

        buttonClick(R.id.btnNavigateToSymptoms) {
            val intent = Intent(this, SecondActivity::class.java)

            val heartRateText = heartRateRes.text.toString()
            val respiratoryRateText = respRes.text.toString()














            fun String.extHeartRate(): String {
                return if (this.contains("Heart Rate")){
                    this.split(" ")[2]
                }else {"0"}
            }


            fun String.extRespRate(): String {
                return if ( this.contains("Respiratory Rate")){
                    this.split(" ")[2]

                }else {"0"}
            }

            val hRate = heartRateText.extHeartRate()
            intent.putExtra("HeartRate",hRate)

            val respRate = respiratoryRateText.extRespRate()
            intent.putExtra("RespiratoryRate",respRate)


            startActivity(intent)
        }

        buttonClick(R.id.goToThirdActivityButton) {
            val intent = Intent(this, TrafficAnalysisActivity::class.java)
            startActivity(intent)
        }


//        buttonClick(R.id.uploadButton) {
//            uploadDataToDatabase()
//        }

        buttonClick(R.id.btnNavigateToSleepActivity) {
            val intent = Intent(this, SleepActivity::class.java)
            startActivity(intent)
        }

        buttonClick(R.id.btnNavBMI) {
            val intent = Intent(this, HostActivity::class.java)
            startActivity(intent)
        }
        buttonClick(R.id.btnNavCAM) {
            val intent = Intent(this, VideoCaptureActivity::class.java)
            startActivity(intent)
        }
//        val sleepActivityButton: Button = findViewById(R.id.btnNavigateToSleepActivity)
//        sleepActivityButton.setOnClickListener {
//            Log.d("MainActivity", "Navigating to SleepActivity")
//            Toast.makeText(this, "Navigating to SleepActivity", Toast.LENGTH_SHORT).show()
//            val intent = Intent(this, SleepActivity::class.java)
//            startActivity(intent)
//        }


        val getFirebaseDataButton: Button = findViewById(R.id.btnGetFirebaseData)
        getFirebaseDataButton.setOnClickListener {
            getFirebaseData()
        }


    }

    private fun uploadDataToDatabase() {
        val heartRateText = heartRateRes.text.toString()
        val respiratoryRateText = respRes.text.toString()

        var hRate: String? = null
        var respRate: String? = null

        if (heartRateText.contains("Heart Rate")) {
            hRate = heartRateText.split(" ")[2]
        }

        if (respiratoryRateText.contains("Respiratory Rate")) {
            respRate = respiratoryRateText.split(" ")[2]
        }

        if(hRate == null || respRate == null) {
            Toast.makeText(this, "Make sure both heart and respiratory rates are calculated before uploading.", Toast.LENGTH_LONG).show()
            return
        }
        Toast.makeText(this, "Data upload success!", Toast.LENGTH_SHORT).show()
    }

    private fun processHeartRate(video: Uri) {
        GlobalScope.launch(Dispatchers.IO) {

                val rst = runCatching {calculateHeartRate(video)}
                withContext(Dispatchers.Main) {
                    heartRateRes.text = when (val rt = rst.getOrNull()){
                        null -> "Error getting heart rate."
                        else -> "Heart Rate: $rt BPM"
                    }
                    if (rst.isFailure) {
                    Log.e("HeartRateError", "Error getting heart rate val", rst.exceptionOrNull())


                }

                    pB.progress = 100
                    pbText.text = "100%"
                }
            }
            }



    private suspend fun updateProgress(current: Int, total: Int) {
        val progress = (current.toFloat() / total) * 100
        withContext(Dispatchers.Main) {
            pB.progress = progress.toInt()
            pbText.text = "${progress.toInt()}%"
        }
    }

    private fun processRespiratoryRate() {
        GlobalScope.launch(Dispatchers.IO) {
            val rst = runCatching {
                readAccelerometerDataFromCSV()
                respRateCalculator() }


            withContext(Dispatchers.Main) {
                    respRes.text = when ( val rt = rst.getOrNull()){
                        null -> "Error occurred during processing."
                        else -> "Respiratory Rate: $rt breaths/min" }
                    if (rst.isFailure) {
                        Toast.makeText(
                            this@MainActivity,
                            "Error getting respiratory rate val: ${rst.exceptionOrNull()?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("RespiratoryRateError", "Error getting respiratory rate val", rst.exceptionOrNull())
                    }

                        pB.progress = 100
                    pbText.text = "100%"
                }
            }
            }




    private fun readAccelerometerDataFromCSV() {
        val csv = resources.openRawResource(R.raw.resp)
        try {
            var totalLines = 0
            csv.bufferedReader().useLines { lines ->
                var idx = 0
                lines.forEach { line ->
                    if(idx < 1280) {
                        totalLines+=1
                        try {
                            accX[idx] = line.trim().toFloat()
                            idx++
                        } catch (e: NumberFormatException) {
                            Log.e("DataReading", "Error because of float on line $totalLines: $line")
                        }
                    }
                    GlobalScope.launch { updateProgress(idx, 1280) }
                }
            }
            Log.d("DataReading", "Total entries from CSV: $totalLines")
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error reading CSV data", e)
        }
    }



    private fun respRateCalculator(): Int {
        var prevVal = 0f
        var k = 0

        for (i in 11..450) {
            val curr = sqrt(accX[i].toDouble().pow(2.0)).toFloat()
            if (abs(prevVal - curr) > 0.04) {
                k++
            }
            prevVal = curr
        }

        val ret = (k / 45.00)



//        GlobalScope.launch(Dispatchers.Main) {
//            showToast("Value of k: $k")
//        }

        return (ret * 30).toInt()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private suspend fun calculateHeartRate(uri: Uri): String? {
        val retriever = MediaMetadataRetriever()
        val frameL = ArrayList<Bitmap>()
        try {
            retriever.setDataSource(this@MainActivity, uri)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT)
            val aduration = duration?.toInt() ?: 0
            var i = 10
            while (i < aduration) {
                val bitmap = retriever.getFrameAtIndex(i)
                if (bitmap != null) {
                    frameL.add(bitmap)
                }
                i += 5

                updateProgress(i, aduration)
            }
        } catch (m_e: Exception) {
            Log.e("HeartRateError", "Error getting frames", m_e)
            return null
        } finally {
            retriever.release()
        }

        val a = mutableListOf<Long>()
        val fn = frameL.map { i ->
            var rb = 0L
            val bitmapWidth = i.width
            val bitmapHeight = i.height

            for (y in 500.coerceAtMost(bitmapHeight - 1) until 700.coerceAtMost(bitmapHeight)) {
                for (x in 500.coerceAtMost(bitmapWidth - 1) until 700.coerceAtMost(bitmapWidth)) {
                    val c: Int = i.getPixel(x, y)
                    rb += (Color.red(c) + Color.blue(c) + Color.green(c))
                }
            }
            a.add(rb)
        }

        val b = mutableListOf<Long>()
        for (i in 0 until a.lastIndex - 5) {
            val temp = (a[i] + a[i + 1] + a[i + 2] + a[i + 3] + a[i + 4]) / 5
            b.add(temp)
        }

        var x = b[0]
        var count = 0
        for (i in 1 until b.lastIndex) {
            val p = b[i.toInt()]
            if ((p - x) > 2000) {
                count++
            }
            x = b[i.toInt()]
        }

        val rate = ((count.toFloat() / 45) * 60).toInt()
        return (rate / 3).toString()
    }

    private fun get_json(): DatabaseReference? {

        return try {
            FirebaseDatabase.getInstance().reference.child("resource")
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, "Error initializing json name", Toast.LENGTH_SHORT).show()
            null
        }
    }

//    private fun getFirebaseData() {
//        val firebaseDatabase = FirebaseDatabase.getInstance().getReference("resource")
//
//        firebaseDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    val data = dataSnapshot.getValue(String::class.java)
//                    Toast.makeText(this@MainActivity, "Data from Firebase: $data", Toast.LENGTH_LONG).show()
//                } else {
//                    Toast.makeText(this@MainActivity, "No data available", Toast.LENGTH_LONG).show()
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Toast.makeText(this@MainActivity, "Error fetching data: ${databaseError.message}", Toast.LENGTH_LONG).show()
//            }
//        })
//    }

    private fun proc_data_snapshots(data_snapshot: DataSnapshot, out_ref: DatabaseReference) {
        try{
            if (data_snapshot.exists()) {
                val type_ind = object : GenericTypeIndicator<HashMap<String, Any>>() {}
                val out_map = data_snapshot.getValue(type_ind)

                val msg = out_map?.get("message")?.toString()
                Toast.makeText(this@MainActivity, msg ?: "No message available", Toast.LENGTH_LONG).show()

                out_ref.removeValue()
            } else {
                Toast.makeText(this@MainActivity, "No output available. Run MATLAB code.", Toast.LENGTH_LONG).show()
            }}catch(e:Exception){

            Toast.makeText(this@MainActivity, "Error processing data snapshots", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getFirebaseData() {
        val out_ref = get_json() ?:return

        try{

            out_ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(data_snapshot: DataSnapshot) {
                    proc_data_snapshots(data_snapshot, out_ref)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@MainActivity, "Error reading from Firebase", Toast.LENGTH_SHORT).show()
                }
            })
        }
        catch(e:Exception){
            Toast.makeText(this@MainActivity, "Error getting output in matlab", Toast.LENGTH_SHORT).show()
        }

    }


}
