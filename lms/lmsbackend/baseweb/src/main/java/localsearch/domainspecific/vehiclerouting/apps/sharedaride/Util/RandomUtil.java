package localsearch.domainspecific.vehiclerouting.apps.sharedaride.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomUtil {
	public static ArrayList<Integer> randomKFromN(int K,int N)
	{
		ArrayList<Integer> re = new ArrayList<>();
		if(K>=N)
		{
			for(int i=1; i<=N;++i)
				re.add(i);
			return re;
		}
		Set<Integer> st = new HashSet<Integer>();
		Random rand = new Random();
		while(st.size() < K)
		{
			int k = rand.nextInt(N) + 1;
			if(!st.contains(k))
				st.add(k);
		}
		for(int k : st)
			re.add(k);
		return re;
	}
}
