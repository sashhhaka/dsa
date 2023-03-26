package homework;
import java.util.*;

public class task1 {

    // The Segment class represents a line segment in the plane
    private static class Segment {
        private Point p1, p2;

        public Segment(Point p1, Point p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        // Check if this segment intersects with another segment
        public boolean intersects(Segment other) {
            return intersect(p1, p2, other.p1, other.p2);
        }

        // Check if two line segments intersect
        private boolean intersect(Point a1, Point a2, Point b1, Point b2) {
            double d1 = direction(b1, b2, a1);
            double d2 = direction(b1, b2, a2);
            double d3 = direction(a1, a2, b1);
            double d4 = direction(a1, a2, b2);
            if (d1 * d2 < 0 && d3 * d4 < 0) {
                return true;
            } else if (d1 == 0 && onSegment(b1, b2, a1)) {
                return true;
            } else if (d2 == 0 && onSegment(b1, b2, a2)) {
                return true;
            } else if (d3 == 0 && onSegment(a1, a2, b1)) {
                return true;
            } else if (d4 == 0 && onSegment(a1, a2, b2)) {
                return true;
            } else {
                return false;
            }
        }

        // Compute the cross product of two vectors given by their endpoints
        private double crossProduct(Point a1, Point a2, Point b1, Point b2) {
            double ax = a2.x - a1.x;
            double ay = a2.y - a1.y;
            double bx = b2.x - b1.x;
            double by = b2.y - b1.y;
            return ax * by - ay * bx;
        }

        // Compute the direction of the vector from a to b relative to the vector from c to d
        private double direction(Point a, Point b, Point c) {
            return crossProduct(c, b, c, a);
        }

        // Check if point p lies on the line segment between a and b
        private boolean onSegment(Point a, Point b, Point p) {
            double minX = Math.min(a.x, b.x);
            double maxX = Math.max(a.x, b.x);
            double minY = Math.min(a.y, b.y);
            double maxY = Math.max(a.y, b.y);
            return (p.x >= minX && p.x <= maxX && p.y >= minY && p.y <= maxY);
        }
    }

    // The Point class represents a point in the plane
    private static class Point {
        private double x, y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    // The Label class represents a label in the label sequence
    private static class Label implements Comparable<Label> {
        private int index;
        private boolean isLeft;

        public Label(int index, boolean isLeft) {
            this.index = index;
            this.isLeft = isLeft;
        }

        public int getIndex() {
            return index;
        }

        public boolean isLeft() {
            return isLeft;
        }

        @Override
        public int compareTo(Label other) {
            if (index == other.index) {
                if (isLeft && !other.isLeft) {
                    return -1;
                } else if (!isLeft && other.isLeft) {
                    return 1;
                } else {
                    return 0;
                }
            } else {
                return Integer.compare(index, other.index);
            }
        }
    }

    public static void main(String[] args) {
        // Read input
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        Segment[] segments = new Segment[n];
        for (int i = 0; i < n; i++) {
            double x1 = sc.nextDouble();
            double y1 = sc.nextDouble();
            double x2 = sc.nextDouble();
            double y2 = sc.nextDouble();
            Point p1 = new Point(x1, y1);
            Point p2 = new Point(x2, y2);
            segments[i] = new Segment(p1, p2);
        }
        sc.close();

        // Sort endpoints of segments from left to right
        List<Point> endpoints = new ArrayList<>();
        for (Segment segment : segments) {
            endpoints.add(segment.p1);
            endpoints.add(segment.p2);
        }
        Collections.sort(endpoints, new Comparator<Point>() {
            @Override
            public int compare(Point p1, Point p2) {
                if (p1.x < p2.x) {
                    return -1;
                } else if (p1.x > p2.x) {
                    return 1;
                } else {
                    return Double.compare(p1.y, p2.y);
                }
            }
        });

        // Create empty label sequence
        TreeSet<Label> labelSeq = new TreeSet<>();

        // Check for intersections
        for (Point endpoint : endpoints) {
            int index = -1;
            boolean isLeft = false;
            for (int i = 0; i < n; i++) {
                if (endpoint == segments[i].p1) {
                    index = i;
                    isLeft = true;
                    break;
                } else if (endpoint == segments[i].p2) {
                    index = i;
                    isLeft = false;
                    break;
                }
            }
            Label label = new Label(index, isLeft);
            if (isLeft) {
                labelSeq.add(label);
                Label successor = labelSeq.higher(label);
                Label predecessor = labelSeq.lower(label);
                if (successor != null && segments[index].intersects(segments[successor.getIndex()])) {
                    System.out.println("INTERSECTION");
                    System.out.printf("%.2f %.2f\n", endpoint.x, endpoint.y);
                    System.out.println(segments[successor.getIndex()].p1.x + " " + segments[successor.getIndex()].p1.y
                            + " " + segments[successor.getIndex()].p2.x + " " + segments[successor.getIndex()].p2.y);
                    return;
                }
                if (predecessor != null && segments[index].intersects(segments[predecessor.getIndex()])) {
                    System.out.println("INTERSECTION");
                    // print the first segment both points
                    System.out.println(endpoint.x + " " + endpoint.y);

                    System.out.println(segments[predecessor.getIndex()].p1.x + " " +
                            segments[predecessor.getIndex()].p1.y + " " +
                            segments[predecessor.getIndex()].p2.x + " " +
                            segments[predecessor.getIndex()].p2.y);
                    return;
                }
            } else {
                labelSeq.remove(label);
                Label successor = labelSeq.higher(label);
                Label predecessor = labelSeq.lower(label);
                if (successor != null && predecessor != null && segments[successor.getIndex()].intersects(segments[predecessor.getIndex()])) {
                    System.out.println("INTERSECTION");
                    System.out.println(segments[predecessor.getIndex()].p1.x + segments[predecessor.getIndex()].p1.y
                    + segments[predecessor.getIndex()].p2.x + segments[predecessor.getIndex()].p2.y);
                    System.out.println(segments[successor.getIndex()].p1.x + " " + segments[successor.getIndex()].p1.y
                    + " " + segments[successor.getIndex()].p2.x + " " + segments[successor.getIndex()].p2.y);
                    return;

                }
                labelSeq.remove(label);
            }
        }
        System.out.println("NO INTERSECTIONS");
    }
}
