package onim.en.tilescanner;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class Cuboid {

  private World world;

  private Location min;

  private Location max;

  public Cuboid(Location a, Location b) {
    if (!a.getWorld().equals(b.getWorld())) {
      throw new IllegalArgumentException("world is not equal");
    }

    world = a.getWorld();
    double x, y, z;

    x = Math.max(a.getX(), b.getX());
    y = Math.max(a.getY(), b.getY());
    z = Math.max(a.getZ(), b.getZ());

    max = new Location(world, x, y, z);

    x = Math.min(a.getX(), b.getX());
    y = Math.min(a.getY(), b.getY());
    z = Math.min(a.getZ(), b.getZ());

    min = new Location(world, x, y, z);
  }

  public Location getMin() {
    return min;
  }

  public Location getMax() {
    return max;
  }

  /**
   * この直方体のXY成分と重なっているチャンクを取得します。
   * 
   * @return
   */
  public List<Chunk> getChunks() {
    List<Chunk> list = new LinkedList<>();

    Chunk minChunk = min.getChunk();
    Chunk maxChunk = max.getChunk();

    for (int x = minChunk.getX(); x <= maxChunk.getX(); x++) {
      for (int z = minChunk.getZ(); z <= maxChunk.getZ(); z++) {
        list.add(world.getChunkAt(x, z));
      }
    }

    return list;
  }

  public boolean isInside(Location loc) {
    boolean inside = true;

    inside &= loc.getBlockX() >= min.getBlockX();
    inside &= loc.getBlockY() >= min.getBlockY();
    inside &= loc.getBlockZ() >= min.getBlockZ();

    inside &= loc.getBlockX() <= max.getBlockX();
    inside &= loc.getBlockY() <= max.getBlockY();
    inside &= loc.getBlockZ() <= max.getBlockZ();

    return inside;
  }
}
