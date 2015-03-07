package hatfat.com.quest.planet;

import com.hatfat.agl.mesh.AglShape;
import com.hatfat.agl.util.AglRandom;
import com.hatfat.agl.util.Color;

import java.util.HashSet;
import java.util.Set;

public class HexTile {

    public enum HexTileType {
        LAND(new Color(0.55f, 0.27f, 0.07f, 1.0f)),
        WATER(new Color(0.0f, 0.0f, 0.6f, 1.0f)),

        GRASSLAND(new Color(0.2f, 0.8f, 0.2f, 1.0f)),
        PLAINS(new Color(0.91f, 0.89f, 0.81f, 1.0f)),
        FOREST(new Color(0.13f, 0.55f, 0.13f, 1.0f)),
        MOUNTAIN(new Color(0.44f, 0.5f, 0.57f, 1.0f)),

        NO_TYPE(new Color(1.0f, 0.0f, 1.0f, 1.0f));

        private final Color color;

        private HexTileType(final Color color) {
            this.color = color;
        }

        public static int getNumBasicTypes() {
            return 2;
        }

        public static int getNumTypes() {
            return 6;
        }

        public Color getColor() {
            return color;
        }

        public static HexTileType typeFromInt(int id) {
            switch (id) {
                case 0:
                    return LAND;
                case 1:
                    return WATER;
                case 2:
                    return PLAINS;
                case 3:
                    return FOREST;
                case 4:
                    return MOUNTAIN;
                case 5:
                    return GRASSLAND;

                default:
                    return NO_TYPE;
            }
        }
    }

    private AglShape shape;
    private HexTileType type;

    private Set<HexTile> neighbors = new HashSet<>();

    public HexTile(final AglShape shape) {
        this.shape = shape;
        this.type = HexTileType.NO_TYPE;
    }

    public HexTile(final AglShape shape, final HexTileType type) {
        this.shape = shape;
        this.type = type;
    }

    public AglShape getShape() {
        return shape;
    }

    public void setType(HexTileType type) {
        this.type = type;
    }

    public HexTileType getType() {
        return type;
    }

    public void addNeighbor(HexTile neighbor) {
        neighbors.add(neighbor);
    }

    public Set<HexTile> getNeighbors() {
        return neighbors;
    }

    public boolean isTypeSet() {
        return type != HexTileType.NO_TYPE;
    }

    public void assignRandomType(final AglRandom random) {
        this.type = HexTileType.typeFromInt(random.get().nextInt(HexTileType.getNumTypes()));
    }

    public void assignRandomBaseType(final AglRandom random) {
        float value = random.get().nextFloat();

        //66% land
        if (value < 0.666f) {
            this.type = HexTileType.LAND;
        }
        else {
            this.type = HexTileType.WATER;
        }
    }
}
