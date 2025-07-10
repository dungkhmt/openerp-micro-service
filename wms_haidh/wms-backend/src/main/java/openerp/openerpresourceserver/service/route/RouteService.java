package openerp.openerpresourceserver.service.route;

import java.util.List;

import openerp.openerpresourceserver.dto.request.CoordinateDTO;
import openerp.openerpresourceserver.dto.request.CoordinateRequest;
import openerp.openerpresourceserver.dto.response.RoutePathResponse;

/**
 * General interface for route calculation services.
 */
public interface RouteService {

    /**
     * Calculates the route based on the given coordinate request.
     *
     * @param request A {@link CoordinateRequest} containing the list of coordinates to calculate the route from.
     * @return A {@link RoutePathResponse} containing:
     *         - the total distance of the route (in meters),
     *         - a list of path points in [lat, lng] format as returned by the routing provider.
     */
    RoutePathResponse getRoute(CoordinateRequest request);

    /**
     * Calculates the distance matrix between each pair of coordinates in the given list.
     * 
     * @param coordinateList A list of {@link CoordinateDTO} representing geographic coordinates 
     *                       for which distances will be calculated.
     * @return A two-dimensional array representing the distance matrix,
     *         where element [i][j] is the distance (in meters) from coordinate i to coordinate j.
     */
	double[][] getDistances(List<CoordinateDTO> coordinateList);
}



