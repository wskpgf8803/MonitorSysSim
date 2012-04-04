package tsinghua.hpc.monitoring.process;

import tsinghua.hpc.monitoring.mode.MonitorSysSim;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;


public class Gmond extends SimProcess {
	
	public Gmond(Model model, String arg1, boolean arg2) {
		super(model, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	private MonitorSysSim model;

	private int seq;  
	
	private int layer;
	
	public void lifeCycle() {

		
	}

}