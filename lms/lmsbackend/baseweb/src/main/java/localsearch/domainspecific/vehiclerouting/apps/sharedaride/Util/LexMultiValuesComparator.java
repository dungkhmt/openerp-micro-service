package localsearch.domainspecific.vehiclerouting.apps.sharedaride.Util;

import java.util.Comparator;

import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;

public class LexMultiValuesComparator implements Comparator<LexMultiValues> {

	@Override
	public int compare(LexMultiValues o1, LexMultiValues o2) {
		// TODO Auto-generated method stub
		
		int sz = o1.size();
		for(int i=0;i<sz;++i)
		{
			if(o1.get(i) < o2.get(i))
				return -1;
			else
				if(o1.get(i) > o2.get(i))
					return 1;
		}
		return 0;
	}

}
