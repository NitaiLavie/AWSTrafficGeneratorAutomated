#!/bin/bash

cwd=`pwd`
itr=26700
slp=720

echo setting up AWS credentials and region...
source set_aws_credentials.sh
echo finished setting up AWS credentials and region!
echo
echo creating results directory...
echo
mkdir -p results; cd results
echo global parameters: itr=$itr slp=$slp
echo

# first line - const lower threshold
mkdir -p first_line; cd first_line
echo running first simulation - constant lower threshold...
low=0.75
high=1
while [ `echo $high \<= 10 | bc` -ne 0 ]
do
	echo current run thresholds: low=$low high=$high
	java -jar $cwd/AWSTrafficGenerator.jar $itr $low $high
	sleep $slp
	high=`echo $high + 2 | bc`
done
cd ..
echo finished running first simulation!
echo

# second line - const upper threshold
mkdir -p second_line; cd second_line
echo running second simulation - constant upper threshold...
low=0.25
high=3
while [ `echo $low \<= $high | bc` -ne 0 ]
do
	echo current run thresholds: low=$low high=$high
	if [ $high = '3' ] && [ $low = '0.75' ] || [ $low = '.75' ]
	then
		echo duplicate detected. skiping...
	else
		java -jar $cwd/AWSTrafficGenerator.jar $itr $low $high
		sleep $slp
	fi
	low=`echo $low + 0.5 | bc`
done
cd ..
echo finished running second simulation!
echo

# third line - const dist 
mkdir -p third_line; cd third_line
echo running third simulation - constant distance
low=0.75
high=3
while [ `echo $high \<= 10 | bc` -ne 0 ]
do
	echo current run thresholds: low=$low high=$high
	if [ $high = '3' ] && [ $low = '0.75' ] || [ $low = '.75' ]
	then
		echo duplicate detected. skiping...
	else
		java -jar $cwd/AWSTrafficGenerator.jar $itr $low $high
		sleep $slp
	fi
	high=`echo $high + 2 | bc`
	low=`echo $low + 2 | bc`
done
cd ..
echo finished running third simulation!
echo

# fourth line - const average
mkdir -p fourth_line; cd fourth_line
echo running fourth simulation - constant average
low=0.75
high=3
while [ `echo $low \>= 0 | bc` -ne 0 ]
do
	echo current run thresholds: low=$low high=$high
	if [ $high = '3' ] && [ $low = '0.75' ] || [ $low = '.75' ]
	then
		echo duplicate detected. skiping...
	else
		java -jar $cwd/AWSTrafficGenerator.jar $itr $low $high
		sleep $slp
	fi
	high=`echo $high + 0.25 | bc`
	low=`echo $low - 0.25 | bc`
done
cd ..
echo finished running fourth simulation!
echo

cd ..
echo finished running all simulations!!!
