package tinghua.hpc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.math.stat.Frequency;
import org.apache.xerces.impl.xpath.regex.REUtil;

public class Strategy {
	
	private ArrayList<Federation> fedList;
	private Map<Integer, Node> nodeMap;
	private static int frequency = 50;
	private int runningTime = 24;
	private final double minCons = 0.001;
	private final double maxCons = 0.005;
	private int minHosts = 15;
	
	public void initFedList(){
		Random rand =  new Random();
		fedList = new ArrayList<Federation>();
		nodeMap = new HashMap<Integer, Node>();
		Federation.counter = 0;
		Node.counter = 0;
		Request.counter = 0;
		int num_nodes_array[] = {4,50,70,3,12,28,56,44,19,89};
		int num_MMN_array[] = {3,3,3,3,3,3,3,3,3,3};
		double rate_array[] = {0.05,0.15,0.15,0.02,0.07,0.07,0.13,0.1,0.06,0.2};
		for(int i = 0; i < 10; i++){
			Federation fed= new Federation();
			fed.setNum_nodes(num_nodes_array[i]);
			fed.setNum_MMNs(num_MMN_array[i]);
			fed.setRate(rate_array[i]);
			fed.subFed.add(fed);
			for(int j = 0; j < num_nodes_array[i]; j++){
				Node node = new Node();
				node.setFederationId(fed.getId());
				node.setLocation(fed.getId());
//				node.setLocation(rand.nextInt(10));
				fed.nodes.add(node.getId());
				nodeMap.put(node.getId(), node);
			}

			selectMMN(fed, num_MMN_array[i]);
			fedList.add(fed);
		}
	}
	
	/**
	 * randomly select number num_MMNs MMNs in Fderation
	 * @param fed
	 * @param num_MMNs
	 */
	public void selectMMN(Federation fed, int num_MMNs){
		fed.MMNs.clear();
		if(fed.nodes.size() <= num_MMNs){
			for(int j = 0; j < fed.nodes.size(); j++){
				fed.MMNs.add(fed.nodes.get(j));
			}
		}else{
			Set<Integer> nodeSet = new TreeSet<Integer>();
			Random rand = new Random();
			for(int j = nodeSet.size(); j < num_MMNs; j=nodeSet.size()){
				nodeSet.add(fed.nodes.get(rand.nextInt(fed.nodes.size())));
			}
			for (Integer integer : nodeSet) {
				fed.MMNs.add(integer);
			}
		}
	}
	
	/**
	 * implement the Huffman-Like Strategy
	 */
	public void huffman(){
		
		TreeSet<Federation> fedTree = new TreeSet<Federation>( new Comparator<Federation>(){

			@Override
			public int compare(Federation arg0, Federation arg1) {
				// TODO Auto-generated method stub
				if(arg0.getNum_nodes() < arg1.getNum_nodes())
					return -1;
				else if(arg0.getNum_nodes() > arg1.getNum_nodes())
					return 1;
				if(arg0.getId() < arg1.getId())
					return -1;
				else
					return 1;
			}	
		});
		
		while(!isHuffBalance()){		
			fedTree.clear();
			for (Federation federation : fedList) {
				fedTree.add(federation);
			}
			Federation a = fedTree.pollFirst();
			Federation b = fedTree.pollFirst();
			fedTree.add(combineFeder(a, b));	
			fedList = new ArrayList<Federation>(fedTree);
		}	
	}
	
	/**
	 * check whether all the federation satisfy the balance of Huffman-Like Strategy
	 * @return
	 */
	public boolean isHuffBalance(){
		if(fedList.size() <= 1)
			return true;
		for (Federation federation : fedList) {
			if(federation.getNum_nodes() <= minHosts){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * combine two federation, the hosts of a is larger than of b
	 * @param a
	 * @param b
	 * @return
	 */
	public Federation combineFeder(Federation a, Federation b){
		a.setId(a.getId());
		a.setNum_nodes(a.getNum_nodes() + b.getNum_nodes());
		a.setNum_MMNs(a.getNum_MMNs());
		a.setRate(a.getRate() + b.getRate());
		for(Federation federation : b.subFed){
			a.subFed.add(federation);
		}
		for(Integer intt : b.nodes){
			a.nodes.add(intt);
		}
		selectMMN(a, a.getNum_MMNs());	
		return a;
	}
	
	public void rankMMN(){
		Random rand = new Random();
		for(Federation fed : fedList){
			int num_req = (int) (fed.getRate()*frequency);
			for(int i = 0; i < num_req; i++){
				Request req = new Request();
				req.setFederationId(fed.getId());
				req.setLocation(rand.nextInt(fedList.size()));
				int mMNId = getRankTopId(fed,req);
				req.setMMNId(mMNId);
				Node node = nodeMap.get(mMNId);
				if(node == null){
					System.out.println("a");
				}
				node.reqList.add(req);			
				nodeMap.put(mMNId, node);
				if(new MMNWithNodes(fed.getNum_nodes(), node.reqList, node.getLocation()).getTCons() > maxCons){
					incMMN(fed);
				}
			}
		}
	}
	
	public void noRankMMN(){
		Random rand = new Random();
		for(Federation fed : fedList){
			int num_req = (int) (fed.getRate()*frequency);
			for(int i = 0; i < num_req; i++){
				Request req = new Request();
				req.setFederationId(fed.getId());
				req.setLocation(rand.nextInt(fedList.size()));
				int mMNId = fed.MMNs.get(rand.nextInt(fed.getNum_MMNs()));
				req.setMMNId(mMNId);
				Node node = nodeMap.get(mMNId);
				node.reqList.add(req);			
				nodeMap.put(mMNId, node);
				if(new MMNWithNodes(fed.getNum_nodes(), node.reqList, node.getLocation()).getTCons() > maxCons){
					incMMN(fed);
				}
			}
		}
	}
	
	public int getRankTopId(Federation fed, Request req){
		double minValue = 0.5/(new MMNWithNodes(fed.getNum_nodes(), nodeMap.get(fed.MMNs.get(0)).reqList, nodeMap.get(fed.MMNs.get(0)).getLocation()).getTCons()) 
				+ 0.5/(MMNWithNodes.distance(nodeMap.get(fed.MMNs.get(0)).getLocation(),req.getLocation()));
		int id = fed.MMNs.get(0);
		for(int i = 1; i < fed.MMNs.size(); i++){
			Node node = nodeMap.get(fed.MMNs.get(i));
			double value = 0.5/(new MMNWithNodes(fed.getNum_nodes(), node.reqList, node.getLocation()).getTCons()) 
				+ 0.5/(MMNWithNodes.distance(node.getLocation(),req.getLocation()));
			if(value <= minValue){
				minValue = value;
				id = node.getId();
			}
		}
		return id;
	}
	
	public void incMMN(Federation fed){
		if(fed.getNum_MMNs() == fed.getNum_nodes())
			return;
		Random rand = new Random();
		int id;
		while(true){
			id = fed.nodes.get(rand.nextInt(fed.nodes.size()));
			for(int i = 0; i < fed.MMNs.size(); i++){
				if(fed.MMNs.get(i) == id)
					continue;
			}
			fed.MMNs.add(id);
			fed.setNum_MMNs(fed.getNum_MMNs() + 1);
			break;
		}
	}
	
	public double getCons(){
		double cons = 0;
		RMNWithClusets rmn = new RMNWithClusets(fedList.size(), nodeMap.size());
		cons += rmn.getTCons();
		for(Federation fed:fedList){
			for(int i = 0; i < fed.MMNs.size(); i++){
				MMNWithNodes mmn = new MMNWithNodes(fed.nodes.size(), nodeMap.get(fed.MMNs.get(i)).reqList, nodeMap.get(fed.MMNs.get(i)).getLocation());
				cons += mmn.getTCons();
			}
		}
		return cons;
	}
	
	public double getSatisiedRate(){
		int total = 0;
		int satisfied = 0;
		for(Federation fed:fedList){
			total += fed.getNum_MMNs();
			for(int i = 0; i < fed.MMNs.size(); i++){
				MMNWithNodes mmn = new MMNWithNodes(fed.nodes.size(), nodeMap.get(fed.MMNs.get(i)).reqList, nodeMap.get(fed.MMNs.get(i)).getLocation());
				if(mmn.getTCons() <= maxCons)
					satisfied ++;
			}
		}	
		return (double)satisfied/total;
	}
	
	public void exp1(PrintWriter Y_Cons, PrintWriter Y_Rate){
		System.out.println("Experiment1:");
		initFedList();
		huffman();
		rankMMN();
		double cons = getCons() * runningTime;
		double rate = getSatisiedRate();
		System.out.println(cons);
		System.out.println(rate);
		Y_Cons.print(cons + " ");
		Y_Rate.print(rate + " ");
	}
	
	public void exp2(PrintWriter Y_Cons, PrintWriter Y_Rate){
		System.out.println("Experiment2:");
		initFedList();
		rankMMN();
		double cons = getCons() * runningTime;
		double rate = getSatisiedRate();
		System.out.println(cons);
		System.out.println(rate);
		Y_Cons.print(cons + " ");
		Y_Rate.print(rate + " ");
	}
	
	public void exp3(PrintWriter Y_Cons, PrintWriter Y_Rate){
		System.out.println("Experiment3:");
		initFedList();
		huffman();
		noRankMMN();
		double cons = getCons() * runningTime;
		double rate = getSatisiedRate();
		System.out.println(cons);
		System.out.println(rate);
		Y_Cons.print(cons + " ");
		Y_Rate.print(rate + " ");
	}
	
	public void exp4(PrintWriter Y_Cons, PrintWriter Y_Rate){
		System.out.println("Experiment4:");
		initFedList();
		noRankMMN();
		double cons = getCons() * runningTime;
		double rate = getSatisiedRate();
		System.out.println(cons);
		System.out.println(rate);
		Y_Cons.print(cons + " ");
		Y_Rate.print(rate + " ");
	}
	
	public static void main(String args[]){
		
		String filePath = "experimentData//";
		clrAndMkDir(filePath);
		File file = new File(filePath);
		file.mkdir();
		
		PrintWriter X_Frequency = null;
		PrintWriter Y_Cons1 = null;
		PrintWriter Y_Cons2 = null;
		PrintWriter Y_Cons3 = null;
		PrintWriter Y_Cons4 = null;
		PrintWriter Y_Rate1 = null;
		PrintWriter Y_Rate2 = null;
		PrintWriter Y_Rate3 = null;
		PrintWriter Y_Rate4 = null;
		
		
		try {
			X_Frequency = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "X_Frequency.txt")));
			Y_Cons1 = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "Y_Cons1.txt")));
			Y_Cons2 = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "Y_Cons2.txt")));
			Y_Cons3 = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "Y_Cons3.txt")));
			Y_Cons4 = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "Y_Cons4.txt")));
			Y_Rate1 = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "Y_Rate1.txt")));
			Y_Rate2 = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "Y_Rate2.txt")));
			Y_Rate3 = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "Y_Rate3.txt")));
			Y_Rate4 = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "Y_Rate4.txt")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		frequency = 50;
		for(int i = 0; i < 10; i++){
			X_Frequency.print(frequency + " ");
			Strategy stra = new Strategy();
			stra.exp1(Y_Cons1, Y_Rate1);
			stra.exp2(Y_Cons2, Y_Rate2);
			stra.exp3(Y_Cons3, Y_Rate3);
			stra.exp4(Y_Cons4, Y_Rate4);
			frequency += 50;
		}
		
		X_Frequency.close();
		Y_Cons1.close();
		Y_Cons2.close();
		Y_Cons3.close();
		Y_Cons4.close();
		Y_Rate1.close();
		Y_Rate2.close();
		Y_Rate3.close();
		Y_Rate4.close();

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
