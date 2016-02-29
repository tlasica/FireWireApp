package pl.tlasica.firewireapp.model;

import java.util.ArrayList;
import java.util.Arrays;
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
 */
public class Board {

    public Set<Integer> nodes = new HashSet<>();
    public Set<Wire>    wires = new HashSet<>();
    public Map<ConnectorType, Integer> connectors = new HashMap<>();
    public int          plus = -1;
    public int          minus = -1;
    public int          target = -1;

    public Board() {

    }

    // add wire a---b
    public Board with(int a, int b) {
        nodes.add(a);
        nodes.add(b);
        wires.add(new Wire(a,b));
        return this;
    }

    // add node n
    public Board with(int n) {
        nodes.add(n);
        return this;
    }

    // add array of nodes
    public Board with(int [] n) {
        for (int x : n) {
            nodes.add(x);
        }
        return this;
    }

    // remove node
    public Board remove(int n) {
        nodes.remove(n);
        List<Wire> adj = adj(n);
        wires.removeAll(adj);
        return this;
    }

    // remove wire a---b, but only wire!
    public Board remove(int a, int b) {
        wires.remove(new Wire(a, b));
        return this;
    }

    public List<Wire> adj(int n) {
        List<Wire> out = new ArrayList<>();
        for (Wire w: wires) {
            if ((w.a==n) || (w.b==n)) out.add(w);
        }
        return out;
    }

    private boolean isIn(int[] arr, int v) {
        for(int x: arr) {
            if (v==x) return true;
        }
        return false;
    }

    public List<Wire> connectedWires(int n, ConnectorType type, int rotation) {
        int [] all = {0, 45, 90, 135, 180, 225, 270, 315};
        List<Wire> connWires = new ArrayList<>();
        int[] directions = type.directions(rotation);
        for(Wire w: adj(n)) {
            int d = w.dirFrom(n);
            if (isIn(directions, d)) {
                connWires.add(w);
            }
        }
        return connWires;
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
}