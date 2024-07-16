%%%%%%%%%%%%%%%Fire Base Values %%%%%%%%%%%%%%%%%%%%%%%%%%%
Hr=80;%from firebase server
Rr=6;%from firebase server
BMI=21;%from firebase server
Sleep_duration=9;%from firebase server
Intoxication_level=0.1;%from firebase server
avg_speed=35;%from firebase server
%%%%%%%%%%%%%%% Fuzzy for Reaction time %%%%%%%%%%%%%%%%%%%
fis = readfis('ReactionTimeFuzzy.fis');
inputValues = [BMI, Sleep_duration, Intoxication_level];
output = evalfis(fis, inputValues);
additionalTr = output(1);

%%%%%%%%%%%%%%% Reaction time calculation %%%%%%%%%%%%%%%%%
Baseline_Tr=0.01*(Hr/Rr);
Tr=additionalTr+Baseline_Tr;
%%%%%%%%%%%%%%%%%%%Initial Condition%%%%%%%%%%%%%%%%%%%%%%%%%
Speed = 40;%get this value from the car speedometer
DisAO=-43.8;
%%%%%%%%Gain and Deceleration Limit Calculation %%%%%%%%%%%
speeddiff=Speed-avg_speed;
fis = readfis('Gainandeclim.fis');
inputValues = [DisAO, speeddiff];
output = evalfis(fis, inputValues);
Gain = output(1);
decelLim= output(2);
if (decelLim<150)
    decelLim=150;
end
if (decelLim>200)
    decelLim=200;
end
decelLim=decelLim*(-1);
i=1;
swap=0; 
%%%%%%%%%%%%%Autonomous Simulation%%%%%%%%%%%%%%%%%%%%%%%%%%
[A,B,C,D,Kess, Kr, Ke, uD] = designControl(secureRand(),Gain);
load_system('LaneMaintainSystem.slx')

set_param('LaneMaintainSystem/VehicleKinematics/Saturation','LowerLimit',num2str(decelLim))
set_param('LaneMaintainSystem/VehicleKinematics/vx','InitialCondition',num2str(Speed))

simModel = sim('LaneMaintainSystem.slx');
if (simModel.sx1.Data(end)<0)
    fprintf('switching not needed \n');
else
K=length(simModel.sx1.Data);
while i<=K
humandec=1.1*decelLim;
load_system('HumanActionModel.slx')
  [A,B,C,D,Kess, Kr, Ke, uD] = designControl(secureRand(),Gain);
set_param('HumanActionModel/VehicleKinematics/Saturation','LowerLimit',num2str(humandec))
set_param('HumanActionModel/VehicleKinematics/vx','InitialCondition',num2str(Speed))
humanModel = sim('HumanActionModel.slx');
if (humanModel.sx1.Time(end)< simModel.sx1.Time(end))
swap=1;
    fprintf('switching needed \n');
    break;
end
Speed=simModel.vx1.Data(2);
DisAO=simModel.sx1.Data(2);
i=i+1;
end
if (swap==0)
    fprintf('switching not needed \n');
end
end

