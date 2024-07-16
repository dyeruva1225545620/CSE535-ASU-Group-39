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



firebaseURL_output = 'https://shubhammcomp-default-rtdb.firebaseio.com/resource.json'; 

% Commenting the output string to of having switch for now for testing. This will be coming from fuzzy logic controller.

if(swap == 1)
    output_str = 'WE HAVE A SWITCH';
    disp(output_str)
else
    output_str = 'WE DONOT HAVE TO SWITCH';
    disp(output_str)
end
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



