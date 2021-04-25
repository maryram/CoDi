#Make File
THE_CLASSPATH=
for i in `ls ../lib/*.jar`
  do
  THE_CLASSPATH=${THE_CLASSPATH}:${i}
done

#Run******************************************************************************************
folder="../../../LFR"
NumofCommunity=(28 28 34 28 28 33 28 28 28 28)
EpsilonArray=(0.01 0.1 0.4 0.4 0.5)
for method in "myDANIBased" "myRandBased"  #"cicm" "diffusionBased"
do
for i in  1 2 3 4 5 #different miu 0.1 0.2 0.3 0.4 0.5
do
#- - - - - - - - - - - - - - -Input Setting- - - - - - - - - - - - - - - - - - - - - - - - - -

groundtruth=${folder}/Comm${i}.txt #Groundtruth of communities for checking NMI and ...
network=${folder}/Net${i}.txt #Underlying network structure for caclucating density and ...

for j in 100 500 1000 3000 4000 5000 7000 10000 12000 15000 20000 #different cascades
do
subfolder=${folder}/${method}
mkdir ${subfolder}
cascade=${folder}/Cascade-${i}/E-${j}-cascades.txt   #Cascades on network
NumComm=${NumofCommunity[${i}-1]} #for Nicola codes
name=${method}-miu${i}-Cas${j}
#- - - - - - - - For Evaluation - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

communityResult=${subfolder}/LFRB-miu${i}-cas${j} #Name of the file to put the detected communtites in
DANI=${subfolder}/Weight-miu${i}-Cas${j} #Output of DANI for my code
result=${folder}/${method}-${i}-Result #To put the runtime results
MutualInf=${folder}/${method}-${i}_NMI.txt
Clustering=${folder}/${method}-${i}_Ev.txt
Structure=${folder}/${method}-${i}_Structure.txt
#- - - - - - - - - - - - - - - - - - - - - - Run Methods- - - - - - - - - - - - - - - - - - - - - - - - - -

if [ ${method} == "myDANIBased" ]
then

java -classpath "..:${THE_CLASSPATH}" myDANIBased.DANIProgram -i ${cascade} -p ${DANI} -o ${communityResult}  -r ${result} -ep ${EpsilonArray[${i}-1]} -si 10 -ss 10

fi

#- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
if [ ${method} == "myRandBased" ]
then
#Average on 10 different runs
for check in 1 2 3 4 5 6 7 8 9 10
do
java -classpath "..:${THE_CLASSPATH}" myRandBased.RandProgram -i ${cascade} -p ${DANI} -o ${communityResult}  -r ${result} -ep 0.5 -si 10 -ss 10
java -classpath "..:${THE_CLASSPATH}" evaluation.EvaluateMutualInformation -g ${groundtruth} -c ${communityResult} -i ${name} -o ${MutualInf}
java -classpath "..:${THE_CLASSPATH}" evaluation.ClusteringEvaluator -g ${groundtruth} -c ${communityResult} -i ${name} -o ${Clustering}
java -classpath "..:${THE_CLASSPATH}" evaluation.CommunityStructureCalculator -c ${communityResult} -n ${network} -i ${name} -o ${Structure}
continue
done
fi
#- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
if [ ${method} == "diffusionBased" ]
then
java -classpath "..:${THE_CLASSPATH}" diffusionBased.CommunityRate_Inference -i ${cascade} -k ${NumComm} -o ${communityResult} -r ${result}
fi
#- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
if [ ${method} == "cicm" ]
then
java -classpath "..:${THE_CLASSPATH}" cicm.CommunityIC_Inference -i ${cascade} -k ${NumComm} -o ${communityResult} -r ${result}
fi
#- - - - - - - - - - - - - - - - - - - - - - - -Evaluate the results - - - - - - - - - - - - - - - - - - - - - - - - 

java -classpath "..:${THE_CLASSPATH}" evaluation.EvaluateMutualInformation -g ${groundtruth} -c ${communityResult} -i ${name} -o ${MutualInf}
java -classpath "..:${THE_CLASSPATH}" evaluation.ClusteringEvaluator -g ${groundtruth} -c ${communityResult} -i ${name} -o ${Clustering}
java -classpath "..:${THE_CLASSPATH}" evaluation.CommunityStructureCalculator -c ${communityResult} -n ${network} -i ${name} -o ${Structure}

done
done
done
