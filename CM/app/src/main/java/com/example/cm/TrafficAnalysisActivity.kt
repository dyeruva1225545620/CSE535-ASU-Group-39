package com.example.cm


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.maps.android.PolyUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.json.JSONException
import org.json.JSONObject

class TrafficAnalysisActivity : AppCompatActivity(), OnMapReadyCallback {

    //Add ons OnMapReadyCallback included

    // Google Map object
    private var mGoogleMap: GoogleMap? = null

    // Current and destination location objects
    private var sourceLocation: Place? = null
    private var destinationLocation: Place? = null

    // To get location permissions
    private val LOCATION_REQUEST_CODE = 23
    private var locationPermission = false

//    // Polyline object
//    private var polylines: List<Polyline>? = null

    private lateinit var autoCompleteFragment_from: AutocompleteSupportFragment
    private lateinit var autoCompleteFragment_to: AutocompleteSupportFragment

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        //Add ons

        //Request location permission
        requestPermission()

        Places.initialize(applicationContext, getString(R.string.google_map_api_key))

        autoCompleteFragment_from =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment_from) as AutocompleteSupportFragment
        autoCompleteFragment_from.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.ADDRESS_COMPONENTS,
                Place.Field.LAT_LNG,
                Place.Field.UTC_OFFSET
            )
        )
        autoCompleteFragment_from.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                Toast.makeText(
                    this@TrafficAnalysisActivity,
                    "Some Error in Source Location in Search",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onPlaceSelected(place: Place) {
                val add = place.address
                //val id = place.id
                val plLatLng = place.latLng!!
                if (add != null) {
                    val latitude = plLatLng.latitude
                    val longitude = plLatLng.longitude
                    val coordinates = LatLng(latitude, longitude)
                    addFromMarker(coordinates, add.toString())
                    zoomOnMap(coordinates)
                    sourceLocation = place
                }
            }

        })

        autoCompleteFragment_to =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment_to) as AutocompleteSupportFragment
        autoCompleteFragment_to.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.ADDRESS_COMPONENTS,
                Place.Field.LAT_LNG,
                Place.Field.UTC_OFFSET
            )
        )
        autoCompleteFragment_to.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                Toast.makeText(
                    this@TrafficAnalysisActivity,
                    "Some Error in Destination Location in Search",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onPlaceSelected(place: Place) {
                val add = place.address
                //val id = place.id
                val plLatLng = place.latLng!!
                if (add != null) {
                    val latitude = plLatLng.latitude
                    val longitude = plLatLng.longitude
                    val coordinates = LatLng(latitude, longitude)
                    addToMarker(coordinates, add.toString())
                    zoomOnMap(coordinates)
                    destinationLocation = place
                }
            }

        })

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Add ons
        val mapOptionButton: ImageButton = findViewById(R.id.mapOptionsMenu)
        val popupMenu = PopupMenu(this, mapOptionButton)
        popupMenu.menuInflater.inflate(R.menu.map_options, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            changeMap(menuItem.itemId)
            true
        }

        mapOptionButton.setOnClickListener {
            popupMenu.show()
        }

        val findButton = findViewById<ImageButton>(R.id.findButton)
        findButton.setOnClickListener {
            getLocCoords()
        }

        val curLocButton = findViewById<ImageButton>(R.id.curLocButton)
        curLocButton.setOnClickListener {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            getCurrentLoc()
        }
    }

    private fun getCurrentLoc() {
        val task = fusedLocationProviderClient.lastLocation

        if( (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
            (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            return
        }
        task.addOnSuccessListener {loc ->
            if(loc != null)
            {
                mGoogleMap?.clear()
                zoomOnMap(LatLng(loc.latitude, loc.longitude))
                addCustomDraggableMarker(LatLng(loc.latitude, loc.longitude))
            }
        }
    }

    private fun requestPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_REQUEST_CODE)
        } else {
            locationPermission = true
        }
        return locationPermission
    }


    //Add ons
    private fun zoomOnMap(latLng: LatLng) {
        val newLatLngZoom =
            CameraUpdateFactory.newLatLngZoom(latLng, 15f)      //Zoom: 12f --> Zoom level
        mGoogleMap?.animateCamera(newLatLngZoom)
    }

    private fun changeMap(itemId: Int) {
        when (itemId) {
            R.id.normal_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.hybrid_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.satellite_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.terrain_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
    }

    //Add ons
    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap?.clear()

        //addDraggableMarker(LatLng(13.12234,13.235))
        //addCustomDraggableMarker(LatLng(19.500,16.14))
        mGoogleMap?.setOnMapClickListener { position ->
            mGoogleMap?.clear()
            addCustomDraggableMarker(position)
        }

        mGoogleMap?.setOnMapLongClickListener { position ->
            addCustomDraggableMarker(position)
        }

        mGoogleMap?.setOnMarkerClickListener { marker ->
            marker.remove()
            false
        }
    }

    private fun addFromMarker(pos: LatLng, add: String): Marker {
        val marker = mGoogleMap?.addMarker(
            MarkerOptions()
                .position(pos)
                .title("Source Address: $add")
                .snippet("$pos")
                .draggable(true)
        )
        Toast.makeText(this@TrafficAnalysisActivity,"(${pos.latitude},${pos.longitude})", Toast.LENGTH_LONG).show()
        return marker!!
    }

    private fun addToMarker(pos: LatLng, add: String): Marker {
        val marker = mGoogleMap?.addMarker(
            MarkerOptions()
                .position(pos)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Destination Address: $add")
                .snippet("$pos")
                .draggable(true)
        )
        Toast.makeText(this@TrafficAnalysisActivity,"(${pos.latitude},${pos.longitude})", Toast.LENGTH_LONG).show()
        return marker!!
    }

    private fun addCustomDraggableMarker(pos: LatLng) {
        mGoogleMap?.addMarker(
            MarkerOptions()
                .position(pos)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                .title("Location Marker")
                .snippet("$pos")
                .draggable(true)
        )
        Toast.makeText(this@TrafficAnalysisActivity,"(${pos.latitude},${pos.longitude})", Toast.LENGTH_LONG).show()
    }

    private fun getLocCoords() {
        val fromCoordinates = sourceLocation?.latLng
        val toCoordinates = destinationLocation?.latLng
        if (fromCoordinates != null && toCoordinates != null) {

            val origin = "${fromCoordinates.latitude},${fromCoordinates.longitude}"
            val destination = "${toCoordinates.latitude},${toCoordinates.longitude}"
            val apiKey = getString(R.string.google_map_api_key)

            val urlDirections =
                "https://maps.googleapis.com/maps/api/directions/json?origin=$origin&destination=$destination&key=$apiKey"
            println(urlDirections)

            val path: MutableList<List<LatLng>> = ArrayList()
            val directionsRequest = object :
                StringRequest(Method.GET, urlDirections, Response.Listener { response ->
                    try {
                        val jsonResponse = JSONObject(response)
                        // Get routes
                        val routes = jsonResponse.getJSONArray("routes")
                        val legs = routes.getJSONObject(0).getJSONArray("legs")
                        val steps = legs.getJSONObject(0).getJSONArray("steps")

                        for (i in 0 until steps.length()) {
                            val points =
                                steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                            path.add(PolyUtil.decode(points))
                        }

                        for (i in 0 until path.size) {
                            this.mGoogleMap?.addPolyline(
                                PolylineOptions().addAll(path[i]).color(Color.GREEN).width(10f)
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        // Handle JSON parsing error
                    }
                }, Response.ErrorListener { error ->
                    error.printStackTrace()
                    // Handle the error, e.g., show an error message to the user
                }) {}
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(directionsRequest)

            val url = "https://likhithsyadav18.pythonanywhere.com/GMaps"
//            val params =
//                "api_key = $apiKey & start_latitude = ${fromCoordinates.latitude} & start_longitude = ${fromCoordinates.longitude} & end_latitude = ${toCoordinates.latitude} & end_longitude = ${toCoordinates.longitude}"

            val formBody = FormBody.Builder()
                .add("api_key", apiKey)
                .add("start_coordinates", origin)
                .add("end_coordinates", destination)
                .build()

            val request = Request.Builder().url(url).post(formBody).build()
            println("Request: $request")
            val response = client.newCall(request).enqueue(responseCallback = object : Callback {
                override fun onFailure(call: Call, e: IOException)
                {
                    runOnUiThread {
                        Toast.makeText(this@TrafficAnalysisActivity, "Something went wrong: ${e.message}", Toast.LENGTH_SHORT).show()
                        call.cancel()
                    }
                }

                override fun onResponse(call: Call, response: okhttp3.Response) {
                    runOnUiThread {
                        try {
                            println(response.body!!.string())

                            Toast.makeText(this@TrafficAnalysisActivity,"Flask Connection Success", Toast.LENGTH_LONG).show()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            })

            println("Response: $response")
        }
        else
        {
            Toast.makeText(this@TrafficAnalysisActivity, "No route found!!", Toast.LENGTH_SHORT).show()
        }
    }
}
//
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import org.json.JSONObject
//import android.util.Log
//import android.widget.Toast
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//
//class TrafficAnalysisActivity : AppCompatActivity() {
//
//    // Your API Key
//    private val API_KEY = "AIzaSyD6nMkF-bkUkMebUE8O8EuHOSjBhZTUoeY"
//
//    private lateinit var firebaseDatabase: DatabaseReference
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_third)
//
//        // Initialize Firebase Database
//        firebaseDatabase = FirebaseDatabase.getInstance().getReference("TrafficData")
//
//        val startLatitude: EditText = findViewById(R.id.start_latitude)
//        val startLongitude: EditText = findViewById(R.id.start_longitude)
//        val endLatitude: EditText = findViewById(R.id.end_latitude)
//        val endLongitude: EditText = findViewById(R.id.end_longitude)
//        val submitButton: Button = findViewById(R.id.submit_button)
//        val resultText: TextView = findViewById(R.id.result_text)
//        val durationText: TextView = findViewById(R.id.duration_text)
//        val durationInTrafficText: TextView = findViewById(R.id.duration_in_traffic_text)
//        val distanceText: TextView = findViewById(R.id.distance_text)
//
//        submitButton.setOnClickListener {
//            val origins = "${startLatitude.text},${startLongitude.text}"
//            val destinations = "${endLatitude.text},${endLongitude.text}"
//            val departureTimestamp = System.currentTimeMillis() / 1000
//            val url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=$origins&destinations=$destinations&departure_time=$departureTimestamp&key=$API_KEY"
//
//            Thread {
//                try {
//                    val client = OkHttpClient()
//                    val request = Request.Builder().url(url).build()
//                    client.newCall(request).execute().use { response ->
//                        val responseBody = response.body()?.string()
//                        val jsonObject = JSONObject(responseBody ?: "")
//                        val rows = jsonObject.getJSONArray("rows")
//                        val elements = rows.getJSONObject(0).getJSONArray("elements")
//                        val element = elements.getJSONObject(0)
//                        val duration = element.getJSONObject("duration").getInt("value")
//                        val durationInTraffic = element.getJSONObject("duration_in_traffic").getInt("value")
//                        val distance = element.getJSONObject("distance").getInt("value")
//
//                        val workload = if (duration > durationInTraffic) "Low Cognitive Workload (Normal Road Condition)" else "High Cognitive Workload (Poor Road Condition)"
//
//                        val averageSpeed = if (durationInTraffic > 0) {
//                            (distance.toDouble() / durationInTraffic.toDouble()) * 3.6 // Convert from m/s to km/h
//                        } else {
//                            0.0
//                        }
//
//                        runOnUiThread {
//                            resultText.text = workload
//                            durationText.text = "Average Duration: $duration seconds"
//                            durationInTrafficText.text = "Current Duration: $durationInTraffic seconds"
//                            distanceText.text = "Distance: $distance meters"
//
//                            // Upload the average speed to Firebase
//                            uploadAverageSpeedToFirebase(averageSpeed)
//                        }
//                    }
//                } catch (e: Exception) {
//                    Log.e("TrafficAnalysis", "Error: ", e)
//                }
//            }.start()
//        }
//    }
//
//    companion object {
//        private const val BMI_DATA_KEY = "SpeedData"
//    }
////    private fun uploadDataToFirebase(weight: Float, height: Float, bmi: Float, category: String) {
////        val bmiData = mapOf(
////            "weight" to weight,
////            "height" to height,
////            "bmi" to bmi,
////            "category" to category
////        )
////
////        // Use a fixed key for the user's BMI data. In a real application, this should be a unique identifier for the user.
////        val userId = "MCUser" // Replace with a real user ID as appropriate
////
////        firebaseDatabase.child("BMIRecords").child(userId).setValue(bmiData)
////            .addOnSuccessListener {
////                Toast.makeText(requireActivity(), "BMI data updated successfully", Toast.LENGTH_SHORT).show()
////            }
////            .addOnFailureListener {
////                Toast.makeText(requireActivity(), "Failed to update BMI data", Toast.LENGTH_SHORT).show()
////            }
////    }
//
//    private fun uploadAverageSpeedToFirebase(averageSpeed: Double) {
//        val currentTime = System.currentTimeMillis()
//        val speedData = mapOf(
//            "timestamp" to currentTime,
//            "averageSpeed" to averageSpeed
//        )
//        val user_Id = "MCUser"
//        firebaseDatabase.child("AverageSpeed").child(user_Id).setValue(speedData)
//            .addOnSuccessListener {
//                Log.d("TrafficAnalysis", "Average speed data uploaded successfully")
//            }
//            .addOnFailureListener { e ->
//                Log.e("TrafficAnalysis", "Failed to upload average speed data", e)
//            }
//    }
//}
