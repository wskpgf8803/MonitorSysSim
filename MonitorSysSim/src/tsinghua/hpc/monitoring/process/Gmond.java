package tsinghua.hpc.monitoring.process;


import java.util.concurrent.TimeUnit;

import tsinghua.hpc.monitoring.mode.MonitorSysSim;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;


public class Gmond extends SimProcess {
	
	private MonitorSysSim model;

	private int seq;  
	
	private int layer;
	
	public static double totalQuc[];
	
	public static double lossQuc[];
	
	public static double totalVolume[];
	
	public static double lossVolume[];
	
	public Gmond(Model model, String arg1, boolean arg2, int seq, int layer) {
		super(model, arg1, arg2);
		// TODO Auto-generated constructor stub
		this.seq = seq;
		this.layer = layer;
		/*
		 * 0  CPU(M Hz)
		 * 1  Memory(M)
		 * 2  Disk(G)
		 * 3  Network(M)
		 */
		totalQuc = new double[4];
		lossQuc = new double[4];
		totalVolume =  new double[4];
		{
			totalVolume[0] = 2000;
			totalVolume[1] = 2000;
			totalVolume[2] = 160;
			totalVolume[3] = 100;
		}
		lossVolume = new double[4];
		{
			lossVolume[0] = 50;
			lossVolume[1] = 50;
			lossVolume[2] = 0.1;
			lossVolume[3] = 1;
		}
	}
	
	public void lifeCycle() {

		while(true){
			for(int i = 0; i < 4 ; i++){
				totalQuc[i] += MonitorSysSim.getQuc(totalVolume[i], i);
				lossQuc[i] += MonitorSysSim.getQuc(lossVolume[i], i);				
			}
			double time = MonitorSysSim.getTaskArrivalTime();
			hold(new TimeSpan(time));
		}
		
	}

}