package openerp.openerpresourceserver.api.vrp.model.cvrp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolutionRoute {
    private List<RouteElement> routeElements;
}
