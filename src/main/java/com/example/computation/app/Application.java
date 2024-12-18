package com.example.computation.app;

import com.example.computation.app.compute.Compute;
import com.example.computation.clerk.Clerk;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Application {
	private final Compute compute;
	private final TimerTask timerTask;
	private long totalGraphsCounted = 0;
	private final File ErrorFile;

	public Application(Compute compute) {
		this.ErrorFile = new File(Configuration.errorFileName);
		this.compute = compute;
		this.timerTask = new MyTimerTask();
		new Timer().scheduleAtFixedRate(timerTask, 0, 6000);
		//600000 - 10 min
	}


	public int start() {

		System.out.println("START - " + new Date());
		String line = "";
		try(BufferedReader bi = new BufferedReader(new InputStreamReader(System.in))) {
			while ((line = bi.readLine()) != null) {
				compute.compute(line);
				totalGraphsCounted++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Clerk.write(Arrays.asList(e.toString(), line), ErrorFile);
			return 1;
		}
		timerTask.run();
		System.out.println("END - " + new Date());
		return 0;
	}


	class MyTimerTask extends TimerTask {
		public void run() {
			try {
				compute.makeRecordAction();
				System.out.println("Total graphs counted was: " + totalGraphsCounted);
			} catch (Exception e) {//Catch exception if any
				System.err.println("Error: " + e.getMessage() + e.getStackTrace()[0].toString());
			}
		}
	}
}