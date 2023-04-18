package localsearch.domainspecific.vehiclerouting.apps.minmaxvrp.experiments;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

class Result{
	double obj;
	double time_2_best;
	public Result(double obj, double time_2_best){
		this.obj = obj; this.time_2_best = time_2_best;
	}
	public Result(String fn){
		try{
			Scanner in = new Scanner(new File(fn));
			this.obj = in.nextDouble();
			this.time_2_best = in.nextDouble();
			in.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
public class ExperimentRunner {

	public static void runExperiments(String type){
		int nbRuns = 20;
		String in_dir = "";
		String out_dir = "";
		String[] fn = null;
		if(type == "F-VRP"){
			fn = new String[]{
					"F-n72-m4.vrp",
					"F-n72-m5.vrp",
					"F-n72-m6.vrp",
					"F-n135-m7.vrp",
					"F-n135-m8.vrp"
			};
		}
		else if(type == "CMT"){
			fn = new String[]{
					"vrpnc1-m5.txt",
					"vrpnc1-m6.txt",
					"vrpnc1-m7.txt",
					"vrpnc2-m10.txt",
					"vrpnc2-m11.txt",
					"vrpnc2-m12.txt",
					"vrpnc3-m8.txt",
					"vrpnc3-m9.txt",
					"vrpnc3-m10.txt",
					"vrpnc4-m12.txt",
					"vrpnc4-m13.txt",
					"vrpnc5-m16.txt",
					"vrpnc5-m17.txt",
					"vrpnc11-m7.txt",
					"vrpnc11-m8.txt",
					"vrpnc12-m10.txt",
					"vrpnc12-m11.txt"
			};
		}
		else if(type == "CE-VRP"){
			fn = new String[]{
					"vrpnc1.txt",
					"vrpnc2.txt",
					"vrpnc3.txt",
					"vrpnc4.txt",
					"vrpnc5.txt",
					"vrpnc11.txt",
					"vrpnc12.txt"
			};
		}
		
		in_dir = "data/MinMaxVRP/" + type + "/std_all/";
		out_dir = "data/output/MinMaxVRP/" + type + "/std-all/";
		
		int timeLimit = 300;
		
		/*String[] fn = new String[]{
				"E-n101-k14.vrp",
				"E-n23-k3.vrp",
				"E-n33-k4.vrp",
				"E-n76-k14.vrp",
				"E-n76-k8.vrp",
				"E-n101-k8.vrp",
				"E-n30-k3.vrp",
				"E-n51-k5.vrp",
				"E-n76-k15.vrp",
				"E-n22-k4.vrp",
				"E-n30-k4.vrp",
				"E-n76-k10.vrp",
				"E-n76-k7.vrp",
			"E-n7-k2.vrp",
			"E-n13-k4.vrp",
			"E-n31-k7.vrp"
		};
		
			
		String in_dir = "data/MinMaxVRP/Christophides/std-all-round-euclide-distance/";
		String out_dir = "output/MinMaxVRP/Christophides/std-all-round-euclide-distance/";
		
		int timeLimit = 300;
		*/		
		
		/*
		String[] fn = {
				"kelly01.txt",
				"kelly02.txt",
				"kelly03.txt",
				"kelly04.txt",
				"kelly05.txt",
				"kelly06.txt",
				"kelly07.txt",
				"kelly08.txt",
				"kelly09.txt",
				"kelly10.txt",
				"kelly11.txt",
				"kelly12.txt",
				"kelly13.txt",
				"kelly14.txt",
				"kelly15.txt",
				"kelly16.txt",
				"kelly17.txt",
				"kelly18.txt",
				"kelly19.txt",
				"kelly20.txt",
			};
		
			
		String in_dir = "data/MinMaxVRP/Kelly/std_all/";
		String out_dir = "output/MinMaxVRP/Kelly/std_all/";
		
		int timeLimit = 300;
		*/
		
		try{
			//PrintWriter out = new PrintWriter(outFile);
			
			for(int r = 1; r <= nbRuns; r++){
				for(int i = 0; i < fn.length; i++){
					String fi = in_dir + "" + fn[i];
					
					String fo = "";
					
					/*
					fo = out_dir + "MinMaxCVRPMultiNeighborhoodsWithTotalCost-ins-" + 
					fn[i] + "-run-" + r + ".txt";
					MinMaxCVRPMultiNeighborhoodsWithTotalCost.run(fi, fo, timeLimit);
					
					
					fo = out_dir + "MinMaxCVRPMultiNeighborhoods-ins-" + 
							fn[i] + "-run-" + r + ".txt";
					MinMaxCVRPMultiNeighborhoods.run(fi, fo, timeLimit);
					
					fo = out_dir + "MinMaxCVRP2Neighborhoods-ins-" + 
									fn[i] + "-run-" + r + ".txt";
					MinMaxCVRP2Neighborhoods.run(fi, fo, timeLimit);
					
					
					fo = out_dir + "MinMaxCVRP2NeighborhoodsWithTotalCost-ins-" + 
							fn[i] + "-run-" + r + ".txt";
					MinMaxCVRP2NeighborhoodsWithTotalCost.run(fi, fo, timeLimit);
					*/
					
					fo = out_dir + "MinMaxCVRPMultiNeighborhoodsWithTotalCostNotPerturbNeighborhood-ins-" + 
							fn[i] + "-run-" + r + ".txt";
					
					MinMaxCVRPMultiNeighborhoodsWithTotalCostNotPerturbNeighborhood.run(fi, fo, timeLimit);
			
				}
				
			}
			//out.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}

	}

	public static void runStatistics(String type, String out_dir, String statisticFN){
		int nbRuns = 20;

		/*
		String[] fn = new String[]{
				"E-n7-k2.vrp",
				"E-n13-k4.vrp",
				"E-n22-k4.vrp",
				"E-n23-k3.vrp",
				"E-n30-k4.vrp",
				"E-n30-k3.vrp",
				"E-n31-k7.vrp",
				"E-n33-k4.vrp",
				"E-n51-k5.vrp",
				"E-n76-k14.vrp",
				"E-n76-k8.vrp",
				"E-n76-k15.vrp",
				"E-n76-k10.vrp",
				"E-n76-k7.vrp",
				"E-n101-k8.vrp",
				"E-n101-k14.vrp"
				
		};
		
		*/
		/*
		String[] fn = {
				"kelly01.txt",
				"kelly02.txt",
				"kelly03.txt",
				"kelly04.txt",
				"kelly05.txt",
				"kelly06.txt",
				"kelly07.txt",
				"kelly08.txt",
				"kelly09.txt",
				"kelly10.txt",
				"kelly11.txt",
				"kelly12.txt",
				"kelly13.txt",
				"kelly14.txt",
				"kelly15.txt",
				"kelly16.txt",
				"kelly17.txt",
				"kelly18.txt",
				"kelly19.txt",
				"kelly20.txt",
			};
		*/
		String[] fn = null;
		if(type == "F-VRP"){
			fn = new String[]{
					"F-n72-m4.vrp",
					"F-n72-m5.vrp",
					"F-n72-m6.vrp",
					"F-n135-m7.vrp",
					"F-n135-m8.vrp"
			};
		}
		else if(type == "CMT"){
			fn = new String[]{
					"vrpnc1-m5.txt",
					"vrpnc1-m6.txt",
					"vrpnc1-m7.txt",
					"vrpnc2-m10.txt",
					"vrpnc2-m11.txt",
					"vrpnc2-m12.txt",
					"vrpnc3-m8.txt",
					"vrpnc3-m9.txt",
					"vrpnc3-m10.txt",
					"vrpnc4-m12.txt",
					"vrpnc4-m13.txt",
					"vrpnc5-m16.txt",
					"vrpnc5-m17.txt",
					"vrpnc11-m7.txt",
					"vrpnc11-m8.txt",
					"vrpnc12-m10.txt",
					"vrpnc12-m11.txt"
			};
		}
		else if(type == "CE-VRP"){
			fn = new String[]{
					"vrpnc1.txt",
					"vrpnc2.txt",
					"vrpnc3.txt",
					"vrpnc4.txt",
					"vrpnc5.txt",
					"vrpnc11.txt",
					"vrpnc12.txt"
			};
		}

		//String in_dir = "data/MinMaxVRP/Christophides/std-all/";
		//String out_dir = "output/MinMaxVRP/Christophides/std-all/";
		int timeLimit = 300;
		
		try{
			PrintWriter out = new PrintWriter(statisticFN);
			String[] algo = new String[]{
//					"MinMaxCVRP2Neighborhoods",
//					"MinMaxCVRP2NeighborhoodsWithTotalCost",
//					"MinMaxCVRPMultiNeighborhoods",
//					"MinMaxCVRPMultiNeighborhoodsWithTotalCost",
					"MinMaxCVRPMultiNeighborhoodsWithTotalCostNotPerturbNeighborhood"
			};
			out.print("Instances");
			for(int k = 0; k < algo.length; k++){
				out.print("\t" + algo[k] + "\t\t\t\t\t\t\t");
			}
			out.println();
			
			double[][][] f = new double[fn.length][algo.length][nbRuns+1];
			//double[][][] c_max = new double[fn.length][algo.length][nbRuns];
			//double[][][] c_avg = new double[fn.length][algo.length][nbRuns];
			
			for(int i = 0; i < fn.length; i++){
				
				out.print(fn[i] + "\t&\t");
				for(int k = 0; k < algo.length; k++){
					double min_f = Integer.MAX_VALUE;
					double max_f = 1-min_f;
					double avg_f = 0;
					double avg_t = 0;
					
					for(int r = 1; r <= nbRuns; r++){
						String fo = out_dir + "/" + algo[k] + "-ins-" + 
								fn[i] + "-run-" + r + ".txt";
						Result rs = new Result(fo);
						if(min_f > rs.obj) min_f = rs.obj;
						if(max_f < rs.obj) max_f = rs.obj;
						avg_f += rs.obj;
						avg_t += rs.time_2_best;
			
						f[i][k][r] = rs.obj;
						
					}
					
					avg_f = avg_f*1.0/nbRuns;
					avg_t = avg_t*1.0/nbRuns;
					
					out.print(min_f + "\t&\t" + max_f + "\t&\t" + avg_f + "\t&\t" + avg_t + "\t&\t");
					
				}
				out.println();
			}
			out.close();
			
			// count number of runs that one algo find better results than another
			int nbBetter = 0;
			int nbEqual = 0;
			int nbWorse = 0;
//			for(int i = 0; i < fn.length; i++){
//				for(int r = 0; r < nbRuns; r++){
//					if(Math.abs(f[i][3][r] - f[i][0][r]) < 0.0001){
//						nbEqual++;
//					}else if(f[i][3][r] < f[i][0][r]){
//						nbBetter++;
//					}else{
//						nbWorse++;
//					}
//				}
//			}
			
			System.out.println("nbBetter = " + nbBetter + ", nbEqual = " + nbEqual + ", nbWorse = " + nbWorse);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//ExperimentRunner.runStatistics( "output/MinMaxVRP/Christophides/std-all-round-euclide-distance/","output/MinMaxVRP/Christophides/std-all-round-euclide-distance/statistic.txt");
		
		//ExperimentRunner.runStatistics( "output/MinMaxVRP/Kelly/std_all/","output/MinMaxVRP/Kelly/std_all/statistic.txt");
		
		
		//ExperimentRunner.runStatistics( "output/MinMaxVRP/Christophides/std-all-round-euclide-distance/","output/MinMaxVRP/Christophides/std-all-round-euclide-distance/statistic.txt");
		String[] type = new String[]{"F-VRP", "CMT", "CE-VRP"};
		for(int i = 0; i < type.length;i++){
			ExperimentRunner.runExperiments(type[i]);
			ExperimentRunner.runStatistics(type[i], "data/output/MinMaxVRP/" + type[i] +"/std_all/", "data/output/MinMaxVRP/" + type[i] + "/std_all/statistic.txt");
		}
		//ExperimentRunner.runStatistics( "output/MinMaxVRP/Kelly/std_all/","output/MinMaxVRP/Kelly/std_all/statistic.txt");
		
		
		
	}

}
