package com.nsslawsproject.constants;

public class connectionConstants {
	
	// here we hold the connection constants we use in the traffic generator
	// some are in use and some are not
	
	public static final int distParam = 3;
	public static final String host = "aws-project-load-balancer-979509742.us-east-2.elb.amazonaws.com";
	//public static final String host ="127.0.0.1";
	public static final int port = 7777;
	
	public static final int cores = 1;
	public static final int timeout = 10; // in sec
	//public static final int iterations = 0; // for sending times calculations
	public static final int iterations = (int) 6e6; // this is a good load
	
	public static final int sleepMulitplier = 1000;
	
	public static final int sleepMulitplierOneMachineLoad = 845; // 948.4864864865 RTT avg - 212.4680851064 net time avg

	public static final int sleepMulitplierStart = 800;
	public static final int sleepMulitplierEnd = 100;
	public static final int sleepMulitplierShiftPeriod = 960000; // in millis
	public static final int sleepMultiplierMachineOpenTime = 120000;
	public static final int sleepMultiplierMaxMachines = 6;
	
	public static long startTime = System.currentTimeMillis();
	
	public static int update_interval = 5000; // in millis
	public static int limit = 10;
	
}
