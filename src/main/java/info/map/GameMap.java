package info.map;

import bwapi.TilePosition;
import info.exception.NoWalkablePathException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Bundles up information about the game map.
 */
public class GameMap {

    private int x;
    private int y;
    private ArrayList<MapTile> heatMap = new ArrayList<>();

    private MapTile[][] mapTiles;

    public GameMap(int x, int y) {
        mapTiles = new MapTile[x][y];
        this.x = x;
        this.y = y;
    }

    public void addTile(MapTile tile, int x, int y) {
        mapTiles[x][y] = tile;
        heatMap.add(tile);
    }

    public MapTile get(int x, int y) {
        return mapTiles[x][y];
    }

    public ArrayList<MapTile> getHeatMap() {
        return heatMap;
    }

    /**
     * A* search to find a walkable path between start and end tiles.
     *
     * TODO: Consider neutral structures in MapTile
     * TODO: Consider current buildings in MapTile
     *
     * @param start origin TilePosition
     * @param end destination TilePosition
     * @return
     * @throws NoWalkablePathException if no walkable path exists
     */
    public GroundPath aStarSearch(MapTile start, MapTile end) throws NoWalkablePathException {
        Map<MapTile, MapTile> cameFrom = new HashMap<>();
        Map<MapTile, Integer> gScore = new HashMap<>();
        Map<MapTile, Integer> fScore = new HashMap<>();

        gScore.put(start, 0);
        fScore.put(start, this.calculateH(start, end));

        PriorityQueue<MapTile> openSet = new PriorityQueue<>(new MapTileFScoreComparator(fScore));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            MapTile current = openSet.poll();
            if (current == end) {
                return reconstructPath(cameFrom, current);
            }

            List<MapTile> neighbors = this.getNeighbors(current);
            for (MapTile n: neighbors) {
                int neighborG = Integer.MAX_VALUE;
                final int currentG = gScore.get(current);
                final int tentativeGScore = this.calculateG(current, n, currentG);

                if (gScore.containsKey(n)) {
                    neighborG = gScore.get(n);
                }

                if (tentativeGScore < neighborG) {
                    cameFrom.put(n, current);
                    gScore.put(n, tentativeGScore);
                    fScore.put(n, this.calculateH(n, end));
                    if (!openSet.contains(n)) {
                        openSet.add(n);
                    }
                }
            }
        }

        throw new NoWalkablePathException("no walkable path exists");
    }

    public GroundPath aStarSearch(TilePosition start, TilePosition end) throws NoWalkablePathException {
        final MapTile startTile = this.mapTiles[start.getX()][start.getY()];
        final MapTile endTile = this.mapTiles[end.getX()][end.getY()];
        return aStarSearch(startTile, endTile);
    }

    public ScoutPath findScoutPath(TilePosition center) {
        List<TilePosition> points = new ArrayList<>();

        TilePosition north = center.add(new TilePosition(0, max(0, center.getY())-7));
        TilePosition east = center.add(new TilePosition(min(x, center.getX())+7, 0));
        TilePosition south = center.add(new TilePosition(0, min(y, center.getY())+7));
        TilePosition west = center.add(new TilePosition(max(0, center.getX())-7, 0));

        points.add(north);
        points.add(east);
        points.add(south);
        points.add(west);

        return new ScoutPath(points);
    }

    private int calculateG(MapTile current, MapTile target, int currentG) {
        return currentG + (int) target.getTile().getDistance(current.getTile());
    }

    private int calculateH(MapTile current, MapTile destination) {
        return (int) destination.getTile().getDistance(current.getTile());
    }

    private GroundPath reconstructPath(Map<MapTile, MapTile> cameFrom, MapTile current) {
        ArrayDeque<MapTile> path = new ArrayDeque<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.addFirst(current);
        }

        return new GroundPath(path);
    }

    /**
     * Returns neighbor tiles of current that can be considered for ground based path-finding.
     *
     * TODO: Don't consider diagonal candidate if it can't be reached by at least 1 cardinal direction
     *
     * @param current
     * @return
     */
    private List<MapTile> getNeighbors(MapTile current) {
        List<MapTile> neighbors = new ArrayList<>();

        final int currentX = current.getX();
        final int currentY = current.getY();

        // Add cardinal neighbors
        // N
        if (isValidTile(currentX, currentY+1) && mapTiles[currentX][currentY+1].isBuildable()) neighbors.add(mapTiles[currentX][currentY+1]);
        // S
        if (isValidTile(currentX, currentY-1) && mapTiles[currentX][currentY-1].isBuildable()) neighbors.add(mapTiles[currentX][currentY-1]);
        // W
        if (isValidTile(currentX-1, currentY) && mapTiles[currentX-1][currentY].isBuildable()) neighbors.add(mapTiles[currentX-1][currentY]);
        // E
        if (isValidTile(currentX+1, currentY) && mapTiles[currentX+1][currentY].isBuildable()) neighbors.add(mapTiles[currentX+1][currentY]);

        return neighbors;
    }

    private boolean isValidTile(int x, int y) {
        return x > 0 && x < this.x && y > 0 && y < this.y;
    }
}
