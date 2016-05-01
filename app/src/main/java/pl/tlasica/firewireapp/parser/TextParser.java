package pl.tlasica.firewireapp.parser;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.tlasica.firewireapp.model.Board;
import pl.tlasica.firewireapp.model.ConnectorType;
import pl.tlasica.firewireapp.model.IntCoord;

/**
 * Translates text description into Board object
 *
 /*
 (x,y)         # add Node(x,y) with empty connector
 [x,y]         # add Node(x,y) with full connector
 (x,y)~(a,b)   # add Node(x,y) with empty connector and wires to (a,b) with empty conn
 (x,y)~[a,b]   # add Node(x,y) with empty connector and wires to (a,b) with full conn
 -(x,y)        # removes node
 -(x,y)~(a,b)  # removes wire (but keeps nodes!)
 (x,y)=N,S     # assign connector to node
 (x,y)=N,E,W   # assign connector to node
 MATRIX(3,4)   # dodaje kratę 3x4 od punku (0,0) ---> i ^
 PLUS(x,y)     # punkt wpływuj prundu
 MINUS(x,y)    # punkt wypływu prundu
 TARGET(x,y)
 */
public class TextParser {

    Pattern emptyNodePattern = Pattern.compile("\\((\\d+),[ ]*(\\d+)\\)");
    Pattern fullNodePattern = Pattern.compile(	"\\[(\\d+),[ ]*(\\d+)\\]");
    Pattern titlePattern = Pattern.compile("T:(.*)");
    Pattern matrixPattern = Pattern.compile("MATRIX\\((\\d+),[ ]*(\\d+)\\)");
    Pattern nodeConnPattern = Pattern.compile("\\((\\d+),[ ]*(\\d+)\\)[ ]*=[ ]*([NSEW,]+)");
    Pattern specialPattern = Pattern.compile("([A-Z]+)\\((\\d+),[ ]*(\\d+)\\)");
    Pattern availConnPattern = Pattern.compile("C(.+)=(\\d+)");

    public Board parse(List<String> lines) {
        Board board = new Board();
        for(String line: lines) {
            if (tryMatrix(board, line)) continue;
            if (tryTitle(board, line)) continue;
            if (tryEmptyNode(board, line)) continue;
            if (tryFullNode(board, line)) continue;
            if (tryRemoveWire(board, line)) continue;
            if (tryRemoveNode(board, line)) continue;
            if (tryAddWire(board, line)) continue;
            if (trySpecial(board, line)) continue;
            if (tryAvailableConnector(board, line)) continue;
            // TODO: connector
            // TODO: fail on unknown command
        }
        return board;
    }

    private boolean tryAvailableConnector(Board board, String line) {
        Matcher m = availConnPattern.matcher(line);
        if (m.matches()) {
            String name = m.group(1);
            int count = Integer.parseInt(m.group(2));
            ConnectorType type = connectorType(name);
            if (type != null) {
                board.connectors.put(type, count);
                return true;
            }
            else throw new IllegalArgumentException(line);
        }
        return false;
    }

    private ConnectorType connectorType(String name) {
        if (name.equalsIgnoreCase("90")) return ConnectorType.L_SHAPE;
        if (name.equalsIgnoreCase("180")) return ConnectorType.I_SHAPE;
        if (name.equalsIgnoreCase("X")) return ConnectorType.X_SHAPE;
        if (name.equalsIgnoreCase("L")) return ConnectorType.L_SHAPE;
        if (name.equalsIgnoreCase("I")) return ConnectorType.I_SHAPE;
        if (name.equalsIgnoreCase("T")) return ConnectorType.T_SHAPE;
        return null;
    }

    private boolean trySpecial(Board board, String line) {
        Matcher m = specialPattern.matcher(line);
        if (m.matches()) {
            String name = m.group(1);
            int x = Integer.parseInt(m.group(2));
            int y = Integer.parseInt(m.group(3));
            int node = IntCoord.i(x, y);
            if (name.equalsIgnoreCase("PLUS")) {
                board.plus = node;
                return true;
            }
            if (name.equalsIgnoreCase("MINUS")) {
                board.minus = node;
                return true;
            }
            if (name.equalsIgnoreCase("TARGET")) {
                board.target = node;
                return true;
            }
        }
        return false;
    }

    private boolean tryMatrix(Board b, String line) {
        Matcher m = matrixPattern.matcher(line);
        if (m.matches()) {
            int xSize = Integer.parseInt(m.group(1));
            int ySize = Integer.parseInt(m.group(2));
            b.matrix(xSize, ySize);
            return true;
        }
        return false;
    }

    private boolean tryEmptyNode(Board b, String line) {
        Matcher m = emptyNodePattern.matcher(line);
        if (m.matches()) {
            int node = nodeCoord(m);
            b.withNode(node);
            return true;
        }
        return false;
    }

    private boolean tryFullNode(Board b, String line) {
        Matcher m = fullNodePattern.matcher(line);
        if (m.matches()) {
            int node = nodeCoord(m);
            b.withNode(node);
            //TODO: place full connector
            return true;
        }
        return false;
    }

    private boolean tryTitle(Board b, String line) {
        Matcher m = titlePattern.matcher(line);
        if (m.matches()) {
            b.title = m.group(1);
            //TODO: set a title for the board
            return true;
        }
        return false;
    }

    private boolean tryRemoveNode(Board b, String line) {
        if (line.startsWith("-")) {
            Matcher m = emptyNodePattern.matcher(line.substring(1));
            if (m.matches() && m.hitEnd()) {
                int node = nodeCoord(m);
                b.removeNode(node);
                return true;
            }
        }
        return false;
    }

    private boolean tryAddWire(Board b, String line) {
        if (!line.startsWith("-") && line.contains("~")) {
            String[] nodes = line.split("~");
            Matcher mFromEmpty = emptyNodePattern.matcher(nodes[0]);
            Matcher mFromFull = fullNodePattern.matcher(nodes[0]);
            Matcher mToEmpty = emptyNodePattern.matcher(nodes[1]);
            Matcher mToFull = fullNodePattern.matcher(nodes[1]);
            int nodeFrom = nodeCoord(mFromEmpty);
            if (nodeFrom<0) nodeFrom = nodeCoord(mFromFull);
            int nodeTo = nodeCoord(mToEmpty);
            if (nodeTo<0) nodeTo = nodeCoord(mToFull);
            b.withWire(nodeFrom, nodeTo);
            //TODO: add connector if full nodeTo or nodeFrom
            return true;
        }
        return false;
    }

    private boolean tryRemoveWire(Board b, String line) {
        if (line.startsWith("-") && line.contains("~")) {
            String[] nodes = line.substring(1).split("~");
            Matcher mFrom = emptyNodePattern.matcher(nodes[0]);
            Matcher mTo = emptyNodePattern.matcher(nodes[1]);
            int nodeFrom = nodeCoord(mFrom);
            int nodeTo = nodeCoord(mTo);
            b.removeWire(nodeFrom, nodeTo);
            return true;
        }
        return false;
    }

    private int nodeCoord(Matcher m) {
        if (m.matches()) {
            int x = Integer.parseInt(m.group(1));
            int y = Integer.parseInt(m.group(2));
            return IntCoord.i(x, y);
        }
        return -1;
    }
}
