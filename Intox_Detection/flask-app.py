from flask import Flask,request,jsonify,send_file
import googlemaps
import pandas as pd
import polyline
import os

app = Flask(__name__)

@app.route("/")

def hello_word():
    return 'Hello'

#@app.route("/GMaps", methods = ["GET","POST"])
@app.route("/GMaps", methods = ["POST"])
def get_traffic_status():
    api_key = request.form['api_key']
    start_address = request.form['start_coordinates']
    end_address = request.form['end_coordinates']

    gmaps = googlemaps.Client(key=api_key)
    directions = gmaps.directions(start_address, end_address, mode='driving')

    data = []

    if directions:
        if directions[0]['legs'][0]['distance']['value']< 25000:
            traffic_matrix = gmaps.distance_matrix(
                    start_address,
                    end_address,
                    mode='driving',
                    departure_time='now',
                    traffic_model='best_guess'
                )

            if 'rows' in traffic_matrix:
                data = traffic_matrix['rows'][0]['elements']
            else:
                return jsonify({"error": "No traffic data found"})

            for element in data:
                #print(element['distance'])
                distance = element['distance']['value']
                duration_in_traffic = element['duration_in_traffic']['value']
                duration = element['duration']['value']

                travel_time_index = (element['duration_in_traffic']['value'] / element['duration']['value'])
                '''
                    Normal Road Condititons:
                            TTI < 1.0 (Less than 1.0):	Excellent Conditions
                            1.0 <= TTI < 1.3 (Between 1.0 and 1.3):	Satisfactory Conditions

                    Poor Road Conditions:
                            1.3 <= TTI < 1.5 (Between 1.3 and 1.5): Moderate Congestion
                            1.5 < TTI < 2.0 (Between 1.5 and 2.0): Heavy Congestion
                            TTI ≥ 2.0 (2.0 or greater):	Severe Congestion
                '''
                if element['duration']['value'] > 0 and travel_time_index < 1.3:
                    element['status'] = 'Normal'
                elif element['duration']['value'] > 0 and travel_time_index >= 1.3:
                    element['status'] = 'Poor'
                else:
                    element['status'] = 'No data'

        else:
            route = directions[0]['overview_polyline']['points']
            path = polyline.decode(route)

            # Sample points along the path
            sample_rate = 0.05  # You can adjust the sample rate as needed
            sampled_points = path[::int(1/sample_rate)]

            # Convert the sampled points to coordinates (latitude, longitude)
            coordinates = [(lat, lng) for lat, lng in sampled_points]
            #sorted_coordinates = sorted(coordinates, key=lambda x: x[0])

            coordinates.insert(0, start_address)
            coordinates.append(end_address)

            location_from = [coordinates[i] for i in range(0, len(coordinates) - 1)]
            location_to = [coordinates[i] for i in range(1, len(coordinates))]


            for origin,destination in zip(location_from,location_to):

                # Get traffic congestion data using the Distance Matrix API
                traffic_matrix = gmaps.distance_matrix(
                    origin,
                    destination,
                    mode='driving',
                    departure_time='now',
                    traffic_model='best_guess'
                )

                if 'rows' in traffic_matrix:
                    data.append(traffic_matrix['rows'][0]['elements'][0])
                else:
                    return jsonify({"error": "No traffic data found"})

            origin_remove = []
            destination_remove = []

            for element,origin,destination in zip(data,location_from,location_to):
                #print(origin,destination)
                # distance = element['distance']['value']
                # duration_in_traffic = element['duration_in_traffic']['value']
                # duration = element['duration']['value']

                '''
                Normal Road Condititons:
                        TTI < 1.0 (Less than 1.0):	Excellent Conditions
                        1.0 <= TTI < 1.3 (Between 1.0 and 1.3):	Satisfactory Conditions

                Poor Road Conditions:
                        1.3 <= TTI < 1.5 (Between 1.3 and 1.5): Moderate Congestion
                        1.5 < TTI < 2.0 (Between 1.5 and 2.0): Heavy Congestion
                        TTI ≥ 2.0 (2.0 or greater):	Severe Congestion
                '''
                if element['duration']['value'] > 0 and (element['duration_in_traffic']['value'] / element['duration']['value']) < 1.3:
                    element['status'] = 'Normal'
                elif element['duration']['value'] > 0 and (element['duration_in_traffic']['value'] / element['duration']['value']) >= 1.3:
                    element['status'] = 'Poor'
                else:
                    element['status'] = 'No data'
                    origin_remove.append(origin)
                    destination_remove.append(destination)

                # traffic_status = element['status']
                # print(f"Distance: {distance}, Duration in Traffic: {duration_in_traffic}, Duration: {duration}, Traffic Status: {traffic_status}")
                # print(f"TTI: {travel_time_index}")
                # print('\n')
            location_from_filtered = [item for item in location_from if item not in origin_remove]
            location_to_filtered = [item for item in location_to if item not in destination_remove]

    else:
        return jsonify({"error": "No route found"})

    filtered_data = [entry for entry in data if entry['status'] != 'No data']

    distance = [entry['distance']['value'] for entry in filtered_data]
    duration = [entry['duration']['value'] for entry in filtered_data]
    duration_in_traffic = [entry['duration_in_traffic']['value'] for entry in filtered_data]
    travel_time_index = [round(entry['duration_in_traffic']['value'] / entry['duration']['value'], 3) for entry in filtered_data]
    velocity = [round(entry['distance']['value'] / entry['duration']['value'],3) for entry in filtered_data]
    velocity_in_traffic = [round(entry['distance']['value'] / entry['duration_in_traffic']['value'],3) for entry in filtered_data]
    status = [entry['status'] for entry in filtered_data]

    if directions[0]['legs'][0]['distance']['value'] > 25000:
        df = pd.DataFrame({
                'location_from': location_from_filtered,
                'location_to': location_to_filtered,
                'distance': distance,
                'duration': duration,
                'duration_in_traffic': duration_in_traffic,
                'travel_time_index': travel_time_index,
                'velocity': velocity,
                'velocity_in_traffic': velocity_in_traffic,
                'status': status
            })
    else:
        df = pd.DataFrame({
                'distance': distance,
                'duration': duration,
                'duration_in_traffic': duration_in_traffic,
                'travel_time_index': travel_time_index,
                'velocity': velocity,
                'velocity_in_traffic': velocity_in_traffic,
                'status': status
            })

    # print(df)
    json_index = df.to_json(orient ='index')
    df.to_csv('output.csv', index=False)

    return (json_index)
    #return (value)

@app.route("/download", methods=["GET"])
def download_file():
    file_path = '/home/likhithsyadav18/output.csv'
    return send_file(file_path, as_attachment=True, download_name="output.csv")

# if __name__=="__main__":
#     app.run(host="0.0.0.0", port=5000)

if __name__ == "__main__":
    app.run(debug=False, host="0.0.0.0", port=int(os.environ.get("PORT", 5000)))
