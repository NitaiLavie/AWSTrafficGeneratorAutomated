package com.nsslawsproject.awstrafficgenerator;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LogWriter {
	private Lock mLock;
	private String mLogFileName;
	private Writer mLogWriter;
	
	
	public LogWriter(String name) {
		mLogFileName = name;
		mLock = new ReentrantLock();
		try {
			mLogWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(mLogFileName), "utf-8")
			);
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void write(String log) {
		mLock.lock();
		try {
			mLogWriter.write(log + "\n");
			mLogWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mLock.unlock();
	}

}
