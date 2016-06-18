package pl.tlasica.firewireapp.play;

import pl.tlasica.firewireapp.model.Board;

/**
 * (200*W/sqrt(t))*(1-P/75)*(1-L/50)*(1-O/200)

 t - czas w sekundach
 W - wymiar planszy (jeden, nie iloczyn z wymiarów macierzy) ^ 1.5
 P - liczba zdjętych z planszy konektorów
 L - liczba wykorzystanych konektorów
 O - liczba obrotów konektorami
 */
public class PointCalculator {

    public static int points(GameLoopStatistics stats, Board board) {
        double t = timeValue(stats, board);
        double p = pValue(stats);
        double l = lValue(stats);
        double o = oValue(stats);
        return (int)Math.floor(t * p * l * o);
    }

    private static double timeValue(GameLoopStatistics stats, Board board) {
        int size = Math.max(board.xSize(), board.ySize());
        double w = Math.pow(size, 1.5);
        return 200 * w / Math.sqrt(stats.durationSec());
    }

    private static double pValue(GameLoopStatistics stats) {
        int p = stats.numRemove + stats.numMove;
        return (1.0 - p/50.0);
    }

    private static double lValue(GameLoopStatistics stats) {
        int l = stats.numPlace - stats.numRemove;
        return (1.0 - l/50.0);
    }

    private static double oValue(GameLoopStatistics stats) {
        int o = stats.numRotate;
        return (1.0 - o/200.0);
    }

}
