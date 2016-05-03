package pl.tlasica.firewireapp.engine;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

import pl.tlasica.firewireapp.model.LevelPlay;

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
                if (graph.getEdgeSource(e) == play.board.target) {
                    return GameStatus.WIN;
                }
            }
            return GameStatus.LOST;
        }
        return GameStatus.NOT_FINISHED;
    }
}
