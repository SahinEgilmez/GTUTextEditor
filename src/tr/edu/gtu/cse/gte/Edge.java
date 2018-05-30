package tr.edu.gtu.cse.gte;

/**
 * Represents an edge in the graph structure.
 *
 * @param <T> type of the vertices linked by this edge
 */
public class Edge<T> {
    // source vertex
    private T source;
    // destination vertex
    private T destination;
    // whether the edge is directed or not
    private boolean isDirected;
    // edge weight
    private double weight;

    /**
     * Constructs an edge between {@code source} and {@code destination}. Sets
     * its weight and whether its directed or not.
     *
     * @param source source vertex
     * @param destination destination vertex
     * @param isDirected whether the edge is directed or not
     * @param weight weight of the edge
     */
    public Edge(T source, T destination, boolean isDirected, double weight) {
        this.source = source;
        this.destination = destination;
        this.isDirected = isDirected;
        this.weight = weight;
    }

    /**
     * Constructs an edge between {@code source} and {@code destination}. Sets
     * its weight to 1.0 and whether its directed or not.
     *
     * @param source source vertex
     * @param destination destination vertex
     * @param isDirected whether the edge is directed or not
     */
    public Edge(T source, T destination, boolean isDirected) {
        this(source, destination, isDirected, 1.0);
    }

    /**
     * Constructs an edge between {@code source} and {@code destination}. Sets
     * its weight and makes it undirected.
     *
     * @param source source vertex
     * @param destination destination vertex
     * @param weight weight of the edge
     */
    public Edge(T source, T destination, double weight) {
        this(source, destination, false, weight);
    }

    /**
     * Constructs an edge between {@code source} and {@code destination}. Sets
     * its weight to 1.0 and makes it undirected.
     *
     * @param source source vertex
     * @param destination destination vertex
     */
    public Edge(T source, T destination) {
        this(source, destination, false, 1.0);
    }

    /**
     * Returns the source vertex.
     *
     * @return the source vertex
     */
    T getSource() {
        return this.source;
    }

    /**
     * Returns the destination vertex.
     *
     * @return the destination vertex
     */
    T getDestination() {
        return this.destination;
    }

    /**
     * Return whether the edge is directed or not.
     *
     * @return if the edge is directed
     */
    boolean isDirected() {
        return this.isDirected;
    }

    /**
     * Return the weight of the edge.
     *
     * @return weight of the edge
     */
    double getWeight() {
        return this.weight;
    }
}
