package com.nsslawsproject.awstrafficgenerator;

import java.util.Random;
import com.nsslawsproject.constants.connectionConstants;
//aws imports
import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.ComparisonOperator;
import com.amazonaws.services.cloudwatch.model.DescribeAlarmsRequest;
import com.amazonaws.services.cloudwatch.model.DescribeAlarmsResult;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricAlarm;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmRequest;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmResult;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.amazonaws.services.cloudwatch.model.Statistic;

public class TrafficGenerator {
	
	public static void main(String[] args ) throws InterruptedException {
		// main traffic generator thread. where all the action is going on
		
		// getting command line parameters:
		int numOfTasks = Integer.parseInt(args[0]); // the number of client tasks to generate
		double lowThreshold = Double.parseDouble(args[1]); // low threashold for cloudwath alarm
		double highThreshold = Double.parseDouble(args[2]); // high threashold for cloudwath alarm
		
		setAwsAlarms(lowThreshold, highThreshold); // set the alarms with aws java api
		
		Random sleepy = new Random(); // a random number generator for sleep time between client tasks
		Random workRand = new Random(); // a random number generator for server task iterations
		double sendingRate = 3/(double)connectionConstants.sleepMulitplierOneMachineLoad; // sending rate average
		double handlingRate = 1/(double)connectionConstants.iterations; // server task iterations average
		Long sleepTime;
		Long handlingTime;
		LogWriter logWriter = new LogWriter(
				"TrafficGenerator_"+ 
				System.currentTimeMillis() +"_"+
				lowThreshold+"-low_"+
				highThreshold+"-high"+
				".log"); // create the log writer
		logWriter.write("client_num,time_stamp,event,server_id,avg_thread_count\n"); // write log header
		
		for(int counter=1;counter<=numOfTasks;counter++) {
			//sleepTime = Math.round(getTanhSleepMultiplier()*getExpRandom(sleepy, sendingRate));
			//sleepTime = Math.round(getLinearSleepMultiplier()*getExpRandom(sleepy, sendingRate));
			//sleepTime = Math.round(connectionConstants.sleepMulitplier*getExpRandom(sleepy, sendingRate));
			sleepTime = Math.round(getExpRandom(sleepy, sendingRate)); // get exponentially distributed sleep time
			//sleepTime = (long) getLinearSleepMultiplier();
			//sleepTime = (long) getLinearFrequencySleepMultiplier();
			//sleepTime = (long) connectionConstants.sleepMulitplier;
			handlingTime = Math.round(getExpRandom(workRand, handlingRate)); // get exponentially distributed server task iterations
			
			Thread.sleep(sleepTime); // wait between client tasks
			//System.out.println("slept for: " + sleepTime+ "\n" );
			new AWSClient(logWriter, counter, handlingTime).start(); // create the client task thread
		}
		
	}
	
    private static void setAwsAlarms(double lowThreshold, double highThreshold) {
    	// set aws cloud watch alarms with aws java api
    	final AmazonCloudWatch cw =
    		    AmazonCloudWatchClientBuilder.defaultClient();
    		
    		 DescribeAlarmsRequest alarmRequestHigh = new DescribeAlarmsRequest()
    				 .withAlarmNames("AVG_RUNNING_THREADS_COUNT_HIGH");
    		 DescribeAlarmsResult alarmResponseHigh = cw.describeAlarms(alarmRequestHigh);
    		 MetricAlarm alarmHigh = alarmResponseHigh.getMetricAlarms().get(0);
    		 alarmHigh.setThreshold(highThreshold);
    		 alarmHigh.setAlarmDescription("Alarm when average thread count is above " + highThreshold);
    		 
    		 DescribeAlarmsRequest alarmRequestLow = new DescribeAlarmsRequest()
    				 .withAlarmNames("AVG_RUNNING_THREADS_COUNT_LOW");
    		 DescribeAlarmsResult alarmResponseLow = cw.describeAlarms(alarmRequestLow);
    		 MetricAlarm alarmLow = alarmResponseLow.getMetricAlarms().get(0);
    		 alarmLow.setThreshold(lowThreshold);
    		 alarmLow.setAlarmDescription("Alarm when average thread count is below " + lowThreshold);

    		PutMetricAlarmRequest requestHigh = new PutMetricAlarmRequest()
    		    .withAlarmName(alarmHigh.getAlarmName())
    		    .withComparisonOperator(alarmHigh.getComparisonOperator())
    		    .withEvaluationPeriods(alarmHigh.getEvaluationPeriods())
    		    .withMetricName(alarmHigh.getMetricName())
    		    .withNamespace(alarmHigh.getNamespace())
    		    .withPeriod(alarmHigh.getPeriod())
    		    .withStatistic(alarmHigh.getStatistic())
    		    .withThreshold(alarmHigh.getThreshold())
    		    .withAlarmDescription(alarmHigh.getAlarmDescription())
    		    .withUnit(alarmHigh.getUnit())
    		    .withDimensions(alarmHigh.getDimensions())
    		    .withActionsEnabled(alarmHigh.getActionsEnabled())
    		    .withAlarmActions(alarmHigh.getAlarmActions());
    		
    		PutMetricAlarmRequest requestLow = new PutMetricAlarmRequest()
        		    .withAlarmName(alarmLow.getAlarmName())
        		    .withComparisonOperator(alarmLow.getComparisonOperator())
        		    .withEvaluationPeriods(alarmLow.getEvaluationPeriods())
        		    .withMetricName(alarmLow.getMetricName())
        		    .withNamespace(alarmLow.getNamespace())
        		    .withPeriod(alarmLow.getPeriod())
        		    .withStatistic(alarmLow.getStatistic())
        		    .withThreshold(alarmLow.getThreshold())
        		    .withAlarmDescription(alarmLow.getAlarmDescription())
        		    .withUnit(alarmLow.getUnit())
        		    .withDimensions(alarmLow.getDimensions())
        		    .withActionsEnabled(alarmLow.getActionsEnabled())
        		    .withAlarmActions(alarmLow.getAlarmActions());
    		
    		PutMetricAlarmResult responseHigh = cw.putMetricAlarm(requestHigh);
    		PutMetricAlarmResult responseLow = cw.putMetricAlarm(requestLow);
	}

	public static double getExpRandom(Random r, double p) { 
		// generating an exponentially distributed value out of a uniformly distributed value
        return -Math.log((1-r.nextDouble())) / p; 
    }
    
    public static double getTanhSleepMultiplier() {
    	// no used - varying sleep time - used for testing
    	long currentTime = System.currentTimeMillis() - connectionConstants.startTime;
    	return	(
    				Math.tanh(
    					2*Math.E*(
    						(currentTime/(Math.E*connectionConstants.sleepMulitplierShiftPeriod))
    						- 0.5
    					)
    				)
    				* (connectionConstants.sleepMulitplierEnd - connectionConstants.sleepMulitplierStart)/2
    			)
				+ (connectionConstants.sleepMulitplierEnd + connectionConstants.sleepMulitplierStart)/2;
    }
    
    public static double getLinearSleepMultiplier() {
    	// no used - varying sleep time - used for testing
    	long currentTime = System.currentTimeMillis() - connectionConstants.startTime;
    	return (
    		(connectionConstants.sleepMulitplierEnd - connectionConstants.sleepMulitplierStart)
    		/ (double) connectionConstants.sleepMulitplierShiftPeriod
    	) * currentTime + connectionConstants.sleepMulitplierStart;
    }
    
    public static double getLinearFrequencySleepMultiplier() {
    	// no used - varying sleep time - used for testing
    	int maxMachines = connectionConstants.sleepMultiplierMaxMachines;
    	long currentTime = System.currentTimeMillis() - connectionConstants.startTime;
    	double machineNum = ((double) currentTime
        		/ (connectionConstants.sleepMultiplierMachineOpenTime * 4))
    			+1 ;
    	return (
    		connectionConstants.sleepMulitplierOneMachineLoad
    		/ (double) ( machineNum<maxMachines ? machineNum : maxMachines )
    	);
    }
}
