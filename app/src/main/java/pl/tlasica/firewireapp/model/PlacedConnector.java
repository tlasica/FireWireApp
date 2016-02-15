package pl.tlasica.firewireapp.model;

/**
 * Placed connectors is a connector of certain type e.g. L-Shape
 * with certain rotation {0, 45, 90, 135, 180, 225, 270, 315}
 */
public class PlacedConnector {
    public ConnectorType    type;
    public int              rotation;

    public PlacedConnector(ConnectorType t, int rot) {
        type = t;
        rotation = rot;
    }
}
