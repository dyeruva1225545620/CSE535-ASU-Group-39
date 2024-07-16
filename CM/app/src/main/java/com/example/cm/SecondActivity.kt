package com.example.cm

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import android.util.Log
import com.google.firebase.database.GenericTypeIndicator

import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SecondActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var sleepHoursInput: EditText
    private lateinit var analyzeSleepButton: Button
    private lateinit var firebaseDatabase: DatabaseReference // Firebase reference
    private var sleepQuality: Int = 0

    val symptoms = arrayOf(
        "Nausea",
        "Headache",
        "diarrhea",
        "Soar Throat",
        "Fever",
        "Muscle Ache",
        "Loss of smell or taste",
        "Cough",
        "Shortness of Breath",
        "Feeling tired"

    )

    private val symptomRatings = HashMap<String, Int>()

    fun buttonClick(btID: Int, action:() -> Unit){
        findViewById<Button>(btID).setOnClickListener{
            action()
        }
    }

    private fun setUpSpinner(){
        val spn = findViewById<Spinner>(R.id.symptomsSpinner)
        val rtBar = findViewById<RatingBar>(R.id.ratingBar)
        spn.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, symptoms)

        spn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                rtBar.rating = 0f
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSymptom = symptoms[position]
                rtBar.rating = symptomRatings[selectedSymptom]?.toFloat() ?: 0f
            }
        }

    }

    private fun setUprtBar(){
        val spn = findViewById<Spinner>(R.id.symptomsSpinner)
        val rtBar = findViewById<RatingBar>(R.id.ratingBar)
        rtBar.setOnRatingBarChangeListener { _, rating, _ ->
            val currentSymptom = spn.selectedItem.toString()
            symptomRatings[currentSymptom] = rating.toInt()
        }


    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val hRate = intent.getStringExtra("HeartRate") ?: "0"
        val respRate = intent.getStringExtra("RespiratoryRate") ?: "0"
        val msg = "Heart Rate: $hRate, Respiratory Rate: $respRate"
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        db = DBHelper(this)
        val spn = findViewById<Spinner>(R.id.symptomsSpinner)
        val rtBar = findViewById<RatingBar>(R.id.ratingBar)
        setUpSpinner()
        setUprtBar()
        firebaseDatabase = FirebaseDatabase.getInstance().reference.child("SymptomsData")

        buttonClick(R.id.uploadButton) {
            val selectedSymptom = spn.selectedItem.toString()
            val selectedRating = rtBar.rating.toInt()


            if (isNumeric(hRate) && isNumeric(respRate)) {
                saveToDatabase(selectedSymptom, selectedRating, hRate, respRate)
                symptomRatings.clear()
            } else {
                Toast.makeText(this, "Invalid heart rate or respiratory rate.", Toast.LENGTH_LONG).show()
            }
        }

        // Initialize the views
        sleepHoursInput = findViewById(R.id.sleepHoursInput)
        analyzeSleepButton = findViewById(R.id.analyzeSleepButton)


        // Set up the button click listener
        analyzeSleepButton.setOnClickListener {
            analyzeSleep()
        }

        buttonClick(R.id.goBackButton)
        {
            finish()
        }

        val btnGetOutput = findViewById<Button>(R.id.btnGetOutput)
        btnGetOutput.setOnClickListener {
            getFirebaseOutput()
        }


    }



    private fun getFirebaseOutput() {
        val outputReference = FirebaseDatabase.getInstance().reference.child("resource")

        outputReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Define the type indicator for a HashMap
                    val typeIndicator = object : GenericTypeIndicator<HashMap<String, Any>>() {}
                    val outputMap = dataSnapshot.getValue(typeIndicator)

                    if (outputMap.isNullOrEmpty()) {
                        Toast.makeText(this@SecondActivity, "No output available. Run MATLAB code.", Toast.LENGTH_LONG).show()
                    } else {
                        // Assuming there is a key named "message" in your HashMap
                        val message = outputMap["message"]?.toString()
                        if (message.isNullOrEmpty()) {
                            Toast.makeText(this@SecondActivity, "Message is empty.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@SecondActivity, message, Toast.LENGTH_LONG).show()
                            // Remove the data from Firebase after displaying the message
                            outputReference.removeValue()
                        }
                    }
                } else {
                    Toast.makeText(this@SecondActivity, "No output available. Run MATLAB code.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@SecondActivity, "Error reading from Firebase", Toast.LENGTH_SHORT).show()
            }
        })
    }







    fun isNumeric(str: String): Boolean = str.toDoubleOrNull() != null
    private fun entryValues(sympName : String, rt : Int, hRate : String?, respRate: String? ): ContentValues{
        return ContentValues().apply {
            put ("timestamp", System.currentTimeMillis())

            symptoms.forEach {
                sym ->
                val k = sym.replace(" ","_")
                val v = if (sym == sympName) rt else symptomRatings[sym]?:0
                put(k,v)
            }
            hRate?.toIntOrNull()?.let {
                put("Heart_Rate", it)
            }

            respRate?.toIntOrNull()?.let {
                put("Respiratory_Rate",it)
            }
            put("Sleep_Quality", sleepQuality)
    }
    }
    private fun analyzeSleep() {
        val hours = sleepHoursInput.text.toString().toFloatOrNull()

        // Analyze sleep based on the hours entered and assign an integer value
        sleepQuality = when {
            hours == null -> 0 // Default case for invalid input
            hours < 6 -> 1 // Low sleep
            hours in 6.0..8.0 -> 2 // Average sleep
            else -> 3 // High sleep
        }

        // Display the result using the new integer values
        val sleepQualityText = when(sleepQuality) {
            1 -> "Low Sleep"
            2 -> "Average Sleep"
            3 -> "High Sleep"
            else -> "Invalid Input"
        }
        Toast.makeText(this, "Sleep Quality: $sleepQualityText", Toast.LENGTH_LONG).show()
    }
//    private fun saveToDatabase(symptomName: String, rating: Int, hRate: String?, respRate: String?) {
//        val db = db.writableDatabase
//
//        val eValues = entryValues(symptomName,rating,hRate,respRate)
//
//        try {
//            db.insertOrThrow("Symptoms", null, eValues)
//
//            Toast.makeText(this, "Symptoms saved successfully.", Toast.LENGTH_SHORT).show()
//        } catch (exp: Exception) {
//
//            exp.printStackTrace()
//            Toast.makeText(this, "Error saving symptoms: ${exp.localizedMessage}", Toast.LENGTH_LONG).show()
//        }
//    }

    private fun saveToFirebase(symptomName: String, rating: Int, hRate: String?, respRate: String?) {
        // Create a new map for the values to be saved in Firebase
        val dataMap = hashMapOf<String, Any?>(
//            "symptom" to symptomName,
            "rating" to rating,
            "heartRate" to hRate?.toIntOrNull(),
            "respiratoryRate" to respRate?.toIntOrNull(),
            "sleepQuality" to sleepQuality
        )

        // Push the data to Firebase Database
        firebaseDatabase.push().setValue(dataMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Data saved to Firebase", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to save data to Firebase", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveToDatabase(symptomName: String, rating: Int, hRate: String?, respRate: String?) {
        val db = db.writableDatabase
        val eValues = entryValues(symptomName, rating, hRate, respRate)
        try {
            db.insertOrThrow("Symptoms", null, eValues)
            saveToFirebase(symptomName, rating, hRate, respRate) // Call Firebase save after local save
            Toast.makeText(this, "Symptoms saved successfully.", Toast.LENGTH_SHORT).show()
        } catch (exp: Exception) {
            exp.printStackTrace()
            Toast.makeText(this, "Error saving symptoms: ${exp.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }


    private class DBHelper(context: AppCompatActivity) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            const val DATABASE_VERSION = 7
            const val DATABASE_NAME = "SymptomsDB.db"
            private const val create = """
                
        CREATE TABLE Symptoms (
            id INTEGER PRIMARY KEY,timestamp INTEGER,
            Nausea INTEGER,Headache INTEGER,
            Diarrhea INTEGER,Soar_Throat INTEGER,
            Fever INTEGER,Muscle_Ache INTEGER,
            Loss_of_smell_or_taste INTEGER,
            Cough INTEGER,Shortness_of_Breath INTEGER,
            Feeling_tired INTEGER,
            Heart_Rate INTEGER,Respiratory_Rate INTEGER,
            Sleep_Quality INTEGER
            
        )
    """
            private const val delete = "DROP TABLE IF EXISTS Symptoms"
        }
        override fun onCreate(db: SQLiteDatabase) {
            try{
            db.execSQL(create)
        }
        catch(exp:Exception){
            exp.printStackTrace()
        }}

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            try{ db.execSQL(delete)
            onCreate(db)
        }
        catch(exp:Exception){exp.printStackTrace()
        }

}}}
