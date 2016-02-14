package pl.tlasica.firewireapp.model;

import java.util.ArrayList;
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
        wires.remove(new Wire(a,b));
        return this;
    }

    public List<Wire> adj(int n) {
        List<Wire> out = new ArrayList<>();
        for (Wire w: wires) {
            if ((w.a==n) || (w.b==n)) out.add(w);
        }
        return out;
    }

}
