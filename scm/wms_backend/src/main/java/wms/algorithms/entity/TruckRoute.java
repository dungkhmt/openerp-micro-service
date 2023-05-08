package wms.algorithms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor // https://stackoverflow.com/questions/47387143/mappingexception-no-property-found-on-entity-to-bind-constructor-parameter-to
public class TruckRoute {
    private List<TruckRouteElement> routeElements;
    public TruckRoute (List<Node> truckRouteNode) {
        List<TruckRouteElement> routeElements1 = new ArrayList<>();
        for (Node node : truckRouteNode) {
            TruckRouteElement element = new TruckRouteElement();
            element.setAction("");
            element.setNode(node);
            element.setLocationID(node.getName());
            routeElements1.add(element);
        }
        this.routeElements = routeElements1;
    }
    public List<Node> convertToListNode(List<TruckRouteElement> routeElem) {
        List<Node> listNode = new ArrayList<>();
        for (TruckRouteElement element: routeElem) {
            listNode.add(element.getNode());
        }
        return listNode;
    }
}
