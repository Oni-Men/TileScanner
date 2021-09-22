package onim.en.tilescanner;

import org.bukkit.Location;

public class CuboidBuilder {

  private Location pos1;
  
  private Location pos2;
  
  public void pos1(Location loc) {
    this.pos1 = loc;
  }
  
  public void pos2(Location loc)  {
    this.pos2 = loc;
  }
  
  public Cuboid create() {
    
    if (pos1 == null || pos2 == null) {
      return null;
    }
    
    return new Cuboid(pos2, pos1);
  }
}
