package wms.algorithms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Simple 2-dimensional representative class for customer request locations
 * @author hoangbui
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Node {
    private double x;
    private double y;
    private String name;
    public Node add(Node another)
    {
        return new Node(x+another.x, y+another.y, name+ " + " + another.name);
    }
    public double dot(Node o)
    {
        return x*o.x + y*o.y;
    }
    public double length()
    {
        return Math.sqrt(x*x + y*y);
    }
}
