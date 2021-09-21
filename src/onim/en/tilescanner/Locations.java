package onim.en.tilescanner;

import org.bukkit.Location;
import org.bukkit.World;

public class Locations {


  public static Location parseCommaSplittedLocation(World world, String str) {
    String[] coords = str.split(",");

    if (coords.length != 3) {
      return null;
    }

    double[] xyz = new double[3];

    for (int i = 0; i < 3; i++) {
      try {
        xyz[i] = Double.parseDouble(coords[i]);
      } catch (NumberFormatException e) {
        return null;
      }
    }

    return new Location(world, xyz[0], xyz[1], xyz[2]);
  }
  
  public static String toStringSpaceSplitted(Location loc) {
    StringBuffer buf = new StringBuffer();
    
    buf.append(loc.getWorld().getName()).append(" ");
    buf.append(loc.getBlockX()).append(" ");
    buf.append(loc.getBlockY()).append(" ");
    buf.append(loc.getBlockZ()).append(" ");
    
    return buf.toString();
  }
}
