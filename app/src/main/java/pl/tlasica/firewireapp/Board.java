package pl.tlasica.firewireapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Board {

    public Set<Integer> nodes = new HashSet<Integer>();
    public List<Wire>   wires = new ArrayList<Wire>();

    public Board() {

    }

    public Board with(int a, int b) {
        nodes.add(a);
        nodes.add(b);
        wires.add(new Wire(a,b));
        return this;
    }

    public Board withNode(int n) {
        nodes.add(n);
        return this;
    }

    public Board withNodes(int [] n) {
        for (int x : n) {
            nodes.add(x);
        }
        return this;
    }

}
