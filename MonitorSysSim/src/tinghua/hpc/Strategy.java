package tinghua.hpc;

import java.util.ArrayList;

public class Strategy {
	
	private ArrayList<Federation> fedList;
	private int frequency;
	private int runningTime = 24;
	private final double minCons = 0.1;
	private final double maxCons = 0.5;
	
	public void initFedList(){
		fedList = new ArrayList<Federation>();
		int num_nodes_array[] = {4,50,70,3,12,28,56,44,19,89};
		int num_MMN_array[] = {3,3,3,3,3,3,3,3,3,3};
		double rate_array[] = {0.05,0.15,0.15,0.02,0.07,0.07,0.13,0.1,0.06,0.2};
		for(int i = 0; i < 10; i++){
			Federation fed= new Federation();
			fed.setNum_nodes(num_nodes_array[i]);
			fed.setNum_MMNs(num_MMN_array[i]);
			fed.setLocaltion(fed.getId());
			fed.setRate(rate_array[i]);
			fedList.add(fed);
		}
	}

}
