
%  getting intoxication data from google firebase real-time
firebaseUrl_intox = 'https://shubhammcomp-default-rtdb.firebaseio.com/users/Intox.json';

intox_json = webread(firebaseUrl_intox);

% rtr_intox = intox_json.Intox;

state = intox_json.state;  %
prob = intox_json.score; 

disp("state : ")
disp(state)
disp("Probability : ")
disp(prob)

%Avergae speed...

server_url = 'https://likhithsyadav18.pythonanywhere.com/download';
try
    file_data = webread(server_url);
catch
    fprintf('Failed to load data from server.\n');
end

road_speed = file_data.('velocity');

disp("Avg speed : ")
disp(road_speed)



%  getting sleep data from google firebase real-time
firebaseUrl_sleep = 'https://shubhammcomp-default-rtdb.firebaseio.com/SleepData.json';

sleep_json = webread(firebaseUrl_sleep);

rtr_sleepData = sleep_json.sleepData;

hrs = rtr_sleepData.hoursSlept;  %
quality = rtr_sleepData.sleepQuality; 

disp("Hours Slept : ")
disp(hrs)
disp("Quality of sleep : ")
disp(quality)




% getting BMI data from google firebase
firebaseUrl_BMI = 'https://shubhammcomp-default-rtdb.firebaseio.com/BMIRecords.json';


bmi_json = webread(firebaseUrl_BMI);

if isfield(bmi_json, 'BMIRecords')
    user_bmi_data = bmi_json.BMIRecords.MCUser;

    weight = user_bmi_data.weight;
    height = user_bmi_data.height;
    bmi = user_bmi_data.bmi;
    category = user_bmi_data.category;

    % Display the retrieved BMI data
    % disp("Weight : ")
    % disp(weight)
    % disp("Height : ")
    % disp(height)
    % disp("BMI : ")
    % disp(bmi)
    % disp("Health Category : ")
    disp(category)
else
    disp('BMI data for the specified user not found.')
end




% getting hr rr symptoms from google firebase
firebaseUrl_symptoms = 'https://shubhammcomp-default-rtdb.firebaseio.com/SymptomsData.json';

data = webread(firebaseUrl_symptoms);

strc_inner = fieldnames(data); 
data_inner = data.(strc_inner{1});

if isfield(data_inner, 'symptom')
    symptom = data_inner.symptom;
end


for i = 1:length(strc_inner)
    entry_key = strc_inner{i};
    entry_data = data.(entry_key);
    if isfield(entry_data, 'symptom')
        symptom = entry_data.symptom;
  
    end
end


% sending output back to firebase and android

firebaseURL_output = 'https://shubhammcomp-default-rtdb.firebaseio.com/resource.json'; 

% Commenting the output string to of having switch for now for testing. This will be coming from fuzzy logic controller.
output_str = 'WE HAVE A SWITCH'; 
data = struct('message', output_str); 

options = weboptions('MediaType', 'application/json', 'RequestMethod', 'put');

try
    response_json = webwrite(firebaseURL_output, data, options);
    disp('Data successfully sent to firebase.');
    disp(response_json); 
catch e
    disp('Error sending data');

    disp(getReport(e));
end
