totalswitches = 0;
totalcollisions=0;
for i=1:1:10
rng('shuffle')



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



%  getting sleep data from google firebase real-time
firebaseUrl_sleep = 'https://shubhammcomp-default-rtdb.firebaseio.com/SleepData.json';

sleep_json = webread(firebaseUrl_sleep);

rtr_sleepData = sleep_json.sleepData;

Sleep_duration = rtr_sleepData.hoursSlept;  %
quality = rtr_sleepData.sleepQuality; 
disp(Sleep_duration)



%getting intoxication data from google firebase real-time
firebaseUrl_intox = 'https://shubhammcomp-default-rtdb.firebaseio.com/users/Intox.json';

intox_json = webread(firebaseUrl_intox);

% rtr_intox = intox_json.Intox;

state = intox_json.state;  %
Intoxication_level = intox_json.score; 


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
% Hr=60+80*rand();%from firebase server
% Rr=12+8*rand();%from firebase server
% BMI=15+20*rand();%from firebase server
% Sleep_duration=3+6*rand();%from firebase server
% Intoxication_level=rand();%from firebase server
% avg_speed=35;%from firebase server
initSpeedA=100*rand();
initSpeedB=100*rand();
fis = readfis('ReactionTimeFuzzy.fis');
inputValues = [BMI, Sleep_duration, Intoxication_level];
output = evalfis(fis, inputValues);
additionalTr = output(1);
Baseline_Tr=0.01*(Hr/Rr);
Tr=additionalTr+Baseline_Tr;
dynamicadvisory
if swap == 1
    ta=i*0.01;
    totalswitches=totalswitches+1;
    load_system('Level3Model.slx');
    set_param('Level3Model/VehicleKinematics/Saturation','LowerLimit',num2str(100*decelLim))
set_param('Level3Model/VehicleKinematics/vx','InitialCondition',num2str(initSpeedB))
set_param('Level3Model/CARA/VehicleKinematics/Saturation','LowerLimit',num2str(decelLim))
set_param('Level3Model/CARA/VehicleKinematics/vx','InitialCondition',num2str(initSpeedA))
set_param('Level3Model/Constant1','Value',num2str(ta))
set_param('Level3Model/Step','Time',num2str(Tr+ta))
set_param('Level3Model/Step','After',num2str(1.1*decelLim))
humanModel= sim('Level3Model.slx');
if (humanModel.sxB.Data(end) >= 0)
    totalcollisions=totalcollisions+1;
end
end
end
fprintf("There are %d switches of which %d result in collision",totalswitches,totalcollisions)
