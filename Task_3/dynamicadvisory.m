load("MemberDecel200.mat");
swap = 0;
%%%%%%%%%%%%%%% Initial Values %%%%%%%%%%%%%%%%%%%%%%
speeddiff= initSpeedA-avg_speed;
rc=zeros(2,1);
rc(1)=(speeddiff/40)+0.5;
rc(2)=0.5-(speeddiff/40);
decelLim=200*rc(1)+150*rc(2);
if (decelLim<150)
    decelLim=150;
end
if (decelLim>200)
    decelLim=200;
end
decelLim=decelLim*(-1);
%Autonomous simulation
load_system('LaneMaintainSystem3Car.slx')
set_param('LaneMaintainSystem3Car/VehicleKinematics/Saturation','LowerLimit',num2str(decelLim))
set_param('LaneMaintainSystem3Car/VehicleKinematics/vx','InitialCondition',num2str(initSpeedB))
set_param('LaneMaintainSystem3Car/CARA/VehicleKinematics/Saturation','LowerLimit',num2str(decelLim))
set_param('LaneMaintainSystem3Car/CARA/VehicleKinematics/vx','InitialCondition',num2str(initSpeedA))
simModelB = sim('LaneMaintainSystem3Car.slx');
arrayLength = length(simModelB.ax1.Data);
t=0.01;
for i=2:arrayLength
decelerationA=(simModelB.vx1.Data(i-1)-simModelB.vx1.Data(i))/t;
distanceAB=simModelB.sx1.Data(i);
speeddiff=simModelB.vx1.Data(i)-avg_speed;
rc=zeros(2,1);
rc(1)=(speeddiff/40)+0.5;
rc(2)=0.5-(speeddiff/40);
decelLim=200*rc(1)+150*rc(2);
%fuzzy logic
fis = readfis('dynamicfuzzy.fis');
inputValues = [distanceAB, speeddiff, decelerationA];
output = evalfis(fis, inputValues);
carDecelerationOutput = output(1);
fis = readfis('dynamicswitching.fis');
inputValues = [carDecelerationOutput, Tr];
output = evalfis(fis, inputValues);
switching = output(1);
if switching > 0.35
    fprintf("Switching needed \n");
    swap=1;
    break;
end
end
if switching < 0.35
    fprintf("Switching not needed \n");
    swap=0;
end
if (decelLim<150)
    decelLim=150;
end
if (decelLim>200)
    decelLim=200;
end
decelLim=decelLim*(-1);



firebaseURL_output = 'https://shubhammcomp-default-rtdb.firebaseio.com/resource.json'; 

% Commenting the output string to of having switch for now for testing. This will be coming from fuzzy logic controller.

if switching > 0.35
    fprintf("Switching needed \n");
    output_str = 'WE HAVE A SWITCH';
    swap=1;
end
if switching < 0.35
    fprintf("Switching not needed \n");
    output_str = 'WE DONOT HAVE TO SWITCH';
    swap=0;
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












