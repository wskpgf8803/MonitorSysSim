package tinghua.hpc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MMNWithNodes extends Consumption{

	private int hosts;
	private ArrayList<Request> reqList;
	private int location;
	
	public MMNWithNodes(double cpu, double mem, double disk, double net) {
		super(cpu, mem, disk, net);
		// TODO Auto-generated constructor stub
	}
	
	public MMNWithNodes(int hosts, ArrayList<Request> reqList, int location){
		this.hosts = hosts;
		this.reqList = reqList;
		this.location = location;
	}
	
	public void basicFormular(){
		usage[0] = 100+2*hosts + 0.1*hosts*hosts;
		usage[1] = 100+0.5*hosts;
		usage[2] = hosts;
		usage[3] = hosts*0.1;
	}
	
	public double getBCons(){
		basicFormular();
		return getCons();
	}
	
	public void responseFormular(){
//		usage[0] = 0.1*hosts*reqList.size();
//		usage[1] = 0.1*hosts;
//		usage[2] = 0;
//		usage[3] = hosts * reqList.size();
//		for(int i = 0; i < reqList.size(); i++){
//			if(distance(location, reqList.get(i).getLocation())<=0){
//				System.out.println(".....");
//			}
//			usage[3] += distance(location, reqList.get(i).getLocation());
//		}
		int size = reqList.size();
		usage[0] = 0.5*size*Math.log(size+1);
		usage[1] = 0.2*(size*size) + 2*size;
		usage[2] = 0;
		usage[3] = 0;
		for(int i = 0; i < reqList.size(); i++){
			usage[3] += distance(location, reqList.get(i).getLocation());
		}
	}
	
	public double getRCons(){
		if(reqList == null){
			return 0;
		}
		responseFormular();
		return getCons();
	}
	
	public double getTCons(){
		return getBCons()+getRCons();
//		return getRCons();
	}
	
	public static int distance(int loc1, int loc2){
		if(loc1 <= loc2){
			return (loc2-loc1)<(10+loc1-loc2)?(1 + loc2-loc1):(11+loc1-loc2);
		}else{
			return (loc1-loc2)<(10+loc2-loc1)?(1 + loc1-loc2):(11+loc2-loc1);
		}
	}
	
	public static void main(String args[]){
		
		String filePath = "experimentData//";
		clrAndMkDir(filePath);
		File file = new File(filePath);
		file.mkdir();
		
		PrintWriter X_Host = null;
		PrintWriter Y_ConsFMN = null;	
		
		try {
			X_Host = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "X_Host.txt")));
			Y_ConsFMN = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "Y_ConsFMN.txt")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		double cons = 0;
		for(int i = 1; i <= 100; i++){
			MMNWithNodes MMN = new MMNWithNodes(i,null,0);
			cons = MMN.getBCons();
			X_Host.print(i + " ");
			Y_ConsFMN.print(cons + " ");
			System.out.println(i+" : "+cons +" : "+MMN.getIdealCons());
		}
		X_Host.close();
		Y_ConsFMN.close();
	
	}
	
	public static void clrAndMkDir(String fileName){
		File file = new File(fileName);
		if(file.exists()){
			deleteDirectory(file);
		}		
		
		file = new File(fileName);
		file.mkdir();
	}
	
	public static void deleteDirectory(File file){
        try{
            if(file.exists()&&file.isDirectory()){
                String[] contents = file.list();
                for(int i=0;i<contents.length;i++){
                    File file2X = new File(file.getAbsolutePath() + "/" +contents[i]);
                    if(file2X.exists()){
                        if(file2X.isFile()){
                            file2X.delete();
                        }else if(file2X.isDirectory()){
                            deleteDirectory(file2X);
                        }
                    }else{
                        throw new RuntimeException("File not exist!");
                    }
                }
                file.delete();
            }else{
                throw new RuntimeException("Not a directory!");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
	
}
