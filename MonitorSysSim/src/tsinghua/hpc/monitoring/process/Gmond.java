package tsinghua.hpc.monitoring.process;

import tsinghua.hpc.monitoring.mode.MonitorSysSim;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;


public class Gmond extends SimProcess {
	
	private MonitorSysSim model;

	private int seq;  
	
	private int layer;
	
	private int totalQuc[];
	
	private int lossQuc[];
	
	public Gmond(Model model, String arg1, boolean arg2, int seq, int layer) {
		super(model, arg1, arg2);
		// TODO Auto-generated constructor stub
		this.seq = seq;
		this.layer = layer;
		totalQuc = new int[4];
		lossQuc = new int[4];
	}
	
	public void lifeCycle() {

		
	}

}