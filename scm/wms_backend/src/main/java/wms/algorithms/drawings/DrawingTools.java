package wms.algorithms.drawings;

import wms.algorithms.entity.Node;
import wms.algorithms.entity.TruckDroneDeliveryInput;
import wms.algorithms.entity.TruckDroneDeliverySolutionOutput;
import wms.algorithms.entity.TruckRouteElement;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class DrawingTools {
    public DrawingTools() {
    }
    @FunctionalInterface
    public static interface Mapping
    {
        public Point transform(double x, double y);
    }

    /**
     * Creates a mapping based on a bounding box
     * @param minX The minimum x coordinate of the input
     * @param minY The minimum y coordinate of the input
     * @param maxX The maximum x coordinate of the input
     * @param maxY The maximum y coordinate of the input
     * @param width The width of the output
     * @param height The height of the output
     * @param margin The margin of the output
     * @return The resulting mapping
     */
    public static Mapping defaultMapping(double minX, double minY, double maxX, double maxY, int width, int height, int margin)
    {

        double xScale = (1d*(width-(2*margin)))/((maxX-minX));
        double yScale = (1d*(height-(2*margin)))/((maxY-minY));
        return (x,y) -> new Point( margin + (int)Math.round((x-minX)*xScale),
                margin + (int)Math.round((y-minY)*(yScale)));
    }
    /**
     * Creates a mapping for an instance
     * @param gi The instance for which this mapping is produced
     * @param width Then width of the output
     * @param height The height of the output
     * @param margin The margin of the output
     * @return The resulting mapping
     */
    public static Mapping defaultMapping(List<Node> gi, int width, int height, int margin)
    {
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (Node v : gi)
        {
            minX = Math.min(minX, v.getX());
            maxX = Math.max(maxX, v.getX());
            minY = Math.min(minY, v.getY());
            maxY = Math.max(maxY, v.getY());
        }
        return defaultMapping(minX, minY, maxX, maxY, width, height, margin);
    }

    /**
     * Creates a mapping based on an instance with 3% of the output box as its margin
     * @param gi The instance
     * @param width The width of the output
     * @param height The height of the output
     * @return The resulting mapping
     */
    public static Mapping defaultMapping(List<Node> gi, int width, int height)
    {
        int margin = (int) Math.ceil(0.03 * Math.max(width,height));
        return defaultMapping(gi, width, height, margin);
    }
    /**
     * Draws an instance on a Graphics object
     * @param gi The instance to be drawn
     * @param m The mapping to be used
     * @param gr The graphics object to draw on
     * @param labels Whether or not to print location labels
     */
    public static void draw(List<Node> gi, Mapping m, Graphics2D gr, boolean labels)
    {
        for (Node v : gi)
        {
            Point p = m.transform(v.getX(), v.getY());
            gr.drawRect(p.x-1, p.y-1, 3, 3);
            if (labels)
            {
                gr.drawString(v.getName(), p.x, p.y);
            }
        }
    }
    private static void drawTruckRoute(TruckDroneDeliveryInput input,
                                       TruckDroneDeliverySolutionOutput solution,
                                       Mapping m, Graphics2D gr) {
//        Point prev = null;
//        for (TruckRouteElement element : solution.getTruckRoute()) {
//            Node currentNode = input.getSpecificLocationById(element.getLocationID());
//            Point current = m.transform(currentNode.getX(), currentNode.getY());
//            if (prev != null) {
//                int x1 = prev.x;
//                int y1 = prev.y;
//                int x2 = current.x;
//                int y2 = current.y;
//                gr.drawLine(x1, y1, x2, y2);
//            }
//            prev = current;
//        }
    }
    public void drawNode(List<Node> initialNode, int w, int h, boolean labels) {
        BufferedImage image = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
        JFrame frame = new JFrame("Line Drawing Example");
        JLabel label = new JLabel(new ImageIcon(image));
        Graphics2D g2d = image.createGraphics();
        Mapping m = defaultMapping(initialNode, w, h);

        label.setBackground(Color.WHITE);
        g2d.setColor(Color.ORANGE);
        g2d.setBackground(Color.WHITE);
        for (Node node : initialNode) {
            Point p = m.transform(node.getX(), node.getY());
            g2d.drawString(node.getName() + "(" + node.getX() + "," + node.getY() + ")", p.x, p.y);
        }
        g2d.dispose();

        frame.add(label);
        frame.pack();
        frame.setVisible(true);
        frame.setBackground(Color.WHITE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void drawSolution(List<Node> truckNode, int w, int h, String labels, boolean isDrawingDrone) {
        BufferedImage image = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
        JFrame frame = new JFrame(labels);
        JLabel label = new JLabel(new ImageIcon(image));
        Graphics2D g2d = image.createGraphics();
        Mapping m = defaultMapping(truckNode, w, h);

        label.setBackground(Color.WHITE);
        g2d.setColor(Color.GREEN);
        g2d.setBackground(Color.WHITE);
        for (int i = 0; i < truckNode.size() - 1; i++) {
            Node currentNode = truckNode.get(i);
            Node nextNode = truckNode.get(i+1);
            Point current = m.transform(currentNode.getX(), currentNode.getY());
            Point next = m.transform(nextNode.getX(), nextNode.getY());
            g2d.drawLine(current.x, current.y, next.x, next.y);
            g2d.drawString(currentNode.getName(), current.x, current.y);
        }
        g2d.dispose();

        frame.add(label);
        frame.pack();
        frame.setVisible(true);
        frame.setBackground(Color.WHITE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void drawSolution(List<Node> truckNode, List<List<Node>> droneNodes, int w, int h, String labels, boolean isDrawingDrone) {
        BufferedImage image = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
        JFrame frame = new JFrame(labels);
        JLabel label = new JLabel(new ImageIcon(image));
        Graphics2D g2d = image.createGraphics();
        Mapping m = defaultMapping(truckNode, w, h);

        label.setBackground(Color.WHITE);
        g2d.setColor(Color.GREEN);
        g2d.setBackground(Color.WHITE);
        for (int i = 0; i < truckNode.size() - 1; i++) {
            Node currentNode = truckNode.get(i);
            Node nextNode = truckNode.get(i+1);
            Point current = m.transform(currentNode.getX(), currentNode.getY());
            Point next = m.transform(nextNode.getX(), nextNode.getY());
            g2d.drawLine(current.x, current.y, next.x, next.y);
            g2d.drawString(currentNode.getName(), current.x, current.y);
        }
        g2d.setColor(Color.ORANGE);
        for (List<Node> droneRoute : droneNodes) {
            for (int i = 0; i < droneRoute.size() - 1; i++) {
                Node currentNode = droneRoute.get(i);
                Node nextNode = droneRoute.get(i+1);
                Point current = m.transform(currentNode.getX(), currentNode.getY());
                Point next = m.transform(nextNode.getX(), nextNode.getY());
                g2d.drawLine(current.x, current.y, next.x, next.y);
                g2d.drawString(currentNode.getName(), current.x, current.y);
            }
        }
        g2d.dispose();

        frame.add(label);
        frame.pack();
        frame.setVisible(true);
        frame.setBackground(Color.WHITE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
