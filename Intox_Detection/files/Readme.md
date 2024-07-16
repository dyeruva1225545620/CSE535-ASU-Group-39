## Model File

The `model.h5` file included in this repository is utilized by the Application_Module.py. This file is generated through the training of a Convolutional Neural Network (CNN) using TensorFlow and Keras libraries. It is specifically designed for processing image data, likely for the purpose of intoxication detection from video streams.

#### Usage

To use the `model.h5` file:

1. Ensure you have TensorFlow and Keras installed. If not, install them using pip:

    ```bash
    pip install tensorflow keras
    ```
    
2. Load the model using appropriate methods provided by TensorFlow or Keras.
    Use the following code in your script:
   ```python
    import tensorflow as tf

    # Path to the model file
    save_at = "./files/model.h5"

    # Load the model
    model = tf.keras.models.load_model(save_at)
    ```
   Replace `./files/model.h5` with the correct path to the `model.h5` file in your project directory.
   
4. Utilize this model within `Application_Module.py` for intoxication detection or related tasks.
   ```python
     # Example usage of the loaded model
   
           # Perform predictions or inference using the loaded model
           prediction = model.predict(your_input_data)
    ```
