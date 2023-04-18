package localsearch.domainspecific.vehiclerouting.vrp.largeneighborhoodexploration;

import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public interface ILargeNeighborhoodExplorer {
	public void exploreLargeNeighborhood(Neighborhood N);
	public void performMove(IVRMove m);
	public String name();
}
