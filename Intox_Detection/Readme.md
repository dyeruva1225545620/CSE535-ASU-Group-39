# Module-2: User Intoxication detection

This project includes two main run files that perform specific tasks.

## Run Files

### 1. Application_Module.py

`Application_Module.py` is responsible for analyzing video streams captured from the mobile application. It detects whether the user is sober or drunk and calculates the level of intoxication based on the provided video stream.

#### Usage

To use `Application_Module.py`, follow these steps:

1. Ensure you have the necessary dependencies installed.
     ```plaintext
    Python version = 3.11.2

    # requirements.txt for Application_Module.py

    opencv-python==4.8.0.76
    mtcnn==0.1.1
    numpy==1.23.5
    requests==2.28.2
    imutils==0.5.4
    tensorflow==2.12.0
    ```
     
3. Run the script and provide the video stream as input from the mobile app which is done with the help of firebase.

### 2. flask-app.py

`flask-app.py` contains the backend logic for processing map data. This data is used to calculate metrics required for a simulink model. The file integrates Flask, handling incoming requests and computing the necessary metrics from the provided map data.

#### Usage

To utilize `flask-app.py`:

1. Install the required dependencies listed in the project's requirements.
     ```plaintext
    Python version = 3.11.2

    # requirements.txt for flask-app.py

    flask==3.0.0
    googlemaps==4.10.0
    pandas==1.5.3
    polyline==2.0.1
    ```
   
3. Run the Flask application.
4. Generates a .csv file which contains the metrics calculated which is used for running the simulink model.
