package goldbitna;

public class RobotSpawnLocation {
    public final int x;
    public final int y;
    public final int mapId;

    public RobotSpawnLocation(int x, int y, int mapId) {
        this.x = x;
        this.y = y;
        this.mapId = mapId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getMapId() {
        return mapId;
    }
}