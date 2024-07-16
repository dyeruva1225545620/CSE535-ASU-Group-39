package com.example.cm
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.GenericTypeIndicator
import android.widget.EditText
import android.widget.Toast
import android.os.Bundle
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference


class SleepActivity : AppCompatActivity() {

    private lateinit var input_sleephrs: EditText

    private lateinit var firebaseDatabase: DatabaseReference
    private lateinit var analyze_sleep: Button
    private lateinit var output_from_matlab: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep)


        output_from_matlab = findViewById(R.id.btnGetMatlabOutput)

        input_sleephrs = findViewById(R.id.input_sleephrs)
        analyze_sleep = findViewById(R.id.analyzeSleepButton)



        btn_click(R.id.goBackButton)
        {
            finish()
        }

        output_from_matlab.setOnClickListener {
            getFirebaseMatlabOutput()
        }

        firebaseDatabase = FirebaseDatabase.getInstance().reference.child("SleepData")


        analyze_sleep.setOnClickListener {
            sleep_analyzer()
        }

        output_from_matlab.setOnClickListener {
            getFirebaseMatlabOutput()
        }
    }
    fun btn_click(btID: Int, action:() -> Unit){
        findViewById<Button>(btID).setOnClickListener{
            action()
        }
    }
    private fun sleep_analyzer() {
        val hours = input_sleephrs.text.toString().toFloatOrNull()


        hours?.let {
            val sleepQuality = when {
                it < 4 -> "Low Sleep"
                it in 4.0..8.0 -> "Average Sleep"
                else -> "High Sleep"
            }


            uploadSleepData(it, sleepQuality)


            Toast.makeText(this, "Sleep_Quality: $sleepQuality", Toast.LENGTH_LONG).show()
        } ?: run {

            Toast.makeText(this, "Please enter valid sleep hours", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val SLEEP_DATA_KEY = "sleepData"
    }

    private fun prep_sleep_data(hrs: Float, quality: String): Map<String, Any> {
        return mapOf(
            "hoursSlept" to hrs,

            "sleepQuality" to quality
        )}




    private fun upload_to_firedb(sleepData: Map<String, Any>) {

        try {

            firebaseDatabase.child(SLEEP_DATA_KEY).setValue(sleepData)

                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        Toast.makeText(this, "Sleep Data Uploaded to db", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to upload data. Please check firebase", Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e:Exception){
            Toast.makeText(this@SleepActivity, "Issue uploading to firebase: ${e.message}", Toast.LENGTH_SHORT).show()

        }

    }


    private fun uploadSleepData(hrs: Float, quality: String) {
        // val sleepData = mapOf(
        //     "hoursSlept" to hours,
        //     "sleepQuality" to quality
        // )

        // // Use a specific key to set the value
        // firebaseDatabase.child(SLEEP_DATA_KEY).setValue(sleepData)
        //     .addOnCompleteListener { task ->
        //         if (task.isSuccessful) {
        //             Toast.makeText(this, "Sleep data uploaded to Firebase", Toast.LENGTH_SHORT).show()
        //         } else {
        //             Toast.makeText(this, "Failed to upload data to Firebase", Toast.LENGTH_SHORT).show()
        //         }
        //     }

        val sleep = prep_sleep_data(hrs, quality)
        upload_to_firedb(sleep)
    }




    private fun get_json(): DatabaseReference? {

        return try {
            FirebaseDatabase.getInstance().reference.child("resource")
        } catch (e: Exception) {
            Toast.makeText(this@SleepActivity, "Error initializing json name", Toast.LENGTH_SHORT).show()
            null
        }
    }


    private fun proc_data_snapshots(data_snapshot: DataSnapshot, out_ref: DatabaseReference) {
        try{
            if (data_snapshot.exists()) {
                val type_ind = object : GenericTypeIndicator<HashMap<String, Any>>() {}
                val out_map = data_snapshot.getValue(type_ind)

                val msg = out_map?.get("message")?.toString()
                Toast.makeText(this@SleepActivity, msg ?: "No message available", Toast.LENGTH_LONG).show()

                out_ref.removeValue()
            } else {
                Toast.makeText(this@SleepActivity, "No output available. Run MATLAB code.", Toast.LENGTH_LONG).show()
            }}catch(e:Exception){

            Toast.makeText(this@SleepActivity, "Error processing data snapshots", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getFirebaseMatlabOutput() {
        val out_ref = get_json() ?:return

        try{

            out_ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(data_snapshot: DataSnapshot) {
                    proc_data_snapshots(data_snapshot, out_ref)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@SleepActivity, "Error reading from Firebase", Toast.LENGTH_SHORT).show()
                }
            })
        }
        catch(e:Exception){
            Toast.makeText(this@SleepActivity, "Error getting output in matlab", Toast.LENGTH_SHORT).show()
        }

    }
}
