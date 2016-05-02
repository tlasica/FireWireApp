package pl.tlasica.firewireapp.engine;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.graph.builder.UndirectedGraphBuilder;

import pl.tlasica.firewireapp.model.Board;
import pl.tlasica.firewireapp.model.LevelPlay;

/**
 * Transforms LevelPlay to graph
 * to make possible short path algorithms
 */
public class GraphBulder {

    UndirectedGraph<Integer, DefaultEdge> graph;

    public UndirectedGraph<Integer, DefaultEdge> create(LevelPlay play) {
        graph = new SimpleGraph<>(DefaultEdge.class);
        this.addNodes(play.board);
        this.addEdges(play);
        return graph;
    }

    private void addEdges(LevelPlay play) {

    }

    private void addNodes(Board board) {
        for(Integer n: board.nodes) {
            graph.addVertex(n);
        }


    }
}
