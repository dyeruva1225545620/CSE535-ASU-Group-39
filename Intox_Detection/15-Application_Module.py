from collections import Counter
import cv2
from mtcnn.mtcnn import MTCNN
import numpy as np
import requests
import json
from imutils.face_utils import rect_to_bb
import tensorflow as tf

def calcArea(rect): 
	(x, y, w, h) = rect_to_bb(rect)
	return w*h

def calcBoxArea(face): 
	return face['box'][2]*face['box'][3]

detector = MTCNN()
save_at = "./files/model.h5"
model_best = tf.keras.models.load_model(save_at)

video_download_url = 'https://firebasestorage.googleapis.com/v0/b/shubhammcomp.appspot.com/o/videos%2Fvideo_1701897432516.mp4?alt=media&token=c1e3382e-98b2-4404-8eb9-9a735447fcd0'

local_file_path = 'video_demo.mp4'

response = requests.get(video_download_url, stream=True)
if response.status_code == 200:
    with open(local_file_path, 'wb') as f:
        for chunk in response.iter_content(1024):
            f.write(chunk)
    print(f'Video saved to {local_file_path}')
else:
    print('Failed to download video')

ranges = {
    "Sober": (0, 0.5),
    "Drunk": (0.5, 1)
}

binary_dict = {0: "Sober", 1: "Drunk"}

cam = cv2.VideoCapture(local_file_path)
frameskip = 20
currentframe = 1

pred_probs = []
pred_states = []

while(True): 
	ret,image = cam.read() 
	if ret: 
		if currentframe==frameskip:
			currentframe = 1
			image_rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
			result = detector.detect_faces(image_rgb)
			print(result)
			if result:
				result.sort(key = calcBoxArea, reverse = True)
				for face in result:
					(x, y, w, h) = face['box']
					print((x, y, w, h))
					if w > 90 and h > 90:
						detected_face = image[y:y+h, x:x+w]
						gray_detected_face = cv2.cvtColor(detected_face, cv2.COLOR_BGR2GRAY)
						#plt.imshow(gray_detected_face)
						aligned_face = cv2.resize(gray_detected_face, (100, 100))
						image = np.expand_dims(aligned_face, axis=0)
						prediction = model_best.predict(image)
						# print(prediction)
						maxindex = int(np.argmax(prediction))
						# print(maxindex)

						for category, (lower, upper) in ranges.items():
							print(category, (lower, upper))
							if binary_dict[maxindex] == category:
								predicted_category = category
								pred_states.append(category)
								mean_value = np.mean(np.arange(lower, (upper + 1), 0.01))
								std_deviation = np.std(np.arange(lower, (upper + 1), 0.01))
								random_number = np.random.normal(mean_value, std_deviation)
								predicted_probability = np.clip(random_number, lower, upper)
								pred_probs.append(float(predicted_probability))
								break
							
							else:
								continue
					else:
						continue
		else:
			currentframe = currentframe +1
	else: 
		break

	
print(pred_states)
print(pred_probs)
						
state_counts = Counter(pred_states)
result_state = max(state_counts, key=state_counts.get)
result_prob = sum(pred_probs) / len(pred_probs)

print(result_state, result_prob)
#cv2.putText(image_rgb,  f"{result_state} ({result_prob:.2f})", (x+20, y-60), cv2.FONT_HERSHEY_SIMPLEX, 2, (255, 255, 255), 2, cv2.LINE_AA)
						
# plt.imshow(image_rgb)
# plt.axis('off')  # Turn off axis labels
# plt.show()
cam.release() 
cv2.destroyAllWindows()

user_key = 'Intox'
url = f'https://shubhammcomp-default-rtdb.firebaseio.com/users/{user_key}.json'

data = {
    'state': result_state,
    'score': result_prob
}

response = requests.patch(url, data=json.dumps(data))

if response.ok:
    print(f"Data at key '{user_key}' updated successfully.")
else:
    print(f"Failed to update data at key '{user_key}'. Error:", response.text)
