package com.example.cm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import android.content.Intent


class Project5Task1 : Fragment() {

    private lateinit var weightEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var calculateButton: Button
    private lateinit var goBackButton: Button
    private lateinit var firebaseDatabase: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.project5_task1, container, false)

        // Initializing views
        weightEditText = view.findViewById(R.id.editText1)
        heightEditText = view.findViewById(R.id.editText2)
        calculateButton = view.findViewById(R.id.calculateButton)
        goBackButton = view.findViewById(R.id.goBackButton)

        // Initialize Firebase database reference
        firebaseDatabase = FirebaseDatabase.getInstance().reference.child("BMIRecords")

        calculateButton.setOnClickListener {
            calculateAndUploadBMI()
        }

        goBackButton.setOnClickListener {
            // Assuming you have a MainActivity to go back to
            // Replace 'MainActivity::class.java' with your main activity class
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun calculateAndUploadBMI() {
        val weightStr = weightEditText.text.toString()
        val heightStr = heightEditText.text.toString()

        if (weightStr.isNotEmpty() && heightStr.isNotEmpty()) {
            val weight = weightStr.toFloat()
            val height = heightStr.toFloat() / 100

            val bmi = calculateBMIValue(weight, height)
            val resultMessage = "Your BMI is: $bmi"
            val healthCategory = getHealthCategory(bmi)

            // Show BMI and category
            Toast.makeText(requireActivity(), "$resultMessage\nYou belong to the category: $healthCategory", Toast.LENGTH_SHORT).show()

            // Upload to Firebase
            uploadDataToFirebase(weight, height, bmi, healthCategory)
        } else {
            Toast.makeText(requireActivity(), "Please enter both weight and height", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateBMIValue(weight: Float, height: Float): Float {
        return weight / (height * height)
    }

    private fun getHealthCategory(bmi: Float): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 24.9 -> "Normal weight"
            bmi < 29.9 -> "Overweight"
            bmi < 34.9 -> "Obesity (Class 1)"
            bmi < 39.9 -> "Obesity (Class 2)"
            else -> "Obesity (Class 3)"
        }
    }
    companion object {
        private const val BMI_DATA_KEY = "BMIData"
    }
    private fun uploadDataToFirebase(weight: Float, height: Float, bmi: Float, category: String) {
        val bmiData = mapOf(
            "weight" to weight,
            "height" to height,
            "bmi" to bmi,
            "category" to category
        )

        // Use a fixed key for the user's BMI data. In a real application, this should be a unique identifier for the user.
        val userId = "MCUser" // Replace with a real user ID as appropriate

        firebaseDatabase.child("BMIRecords").child(userId).setValue(bmiData)
            .addOnSuccessListener {
                Toast.makeText(requireActivity(), "BMI data updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireActivity(), "Failed to update BMI data", Toast.LENGTH_SHORT).show()
            }
    }

}
