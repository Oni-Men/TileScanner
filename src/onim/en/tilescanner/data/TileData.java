package onim.en.tilescanner.data;

import org.bukkit.block.BlockState;

import onim.en.tilescanner.Locations;

public class TileData {

  public String type;

  public String location;
  
  public TileData(BlockState block) {
    this.type = block.getType().name();
    this.location = Locations.toStringSpaceSplitted(block.getLocation());
  }
}
