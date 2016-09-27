package pl.tlasica.firewire.engine;

import android.util.Log;

import org.jgrapht.*;
import org.jgrapht.graph.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.tlasica.firewire.model.Board;
import pl.tlasica.firewire.model.DefinedConnector;
import pl.tlasica.firewire.model.LevelPlay;
import pl.tlasica.firewire.model.PlacedConnector;

/**
 * Transforms LevelPlay to graph
 * to make possible short path algorithms
 */
public class PlayGraphBuilder {

    UndirectedGraph<Integer, DefaultEdge> graph;

    public UndirectedGraph<Integer, DefaultEdge> create(LevelPlay play) {
        graph = new SimpleGraph<>(DefaultEdge.class);
        this.addNodes(play.board);
        this.addEdges(play);
        Log.i("GRAPH", graph.toString());
        return graph;
    }

    private void addEdges(LevelPlay play) {
        // for each node using placed connectors or defined connections
        // create a map node->Set(maybe connected nodes)
        Map<Integer, Set<Integer>> halfEdges = new HashMap<>();
        for (Map.Entry<Integer, PlacedConnector> item : play.placedConnectors.entrySet()) {
            int node = item.getKey();
            PlacedConnector conn = item.getValue();
            List<Integer> adjNodes = play.board.connectedNodes(node, conn);
            update(halfEdges, node, adjNodes);
        }
        for (Map.Entry<Integer, DefinedConnector> item : play.board.definedConnectors.entrySet()) {
            int node = item.getKey();
            DefinedConnector conn = item.getValue();
            List<Integer> adjNodes = play.board.connectedNodes(node, conn);
            update(halfEdges, node, adjNodes);
        }
        // create edge if a->b and b->a
        for(Map.Entry<Integer, Set<Integer>> item: halfEdges.entrySet()) {
            int node = item.getKey();
            for(int adjNode: item.getValue()) {
                if (node<adjNode) {
                    Set<Integer> adjNodeAdj = halfEdges.get(adjNode);
                    if (adjNodeAdj != null && adjNodeAdj.contains(node)) {
                        graph.addEdge(node, adjNode);
                    }
                }
            }
        }
    }

    private static void update(Map<Integer, Set<Integer>> aMap, int node, List<Integer> adj) {
        Set<Integer> nodeAdj = aMap.get(node);
        if (nodeAdj==null) nodeAdj = new HashSet<>();
        nodeAdj.addAll(adj);
        aMap.put(node, nodeAdj);
    }

    private void addNodes(Board board) {
        for(Integer n: board.nodes) {
            graph.addVertex(n);
        }


    }
}
