package tsinghua.hpc.monitoring.process;

import tsinghua.hpc.monitoring.mode.MonitorSysSim;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeSpan;

public class Gmetad extends SimProcess {
	
	private MonitorSysSim model;

	private int seq;  
	
	private int layer;
	
	public static double totalQuc[];
	
	public static double lossQuc[];
	
	private double totalVolume[];
	
	private double lossVolume[];
	
	public Gmetad(Model model, String arg1, boolean arg2, int seq, int layer) {
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
			int nodeNum = nodeNum(MonitorSysSim.layerSize - layer);
			lossVolume[0] = 50;
			lossVolume[1] = 50 * nodeNum;
			lossVolume[2] = 0.1 * nodeNum;
			lossVolume[3] = 1 * (MonitorSysSim.layerSize - layer + 1);
		}
	}
	
	public int nodeNum(int layer){
		int num = 0;
		for(int i = 0; i <= layer; i++){
			num += Math.pow(2, i);
		}
		return num;
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


