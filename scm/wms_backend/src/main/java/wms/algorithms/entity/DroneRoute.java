package wms.algorithms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class DroneRoute {
    private List<DroneRouteElement> droneRouteElements;
    public DroneRoute(List<Node> droneRoute) {
        List<DroneRouteElement> routeElements1 = new ArrayList<>();
        for (Node node : droneRoute) {
            DroneRouteElement element = new DroneRouteElement();
            element.setAction("");
            element.setNode(node);
            element.setLocationID(node.getName());
            routeElements1.add(element);
        }
        this.droneRouteElements = routeElements1;
    }

    public List<Node> convertToListNode(List<DroneRouteElement> droneRouteElements) {
        List<Node> convertedListNode = new ArrayList<>();
        for (DroneRouteElement droneRouteElement : droneRouteElements) {
            convertedListNode.add(droneRouteElement.getNode());
        }
        return convertedListNode;
    }
}
