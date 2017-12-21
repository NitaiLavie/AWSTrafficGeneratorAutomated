package com.nsslawsproject.awstrafficgenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.nsslawsproject.constants.connectionConstants;

public class AWSClient extends Thread{
	// this is a client thread.
	// it's responsibility is to create the connection to the aws server through the ELB
	// and then wait for the response received from the server
	// additionally the client thread sends run parameters to the aws server with each message
	// this is needed because there is no way to know which server will get this parameter and if this
	// is a new server that just has been started by the aws auto scaling group
	
	private Socket socket = null;
	private BufferedReader buffRead = null;
	private BufferedWriter buffWrite = null;
	private int clientNum; // each client thread has a uniqe num for logging the tasks
	private Long iterations; // number of iteration parameter to be sent to the server
	private LogWriter logWriter; // the log writer object that is being used for logging
	
	public AWSClient(LogWriter writer, int num, Long handlingTime) {
		clientNum = num;
		logWriter = writer;
		iterations = handlingTime;
	}
	@Override
	public void run() {
		
		super.run();
		RttTimer timer = new RttTimer(); // monitoring the round trip time
		try {
			socket = new Socket(connectionConstants.host, connectionConstants.port);
			buffRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffWrite = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			timer.start();
			buffWrite.write(connectionConstants.cores+"\n"); // amount of cores to stress
			buffWrite.write(connectionConstants.timeout+"\n"); // seconds to timeout the stress
			buffWrite.write(iterations+"\n"); // number of iterations
			buffWrite.write(connectionConstants.update_interval+"\n"); // what is the metric update interval in millis
			buffWrite.write(connectionConstants.limit+"\n"); // what is the thread count limit
			long startTime = System.currentTimeMillis();
			buffWrite.flush();
			logWriter.write(clientNum+","+startTime+",start,,");
			String reply = buffRead.readLine(); // waiting for an answer from the server
			long endTime = System.currentTimeMillis();
			timer.stop();
			logWriter.write(clientNum+","+endTime+","+reply);
			//System.out.println(reply + ": RTT = " + timer);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				buffRead.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
