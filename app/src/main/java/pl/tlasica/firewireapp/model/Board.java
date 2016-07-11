package pl.tlasica.firewireapp.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Board description is a level description including nodes, wires,
 * initially placed connectors, plus and minus locations as well as target location
 * locations are described as int
 *
 * It keeps also information about:
 * - available connectors : type -> count
 * - predefined connectors : node -> connected directions
 */
public class Board {

    public Set<Integer> nodes = new HashSet<>();
    public Set<Wire>    wires = new HashSet<>();
    public int          plus = -1;
    public int          minus = -1;
    public int          target = -1;
    public String       title;
    public Map<ConnectorType, Integer> connectors = new HashMap<>();
    public Map<Integer, DefinedConnector> definedConnectors = new HashMap<>();

    public Board() {
    }

    // add wire a---b
    public Board withWire(int a, int b) {
        nodes.add(a);
        nodes.add(b);
        wires.add(new Wire(a,b));
        return this;
    }

    // add node n
    public Board withNode(int n) {
        nodes.add(n);
        return this;
    }

    // add array of nodes
    public Board withNodes(int [] n) {
        for (int x : n) {
            nodes.add(x);
        }
        return this;
    }

    // remove node
    public Board removeNode(int n) {
        nodes.remove(n);
        List<Wire> adj = adj(n);
        wires.removeAll(adj);
        return this;
    }

    // remove wire a---b but keeps both nodes in the board
    public Board removeWire(int a, int b) {
        wires.remove(new Wire(a, b));
        return this;
    }

    // creates full matrix of orthogonal wires
    public Board matrix(int xSize, int ySize) {
        for (int x = 0; x < xSize; ++x) {
            for (int y = 0; y < ySize; ++y) {
                this.withWire(IntCoord.i(x, y), IntCoord.i(x + 1, y));
                this.withWire(IntCoord.i(x, y), IntCoord.i(x, y + 1));
            }
        }
        for (int x = 0; x < xSize; ++x) {
            this.withWire(IntCoord.i(x, ySize), IntCoord.i(x + 1, ySize));
        }
        for (int y = 0; y < ySize; ++y) {
            this.withWire(IntCoord.i(xSize, y), IntCoord.i(xSize, y + 1));
        }
        return this;
    }

    // list wires adjecent to given node
    public List<Wire> adj(int n) {
        List<Wire> out = new ArrayList<>();
        for (Wire w: wires) {
            if ((w.a==n) || (w.b==n)) out.add(w);
        }
        return out;
    }

    // list nodes connected to given node by existing wires
    public List<Integer> adjNodes(int n) {
        List<Integer> out = new ArrayList<>();
        for (Wire w: wires) {
            if (w.a == n) out.add(w.b);
            else if (w.b == n) out.add(w.a);
        }
        return out;
    }

    // helper method to search for int v in given array
    private static boolean isIn(int[] arr, int v) {
        for(int x: arr) {
            if (v==x) return true;
        }
        return false;
    }

    // return all nodes which are connected with node n when connector of type "type" is placed
    public List<Integer> connectedNodes(int n, PlacedConnector connector) {
        List<Integer> ret = new ArrayList<>();
        int[] directions = connector.directions();
        for(Wire w: adj(n)) {
            int d = w.dirFrom(n);
            if (isIn(directions, d)) {
                ret.add(w.a!=n ? w.a : w.b);
            }
        }
        return ret;
    }

    public int[] possibleRotations(ConnectorType type, int position) {
        int [] all = {0, 45, 90, 135, 180, 225, 270, 315};
        List<Wire> adjWires = adj(position);
        // at least as many wires as conn directions
        if (adjWires.size() < type.directions(0).length) {
            return null;
        }
        // calculate adj directions as lookup array
        boolean [] adjDirs = new boolean[8];
        for(Wire w: adjWires) {
            int d = w.dirFrom(position);
            adjDirs[d / 45] = true;
        }
        // check all possible rotations
        int [] acceptable = new int[8];
        int found = 0;
        for(int rot: all) {
            // for each deg in rotated conn deg there should be matching wire
            int [] dirs = type.directions(rot);
            boolean match = true;
            for(int d: dirs) {
                int dp = d / 45;
                if (adjDirs[dp]==false) {
                    match = false;
                    break;
                }
            }
            if (match) {
                acceptable[found] = rot;
                found ++;
            }
        }
        return (found>0) ? Arrays.copyOf(acceptable, found) : null;
    }

    public int xSize() {
        return 1 + IntCoord.x( Collections.max(nodes) );
    }

    public int ySize() {
        int maxY = 0;
        for(int n: nodes) if (IntCoord.y(n)>maxY) maxY=IntCoord.y(n);
        return maxY;
    }

    public boolean isFree(int node) {
        if (!nodes.contains(node)) return false;
        if (isSpecial(node)) return false;
        if (definedConnectors.containsKey(node)) return false;
        return true;
    }

    public boolean isSpecial(int node) {
        return (node==target || node==minus || node==plus);
    }

}
