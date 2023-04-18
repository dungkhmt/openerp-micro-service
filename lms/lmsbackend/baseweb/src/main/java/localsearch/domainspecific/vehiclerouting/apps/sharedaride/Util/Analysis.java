package localsearch.domainspecific.vehiclerouting.apps.sharedaride.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Analysis {
	public static double fmin(double []a)
	{
		double mi = Double.MAX_VALUE;
		for(int i=0;i<a.length;++i)
			mi = Math.min(a[i], mi);
		return mi;
	}
	public static double fmax(double []a)
	{
		double ma = Double.MIN_VALUE;
		for(int i=0;i<a.length;++i)
			ma = Math.max(a[i], ma);
		return ma;
	}
	public static double favg(double []a)
	{
		double sum = 0;
		for(int i=0;i<a.length;++i)
			sum += a[i];
		return sum/a.length;
	}
	public static double fLc(double []a)
	{
		double avg = favg(a);
		double s = 0;
		for(int i=0;i<a.length;++i)
			s+= (a[i]-avg)*(a[i]-avg);
		s/=a.length;
		return Math.sqrt(s);
	}
	public static void main(String []args) throws FileNotFoundException
	{
		String s = "S7";
		int K = 10;
		double [] vio = new double[K];
		double [] sumD = new double[K];
		PrintWriter out = new PrintWriter(new File("out/"+s+"/total.txt"));
		double sumMi = 0;
		double sumMa = 0;
		double sumAvg = 0;
		double sumLc = 0;
		int cntVio = 0;
		for(int data = 1; data <= 10; ++data)
		{
			Scanner in= new Scanner(new File("out/"+s+"/N100_R10_D"+data+"_"+s+".txt"));
			for(int i=0;i<K;++i)
			{
				vio[i] = in.nextDouble();
				if(vio[i]>0)
					cntVio ++;
				sumD[i] = in.nextDouble();
			}
			in.close();
			double mi  = fmin(sumD);
			double ma = fmax(sumD);
			double avg = favg(sumD);
			double lc = fLc(sumD);
			out.println(mi+" "+ma+" "+avg+" "+lc);
			
			System.out.println((int)mi+","+(int)ma+","+(int)avg+","+lc);
			sumMi += mi;
			sumMa += ma;
			sumAvg += avg;
			sumLc += lc;
		}
		int n = 10;
		out.println(sumMi/n+" "+sumMa/n+" "+sumAvg/n+" "+sumLc/n);
		System.out.println(sumMi/n+","+sumMa/n+","+sumAvg/n+","+sumLc/n);
		out.close();
		//System.out.println(cntVio);
	}
	
}
/*
827,840,831,4.741826951661777
660,729,698,27.45571546560741
639,692,666,17.114162787037937
660,719,696,20.364266467649472
697.25,745.5451388888889,723.1131944444445,17.418992917989147

847,870,863,8.77513145763444
660,748,702,29.395674494937964
658,711,681,17.619677639601022
693,708,703,5.694566394358155
715.0659722222222,759.7395833333333,737.6465277777777,15.371262496632896
*/
// s5 7180.13888888889 7799.847222222222 7491.316666666667
// s6 7038.819444444444 7749.513888888889 7338.918055555556
//688.8374999999999,746.9569444444445,717.3808333333333,17.342484334026228
//682.6277777777779,757.1375,720.0604166666666,20.656900521120228
//688.3013888888888,746.5875000000002,716.5515277777778,17.526493690281598

/*
864,925,895,19.384515982519243
664,711,690,13.957853018270022
672,708,692,12.579307134273922
710,783,742,25.464274212870716
715,797,756,23.919798518973177
654,732,699,25.517901910251855
813,868,840,19.713014331344088
753,809,773,19.036788417997542
600,672,633,26.0137524837736
729,789,765,19.579366422491116
718.0138888888889,779.9847222222222,749.1316666666667,20.51665724327653

819,858,829,10.932930791624177
655,692,673,12.247188909634382
623,686,657,18.271953225951467
663,703,688,13.418164455158495
690,721,706,8.366637155031324
632,678,654,13.514301409713799
756,816,790,19.217457662561547
705,760,732,18.390825730913072
580,621,602,12.18543200245358
673,746,718,22.737050691574623
680.1958333333333,728.6541666666667,705.288611111111,14.928194203461647


*/
