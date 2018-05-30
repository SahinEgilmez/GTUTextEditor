package tr.edu.gtu.cse.gte;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import java.util.*;

/**
 * Represents a graph.
 *
 * @param <T> type of the vertices in the graph
 */
public class Graph<T> implements edu.uci.ics.jung.graph.Graph<T, Edge<T>> {
    // vertices
    private HashMap<String, T> vertices;
    // edges
    private HashSet<Edge<T>> edges;

    /**
     * Constructs an empty graph.
     */
    public Graph() {
        vertices = new HashMap<>();
        edges = new HashSet<>();
    }

    /**
     * Adds a new vertex to the graph.
     *
     * @param label label for new vertex
     * @param vertex the new vertex
     */
    public void addVertex(String label, T vertex) {
        vertices.put(label, vertex);
    }

    /**
     * Returns the vertex associated with the label {@code label} from the
     * graph.
     *
     * @param label a label
     * @return the vertex associated with the {@code label}
     */
    public T getVertex(String label) {
        return vertices.get(label);
    }

    /**
     * Adds a new edge from the vertex labeled with {@code from} to the vertex
     * labeled with {@code to}. Sets if it is directed and its weight.
     *
     * @param from label for the source vertex
     * @param to label for the destination vertex
     * @param isDirected whether the edge is directed or not
     * @param weight weight of the edge
     */
    public void addEdge(String from, String to, boolean isDirected,
                        double weight) {
        edges.add(new Edge<>(vertices.get(from), vertices.get(to),
                isDirected, weight));
    }

    /**
     * Adds a new edge from the vertex labeled with {@code from} to the vertex
     * labeled with {@code to}, using default weight of 1.0. Sets if it is
     * directed.
     *
     * @param from label for the source vertex
     * @param to label for the destination vertex
     * @param isDirected whether the edge is directed or not
     */
    public void addEdge(String from, String to, boolean isDirected) {
        edges.add(new Edge<>(vertices.get(from), vertices.get(to), isDirected));
    }

    /**
     * Adds a new undirected edge from the vertex labeled with {@code from} to
     * the vertex labeled with {@code to}. Sets its weight.
     *
     * @param from label for the source vertex
     * @param to label for the destination vertex
     * @param weight weight of the edge
     */
    public void addEdge(String from, String to, double weight) {
        edges.add(new Edge<>(vertices.get(from), vertices.get(to), weight));
    }

    /**
     * Adds a new undirected edge from the vertex labeled with {@code from} to
     * the vertex labeled with {@code to}, using default weight of 1.0.
     *
     * @param from label for the source vertex
     * @param to label for the destination vertex
     */
    public void addEdge(String from, String to) {
        edges.add(new Edge<>(vertices.get(from), vertices.get(to)));
    }

    /**
     * Returns a random vertex from the graph.
     *
     * @return a random vertex
     */
    public T randomVertex() {
        ArrayList<T> verticesArrayList = new ArrayList<>();
        for (String label : vertices.keySet()) {
            verticesArrayList.add(vertices.get(label));
        }

        int listLength = verticesArrayList.size();

        T randomVertex = null;
        if (listLength > 0) {
            randomVertex = verticesArrayList.get(
                    (new Random()).nextInt(listLength));
        }

        return randomVertex;
    }

    /**
     * Returns a random vertex from the graph which is a neighbor of the
     * vertex {@code t}.
     *
     * @param t a vertex
     * @return a random neighbor vertex
     */
    public T randomNeighbor(T t) {
        ArrayList<T> verticesArrayList = new ArrayList<>();
        for (T vertex : getNeighbors(t)) {
            verticesArrayList.add(vertex);
        }

        T randomVertex = verticesArrayList.get(
                (new Random()).nextInt(verticesArrayList.size()));
        return randomVertex;
    }

    /**
     * Returns a collection of the edges in the graph.
     *
     * @return edges
     */
    @Override
    public Collection<Edge<T>> getEdges() {
        return edges;
    }

    /**
     * Returns a collection of the vertices in the graph.
     *
     * @return vertices
     */
    @Override
    public Collection<T> getVertices() {
        return vertices.values();
    }

    /**
     * Returns if the graph contains the vertex {@code t}.
     *
     * @param t a vertex
     * @return true if the graph contains {@code t}, false otherwise
     */
    @Override
    public boolean containsVertex(T t) {
        return getVertices().contains(t);
    }

    /**
     * Returns if the graph contains the edge {@code edge}.
     *
     * @param edge an edge
     * @return true if the graph contains {@code edge}, false otherwise
     */
    @Override
    public boolean containsEdge(Edge edge) {
        return getEdges().contains(edge);
    }

    /**
     * Returns the number of the edges in the graph.
     * @return the number of the edges
     */
    @Override
    public int getEdgeCount() {
        return edges.size();
    }

    /**
     * Returns the number of the vertices in the graph.
     * @return the number of the vertices
     */
    @Override
    public int getVertexCount() {
        return vertices.size();
    }

    /**
     * Returns the vertices connected to the vertex {@code t}.
     *
     * @param t a vertex
     * @return neighbor set of the {@code t}
     */
    @Override
    public Collection<T> getNeighbors(T t) {
        HashSet<T> neighbors = new HashSet<>();
        // find all neighbors
        for (Edge<T> edge : edges) {
            if (edge.getSource().equals(t)) {
                neighbors.add(edge.getDestination());
            }
            else if (edge.getDestination().equals(t)) {
                neighbors.add(edge.getSource());
            }
        }

        return neighbors;
    }

    /**
     * Returns the edges connected to the vertex {@code t}
     *
     * @param t a vertex
     * @return set of the edges connected to {@code t}
     */
    @Override
    public Collection<Edge<T>> getIncidentEdges(T t) {
        HashSet<Edge<T>> incidents = new HashSet<>();
        // find all incident edges
        for (Edge<T> edge : edges) {
            if (edge.getSource().equals(t)
                    || edge.getDestination().equals(t)) {
                incidents.add(edge);
            }
        }

        return incidents;
    }

    /**
     * Returns the vertices connected to the edge {@code edge}
     *
     * @param edge an edge
     * @return set of the vertices connected to {@code edge}
     */
    @Override
    public Collection<T> getIncidentVertices(Edge<T> edge) {
        HashSet<T> incidents = new HashSet<>();
        incidents.add(edge.getSource());
        incidents.add(edge.getDestination());

        return incidents;
    }

    /**
     * Returns the edge in the graph that connects vertex {@code t} to
     * {@code v}.
     *
     * @param t a vertex
     * @param v1 another vertex
     * @return the edge between the {@code t} and the {@code v1}
     */
    @Override
    public Edge<T> findEdge(T t, T v1) {
        for (Edge<T> edge : edges) {
            if (
                    edge.getSource().equals(t)
                        && edge.getDestination().equals(v1)
                    || edge.getSource().equals(v1)
                        && edge.getDestination().equals(t)
                ) {
                return edge;
            }
        }
        return null;
    }

    /**
     * Returns the edge set in the graph that connects vertex {@code t}
     * to {@code v}.
     *
     * @param t a vertex
     * @param v1 another vertex
     * @return the set of the edges between the {@code t} and the {@code v1}
     */
    @Override
    public Collection<Edge<T>> findEdgeSet(T t, T v1) {
        HashSet<Edge<T>> edgeSet = new HashSet<>();
        edgeSet.add(findEdge(t, v1));
        return edgeSet;
    }

    @Override
    public boolean addVertex(T t) {
        return false;
    }

    @Override
    public boolean addEdge(Edge<T> edge, Collection<? extends T> collection) {
        return false;
    }

    @Override
    public boolean addEdge(Edge<T> edge, Collection<? extends T> collection, EdgeType edgeType) {
        return false;
    }

    @Override
    public boolean removeVertex(T t) {
        return false;
    }

    @Override
    public boolean removeEdge(Edge edge) {
        return false;
    }

    /**
     * Returns if the vertex {@code t} is a neighbor of the vertex {@code v1}.
     *
     * @param t a vertex
     * @param v1 another vertex
     * @return true if the {@code t} and the {@code v1} is neighbors,
     * false otherwise
     */
    @Override
    public boolean isNeighbor(T t, T v1) {
        for (Edge<T> edge : edges) {
            if (
                    edge.getSource().equals(t)
                        && edge.getDestination().equals(v1)
                    || edge.getSource().equals(v1)
                        && edge.getDestination().equals(t)
                ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns if the vertex {@code t} and the edge {@code edge} is incident
     * to each other.
     *
     * @param t a vertex
     * @param edge an edge
     * @return true if the {@code t} and the {@code edge} are incidents
     */
    @Override
    public boolean isIncident(T t, Edge<T> edge) {
        return edge.getSource().equals(t) || edge.getDestination().equals(t);
    }

    /**
     * Returns the number of edges incident to the vertex {@code t}.
     *
     * @param t a vertex
     * @return the number of the incident edges
     */
    @Override
    public int degree(T t) {
        return getIncidentEdges(t).size();
    }

    /**
     * Returns the number of neighbor vertices of the vertex {@code t}.
     *
     * @param t a vertex
     * @return the number of the neighbor vertices
     */
    @Override
    public int getNeighborCount(T t) {
        return getNeighbors(t).size();
    }

    /**
     * Returns the number of vertices incident to the edge {@code edge}.
     *
     * @param edge an edge
     * @return the number of the incident vertices
     */
    @Override
    public int getIncidentCount(Edge<T> edge) {
        return getIncidentVertices(edge).size();
    }

    /**
     * Returns edge type of the edge. (directed or undirected)
     *
     * @param edge an edge
     * @return edge type
     */
    @Override
    public EdgeType getEdgeType(Edge edge) {
        return edge.isDirected() ? EdgeType.DIRECTED : EdgeType.UNDIRECTED;
    }

    /**
     * Returns the default edge type. (undirected)
     *
     * @return the default edge type
     */
    @Override
    public EdgeType getDefaultEdgeType() {
        return EdgeType.UNDIRECTED;
    }

    /**
     * Returns all edges with edge type {@code edgeType}.
     *
     * @param edgeType an edge type
     * @return collection of edges with type {@code edgeType}
     */
    @Override
    public Collection<Edge<T>> getEdges(EdgeType edgeType) {
        HashSet<Edge<T>> edgesOfSpecificType = new HashSet<>();
        if (edgeType == EdgeType.DIRECTED) {
            for (Edge<T> edge : edges) {
                if (edge.isDirected()) {
                    edgesOfSpecificType.add(edge);
                }
            }
        }
        else {
            for (Edge<T> edge : edges) {
                if (!edge.isDirected()) {
                    edgesOfSpecificType.add(edge);
                }
            }

        }
        return edgesOfSpecificType;
    }

    /**
     * Returns the number of edges with edge type {@code edgeType}.
     *
     * @param edgeType an edge type
     * @return number of edges with type {@code edgeType}
     */
    @Override
    public int getEdgeCount(EdgeType edgeType) {
        return getEdges(edgeType).size();
    }

    /**
     * Returns all incoming edges to the vertex {@code t}.
     *
     * @param t a vertex
     * @return collection of incoming edges of the {@code t}
     */
    @Override
    public Collection<Edge<T>> getInEdges(T t) {
        HashSet<Edge<T>> incoming = new HashSet<>();
        // find all incoming edges
        for (Edge<T> edge : edges) {
            if (edge.getDestination().equals(t)) {
                incoming.add(edge);
            }
        }

        return incoming;
    }

    /**
     * Returns all outgoing edges from the vertex {@code t}.
     *
     * @param t a vertex
     * @return collection of outgoing edges of the {@code t}
     */
    @Override
    public Collection<Edge<T>> getOutEdges(T t) {
        HashSet<Edge<T>> outgoing = new HashSet<>();
        // find all outgoing edges
        for (Edge<T> edge : edges) {
            if (edge.getSource().equals(t)) {
                outgoing.add(edge);
            }
        }

        return outgoing;
    }

    /**
     * Returns all predecessor vertices of the vertex {@code t}.
     *
     * @param t a vertex
     * @return collection or predecessor vertices
     */
    @Override
    public Collection<T> getPredecessors(T t) {
        HashSet<T> predecessors = new HashSet<>();
        // find all predecessor vertices
        for (Edge<T> edge : edges) {
            if (edge.getDestination().equals(t)) {
                predecessors.add(edge.getSource());
            }
        }

        return predecessors;
    }

    /**
     * Returns all successor vertices of the vertex {@code t}.
     *
     * @param t a vertex
     * @return collection or successor vertices
     */
    @Override
    public Collection<T> getSuccessors(T t) {
        HashSet<T> successors = new HashSet<>();
        // find all successor vertices
        for (Edge<T> edge : edges) {
            if (edge.getSource().equals(t)) {
                successors.add(edge.getDestination());
            }
        }

        return successors;
    }

    /**
     * Returns the number of incoming edges to vertex {@code t}.
     *
     * @param t a vertex
     * @return the number of the incoming edges to {@code t}
     */
    @Override
    public int inDegree(T t) {
        return getInEdges(t).size();
    }

    /**
     * Returns the number of outgoing edges from vertex {@code t}.
     *
     * @param t a vertex
     * @return the number of the outgoing edges from {@code t}
     */
    @Override
    public int outDegree(T t) {
        return getOutEdges(t).size();
    }

    /**
     * Return whether the vertex {@code v1} is a predecessor of the vertex
     * {@code t}.
     *
     * @param t a vertex
     * @param v1 another vertex
     * @return true if {@code v1} is a predecessor of {@code t},
     * false otherwise
     */
    @Override
    public boolean isPredecessor(T t, T v1) {
        return getPredecessors(t).contains(v1);
    }

    /**
     * Return whether the vertex {@code v1} is a successor of the vertex
     * {@code t}.
     *
     * @param t a vertex
     * @param v1 another vertex
     * @return true if {@code v1} is a successor of {@code t},
     * false otherwise
     */
    @Override
    public boolean isSuccessor(T t, T v1) {
        return getSuccessors(t).contains(v1);
    }

    /**
     * Returns the number of the predecessor vertices of the vertex {@code t}.
     *
     * @param t a vertex
     * @return the number of the predecessors
     */
    @Override
    public int getPredecessorCount(T t) {
        return getPredecessors(t).size();
    }

    /**
     * Returns the number of the successor vertices of the vertex {@code t}.
     *
     * @param t a vertex
     * @return the number of the successors
     */
    @Override
    public int getSuccessorCount(T t) {
        return getSuccessors(t).size();
    }

    /**
     * Returns the source vertex of the edge {@code edge}.
     *
     * @param edge an edge
     * @return source vertex of the {@code edge}
     */
    @Override
    public T getSource(Edge<T> edge) {
        return edge.getSource();
    }

    /**
     * Returns the destination vertex of the edge {@code edge}.
     *
     * @param edge an edge
     * @return destination vertex of the {@code edge}
     */
    @Override
    public T getDest(Edge<T> edge) {
        return edge.getDestination();
    }

    /**
     * Returns if the vertex {@code t} is the source vertex of the edge
     * {@code edge}.
     *
     * @param t a vertex
     * @param edge an edge
     * @return true if the {@code t} is the source vertex of the {@code edge},
     * false otherwise
     */
    @Override
    public boolean isSource(T t, Edge<T> edge) {
        return edge.getSource().equals(t);
    }

    /**
     * Returns if the vertex {@code t} is the destination vertex of the edge
     * {@code edge}.
     *
     * @param t a vertex
     * @param edge an edge
     * @return true if the {@code t} is the destination vertex of the
     * {@code edge}, false otherwise
     */
    @Override
    public boolean isDest(T t, Edge<T> edge) {
        return edge.getDestination().equals(t);
    }

    @Override
    public boolean addEdge(Edge<T> edge, T t, T v1) {
        return false;
    }

    @Override
    public boolean addEdge(Edge<T> edge, T t, T v1, EdgeType edgeType) {
        return false;
    }

    /**
     * Returns the vertices connected by the edge {@code edge} as a Pair.
     *
     * @param edge an edge
     * @return the vertices connected by the {@code edge}
     */
    @Override
    public Pair<T> getEndpoints(Edge<T> edge) {
        return new Pair<T>(edge.getSource(), edge.getDestination());
    }

    /**
     * Returns the vertex at the other end of the edge {@code edge}.
     *
     * @param t a vertex
     * @param edge an edge
     * @return the vertex at the other end fo the {@code edge}
     */
    @Override
    public T getOpposite(T t, Edge<T> edge) {
        if (edge.getSource().equals(t)) {
            return edge.getDestination();
        }
        else if (edge.getDestination().equals(t)) {
            return edge.getSource();
        }
        else {
            return null;
        }
    }
}
