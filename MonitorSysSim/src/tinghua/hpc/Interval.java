package tinghua.hpc;

import jsc.distributions.Normal;
import desmoj.core.dist.ContDistNormal;

public class Interval {
	
	private final double maxValue[] = {2000,2000,300000,1000};
//	private final double CPU = 2000;
//	private final double MEMORY = 2000;
//	private final double DISK = 300000;
//	private final double NETWORK = 1000;
	
	Normal normalCPU = new Normal(0, 100);
	Normal normalMEMORY = new Normal(0, 200);
	Normal normalDISK = new Normal(0, 30000);
	Normal normalNETWORK = new Normal(0, 250);
	
	protected double getNextValue(double value, int type) {

		Normal normal = null;
		switch(type){
		case 0:
			normal = normalCPU;
			break;
		case 1:
			normal = normalMEMORY;
			break;
		case 2:
			normal = normalDISK;
			break;
		case 3:
			normal = normalNETWORK;
			break;
		}
		
		double valueTemp;
		do{
			valueTemp = normal.random() + value;
		}while(valueTemp <= 0 || valueTemp >= maxValue[type]);
		return valueTemp;
	}
	
	public double getAccurate(int interval){
		
		double value0[] = {800,1000,100000,500};
		double value1[] = {800,1000,100000,500};
		
		for(int i = 0; i < interval; i++){
			for(int j = 0; j < 4; j++){
				value1[j] = getNextValue(value1[j], j);
			}
		}
		
		double acc = 0;
		for(int i = 0; i < 4; i++){
			acc += Math.abs(value1[i] - value0[i])/maxValue[i];
		}
		return 1-acc/4;
	}
	
	public static void main(String args[]){
		Interval interval = new Interval();
		double accurate[]= new double[100];
		for(int l = 0; l < 100; l++){
			for(int i = 1; i <= 100; i++){
				accurate[l] += interval.getAccurate(i);
			}
		}
		for(int i = 0; i < 100; i++){
			System.out.println(i+" : " + accurate[i]/100);
		}

	}

}
