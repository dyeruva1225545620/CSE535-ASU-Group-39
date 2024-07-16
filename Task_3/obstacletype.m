%%%%%%%%%%%%%%%Fire Base Values %%%%%%%%%%%%%%%%%%%%%%%%%%%

%  getting sleep data from google firebase real-time
firebaseUrl_sleep = 'https://shubhammcomp-default-rtdb.firebaseio.com/SleepData.json';

sleep_json = webread(firebaseUrl_sleep);

rtr_sleepData = sleep_json.sleepData;

Sleep_duration = rtr_sleepData.hoursSlept;  %
quality = rtr_sleepData.sleepQuality; 
disp(Sleep_duration)



% getting BMI data from google firebase
firebaseUrl_BMI = 'https://shubhammcomp-default-rtdb.firebaseio.com/BMIRecords.json';


bmi_json = webread(firebaseUrl_BMI);

if isfield(bmi_json, 'BMIRecords')
    user_bmi_data = bmi_json.BMIRecords.MCUser;

    weight = user_bmi_data.weight;
    height = user_bmi_data.height;
    BMI = user_bmi_data.bmi;
    category = user_bmi_data.category;
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
    Hr = data_inner.heartRate;
    Rr = data_inner.respiratoryRate;
    disp(Hr)
    disp(Rr)
end




Hr=80;%from firebase server
Rr=6;%from firebase server
% BMI=21;%from firebase server


%Avergae speed...
server_url = 'https://likhithsyadav18.pythonanywhere.com/download';
try
    file_data = webread(server_url);
catch
    fprintf('Failed to load data from server.\n');
end

avg_speed = file_data.('velocity');

disp("Avg speed : ")
disp(avg_speed)






%getting intoxication data from google firebase real-time
firebaseUrl_intox = 'https://shubhammcomp-default-rtdb.firebaseio.com/users/Intox.json';

intox_json = webread(firebaseUrl_intox);

% rtr_intox = intox_json.Intox;

state = intox_json.state;  %
Intoxication_level = intox_json.score; 


% avg_speed=35;%from firebase server
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
fprintf ("1 for static obstacle and 2 for dynamic obstacle \n");
userInput = input('Enter your selection: ', 's');
% Convert the input to an integer
    integerValue = str2double(userInput);
%%%%%%%%%%%%%%% Fuzzy for Reaction time %%%%%%%%%%%%%%%%%%%
fis = readfis('ReactionTimeFuzzy.fis');
inputValues = [BMI, Sleep_duration, Intoxication_level];
output = evalfis(fis, inputValues);
additionalTr = output(1);
Baseline_Tr=0.01*(Hr/Rr);
Tr=additionalTr+Baseline_Tr;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
if integerValue==1
    fprintf("Static Obstacle \n");
userInput = input('Initial Speed of the Vehicle: ', 's');
% Convert the input to an integer
    Speed = str2double(userInput);
userInput = input('Distance between obstacle and vehicle: ', 's');
% Convert the input to an integer
    DisAO = str2double(userInput);
    staticadvisory
end
if integerValue==2
    userInput = input('Speed of the Vehicle in the front: ', 's');
% Convert the input to an integer
    initSpeedA = str2double(userInput);
userInput = input('Initial Speed of the Vehicle ', 's');
% Convert the input to an integer
    initSpeedB = str2double(userInput);
     fprintf("Dynamic Obstacle \n");
    dynamicadvisory
end
