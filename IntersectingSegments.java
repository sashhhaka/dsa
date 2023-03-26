//package homework2;
//Alexandra Vabnits

// Importance of line segment intersection task:
// The task of finding the intersection of two line segments appears in many areas of computer science,
// including computer graphics, robotics, and mathematical modelling.
// For example, in computer graphics, it is used to determine whether a line segment
// is visible from a given point. In robotics, it is used to determine whether a robot arm
// can reach an object. In mathematical modelling, it is used in the construction of
// the Voronoi diagram, which is used to solve many problems in geometry, such as
// finding the shortest path between two points.
// This task also have a lot of applications in other fields, for example, in the construction of roads
// or finding out the best layout for an electrical scheme.

// Sweep line algorithm:
// General idea of the sweep line algorithm is to "sweep" a line across the plane
// by one of its axes, for example from left to right. At each step of the line, the algorithm
// looks at the set of line segments that the line currently intersects, sorted by y-coordinates.
// The sorted set of segments can be changed only when the sweep line goes through an end point or a crossing point.
// In the first case, the segment is removed from the set, and in the second case, the segment is added to the set
// and the order of the segments in the set may change.

import java.util.Scanner;


import static java.lang.Math.min;


public class IntersectingSegments {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // read the number of segments
        int n = sc.nextInt();
        // read an array of segments
        Segment[] segments = new Segment[n];
        for (int i = 0; i < n; i++) {
            segments[i] = new Segment(new Point(sc.nextInt(), sc.nextInt()), new Point(sc.nextInt(), sc.nextInt()));
        }
        // find intersections
        SweepLine sweepLine = new SweepLine(getPoints(segments), segments);
        Segment[] intersections = sweepLine.findIntersections();
        // print one intersection if it exists
        if (intersections != null) {
            System.out.println("INTERSECTION");
            System.out.println(intersections[0].getP1().getX() + " " + intersections[0].getP1().getY());
            System.out.println(intersections[1].getP1().getX() + " " + intersections[1].getP1().getY());
        }
        else {
            System.out.println("NO INTERSECTION");
        }


    }

    private static Point[] getPoints(Segment[] segments) {
        Point[] points = new Point[segments.length * 2];
        for (int i = 0; i < segments.length; i++) {
            points[i * 2] = segments[i].getP1();
            points[i * 2 + 1] = segments[i].getP2();
        }
        return points;
    }
}

/**
 * Sweep line algorithm
 * Uses HeapSort for sorting point coordinates and RedBlackTree for storing segments
 */
class SweepLine {
    private Point[] points;
    private Segment[] segments;
    private RedBlackTree<Segment> tree;

    public SweepLine(Point[] points, Segment[] segments) {
        this.points = points;
        this.segments = segments;
        this.tree = new RedBlackTree<Segment>();
    }

    // a method for finding intersections
    // then finds the intersection point of two segments, returns these two segments
    public Segment[] findIntersections() {
        // sort points by x-coordinate
        HeapSort<Point> heapSort = new HeapSort<>(points);
        heapSort.sort();
        // sort segments by y-coordinate
        HeapSort<Segment> heapSort1 = new HeapSort<>(segments);
        heapSort1.sort();
        // for each point
        for (Point point : points) {
            // for each segment
            for (Segment segment : segments) {
                // if the segment contains the point
                if (segment.contains(point)) {
                    // if the point is the left end point of the segment
                    if (segment.getP1().equals(point)) {
                        // add the segment to the tree
                        tree.insert(segment);
                    } else {
                        // if the point is the right end point of the segment
                        // remove the segment from the tree
                        tree.remove(segment);
                    }
                }
            }
            // if the tree contains more than one segment
            if (tree.size() > 1) {
                // find the intersection point of two segments
                Segment[] segments = tree.findIntersections(point);
                if (segments != null) {
                    return segments;
                }
            }
        }

    }

    /**
     * A class for HeapSort algorithm with generic comparable type.
     */
    class HeapSort<T extends Comparable<T>> {
        private T[] array;
        private int size;

        public HeapSort(T[] array) {
            this.array = array;
            this.size = array.length;
        }

        public void sort() {
            buildHeap();
            for (int i = size - 1; i > 0; i--) {
                swap(0, i);
                size--;
                heapify(0);
            }
        }

        private void buildHeap() {
            for (int i = size / 2 - 1; i >= 0; i--) {
                heapify(i);
            }
        }

        private void heapify(int i) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int largest = i;
            if (left < size && array[left].compareTo(array[largest]) > 0) {
                largest = left;
            }
            if (right < size && array[right].compareTo(array[largest]) > 0) {
                largest = right;
            }
            if (largest != i) {
                swap(i, largest);
                heapify(largest);
            }
        }

        private void swap(int i, int j) {
            T temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    /**
     * Intermediary class for storing the intersection point of two segments.
     * A Red-Black tree
     */
    class RedBlackTree<T extends Comparable<T>> {
        private Node<T> root;
        private int size;

        public RedBlackTree() {
            this.root = null;
            this.size = 0;
        }

        public void insert(T value) {
            Node<T> node = new Node<>(value);
            if (root == null) {
                root = node;
                root.setColor(Color.BLACK);
            } else {
                Node<T> current = root;
                Node<T> parent = null;
                while (true) {
                    parent = current;
                    if (node.getValue().compareTo(current.getValue()) < 0) {
                        current = current.getLeft();
                        if (current == null) {
                            parent.setLeft(node);
                            node.setParent(parent);
                            break;
                        }
                    } else {
                        current = current.getRight();
                        if (current == null) {
                            parent.setRight(node);
                            node.setParent(parent);
                            break;
                        }
                    }
                }
                fixTree(node);
            }
            size++;
        }

        public void remove(T value) {
            Node<T> node = findNode(value);
            if (node != null) {
                Node<T> child;
                if (node.getLeft() == null || node.getRight() == null) {
                    child = node;
                } else {
                    child = getSuccessor(node);
                }
                Node<T> parent;
                if (child.getLeft() != null) {
                    parent = child.getLeft();
                } else {
                    parent = child.getRight();
                }
                if (parent != null) {
                    parent.setParent(child.getParent());
                }
                if (child.getParent() == null) {
                    root = parent;
                } else if (child == child.getParent().getLeft()) {
                    child.getParent().setLeft(parent);
                } else {
                    child.getParent().setRight(parent);
                }
                if (child != node) {
                    node.setValue(child.getValue());
                }
                if (child.getColor() == Color.BLACK) {
                    fixTree(parent);
                }
                size--;
            }
        }

        public Node<T> findNode(T value) {
            Node<T> current = root;
            while (current != null) {
                if (value.compareTo(current.getValue()) < 0) {
                    current = current.getLeft();
                } else if (value.compareTo(current.getValue()) > 0) {
                    current = current.getRight();
                } else {
                    return current;
                }
            }
            return null;
        }

        public Node<T> getRoot() {
            return root;
        }

        public int size() {
            return size;
        }


    }

    /**
     * A class that represents a point in the plane.
     * The point is defined by two coordinates.
     */
    class Point implements Comparable<Point> {
        private int x;
        private int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public boolean isLeft() {
            return x < 0;
        }

        public Comparable<Integer> getSegment() {
            if (x == 0) {
                return y;
            } else {
                return x;
            }
        }

        public boolean isRight() {
            return x > 0;
        }

        @Override
        public int compareTo(Point o) {
            if (x == o.x) {
                return y - o.y;
            } else {
                return x - o.x;
            }
        }
    }

    /**
     * A class that represents a segment of a line in the plane.
     * The line is defined by two points.
     */
    class Segment implements Comparable<Segment> {
        private Point p1;
        private Point p2;

        public Segment(Point p1, Point p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        public Point getP1() {
            return p1;
        }

        public Point getP2() {
            return p2;
        }

        public void setP1(Point p1) {
            this.p1 = p1;
        }

        public void setP2(Point p2) {
            this.p2 = p2;
        }

        public boolean isParallel(Segment other) {
            // check if the lines are parallel
            int x1 = p1.getX();
            int y1 = p1.getY();
            int x2 = p2.getX();
            int y2 = p2.getY();
            int x3 = other.getP1().getX();
            int y3 = other.getP1().getY();
            int x4 = other.getP2().getX();
            int y4 = other.getP2().getY();
            return (x1 - x2) * (y3 - y4) == (y1 - y2) * (x3 - x4);
        }


        public boolean isIntersecting(Segment other) {
            // check if lines are parallel or coincident
            if (isParallel(other)) {
                return false;
            }
            // check if the intersection point is inside the segments
            return new Intersection(this, other).isInside();

        }

        @Override
        public int compareTo(Segment o) {
            return p1.compareTo(o.getP1());
        }

        public boolean contains(Point point) {
            // check if the point is on the line
            int x1 = p1.getX();
            int y1 = p1.getY();
            int x2 = p2.getX();
            int y2 = p2.getY();
            int x3 = point.getX();
            int y3 = point.getY();
            if ((x1 - x3) * (y2 - y3) != (x2 - x3) * (y1 - y3)) {
                return false;
            }
            // check if the point is inside the segment
            if (x1 != x2) {
                return (x1 <= x3) == (x3 <= x2);
            } else {
                return (y1 <= y3) == (y3 <= y2);
            }
        }

        public Point getIntersectionPoint(Segment segment) {
            // compute the intersection point
            int xA = p1.getX();
            int yA = p1.getY();
            int xB = p2.getX();
            int yB = p2.getY();
            int xC = segment.getP1().getX();
            int yC = segment.getP1().getY();
            int xD = segment.getP2().getX();
            int yD = segment.getP2().getY();
            int x = ((xA * yB - yA * xB) * (xC - xD) - (xA - xB) * (xC * yD - yC * xD)) /
                    ((xA - xB) * (yC - yD) - (yA - yB) * (xC - xD));
            int y = ((xA * yB - yA * xB) * (yC - yD) - (yA - yB) * (xC * yD - yC * xD)) /
                    ((xA - xB) * (yC - yD) - (yA - yB) * (xC - xD));
            return new Point(x, y);
        }

        public boolean intersects(Segment segment2) {
            // check if the segments are parallel
            if (isParallel(segment2)) {
                return false;
            }
            // check if the intersection point is inside the segments
            Point intersectionPoint = getIntersectionPoint(segment2);
            return contains(intersectionPoint) && segment2.contains(intersectionPoint);
        }

    }

    /**
     * A class that represents an intersection of two segments.
     * The intersection is defined by two segments.
     * The class only being created if the two segments are intersecting.
     */
    class Intersection {
        private Segment s1;
        private Segment s2;

        public Intersection(Segment s1, Segment s2) {
            this.s1 = s1;
            this.s2 = s2;
        }

        public Segment getS1() {
            return s1;
        }

        public Segment getS2() {
            return s2;
        }

        public void setS1(Segment s1) {
            this.s1 = s1;
        }

        public void setS2(Segment s2) {
            this.s2 = s2;
        }

        public boolean isInside() {
            // compute the intersection point
            int xA = s1.getP1().getX();
            int yA = s1.getP1().getY();
            int xB = s1.getP2().getX();
            int yB = s1.getP2().getY();
            int xC = s2.getP1().getX();
            int yC = s2.getP1().getY();
            int xD = s2.getP2().getX();
            int yD = s2.getP2().getY();

            int x = ((xA * yB - yA * xB) * (xC - xD) - (xA - xB) * (xC * yD - yC * xD)) / ((xA - xB) * (yC - yD) - (yA - yB) * (xC - xD));
            int y = ((xA * yB - yA * xB) * (yC - yD) - (yA - yB) * (xC * yD - yC * xD)) / ((xA - xB) * (yC - yD) - (yA - yB) * (xC - xD));
            // check if the intersection point is inside the segments
            if (x < min(xA, xB) || x > Math.max(xA, xB) || x < min(xC, xD) || x > Math.max(xC, xD)) {
                return false;
            }
            return y >= min(yA, yB) && y <= Math.max(yA, yB) && y >= min(yC, yD) && y <= Math.max(yC, yD);

        }
    }
}








