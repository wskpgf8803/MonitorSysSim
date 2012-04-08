package tsinghua.hpc.monitoring.newprocess;


import java.util.concurrent.TimeUnit;

import tsinghua.hpc.monitoring.mode.MonitorSysSim;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;


public class MMN extends SimProcess {
	
	private final int CPU = 0;
	
	private final int MEMORY = 1;
	
	private final int DISK = 2;
	
	private final int NETWORK = 3;
	
	private MonitorSysSim model;

	private int seq;  
	
	private int layer;
	
	private int clusterSize;
	
	public static double totalQuc[];
	
	public static double lossQuc[];
	
	private double totalVolume[];
	
	private double lossVolume[];
	
	public MMN(Model model, String arg1, boolean arg2, int seq, int layer, int clusterSize) {
		super(model, arg1, arg2);
		// TODO Auto-generated constructor stub
		this.seq = seq;
		this.layer = layer;
		this.clusterSize = clusterSize;
		
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
			totalVolume[CPU] = 2000;
			totalVolume[MEMORY] = 2000;
			totalVolume[DISK] = 160;
			totalVolume[NETWORK] = 100;
		}
		lossVolume = new double[4];
		{
			lossVolume[CPU] = 50;
			lossVolume[MEMORY] = clusterSize * 1 ;
			lossVolume[DISK] = 0;
			lossVolume[NETWORK] = clusterSize * 0.16;
		}
	}
	
	public void lifeCycle() {

		while(true){
			double time = MonitorSysSim.getTaskArrivalTime();
			hold(new TimeSpan(time));
			for(int i = 0; i < 4 ; i++){
				totalQuc[i] += MonitorSysSim.getQuc(totalVolume[i], i);
				switch(i){
				case NETWORK:
					lossQuc[i] += MonitorSysSim.getQuc(lossVolume[i] / time , i);	
				default:
					lossQuc[i] += MonitorSysSim.getQuc(lossVolume[i], i);	
				}	
			}
		}
		
	}

}