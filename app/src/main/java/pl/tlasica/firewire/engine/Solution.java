package pl.tlasica.firewire.engine;

import android.util.Log;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

import pl.tlasica.firewire.model.LevelPlay;

/**
 *
 */
public class Solution {

    public enum GameStatus {
        WIN,
        LOST,
        NOT_FINISHED
    }

    public static GameStatus solution(LevelPlay play) {
        PlayGraphBuilder gb = new PlayGraphBuilder();
        UndirectedGraph<Integer, DefaultEdge> graph = gb.create(play);
        List<DefaultEdge> path = DijkstraShortestPath.findPathBetween(graph, play.board.plus, play.board.minus);
        if (path!=null && !path.isEmpty()) {
            for (DefaultEdge e : path) {
                Log.d("Path", e.toString());
                if (graph.getEdgeSource(e) == play.board.target || graph.getEdgeTarget(e)==play.board.target) {
                    return GameStatus.WIN;
                }
            }
            // path of length 2 is not a solution as it may happen during rotating the connector
            if (path.size()>2) return GameStatus.LOST;
        }
        return GameStatus.NOT_FINISHED;
    }
}
