package com.richitec.donkey;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class DonkeyThreadPool {
	
	private ExecutorService executer;
	
	public DonkeyThreadPool(){
		executer = Executors.newCachedThreadPool();
	}
	
	public Future<?> submit(Runnable task){
		return executer.submit(task);
	}
	
}
