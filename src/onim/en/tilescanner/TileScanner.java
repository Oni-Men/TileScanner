package onim.en.tilescanner;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.block.Beacon;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.inventory.Inventory;

import onim.en.tilescanner.data.CommandBlockData;
import onim.en.tilescanner.data.ContainerData;
import onim.en.tilescanner.data.ScanDataContainer;

public class TileScanner {

  private Cuboid cuboid;

  public TileScanner(Cuboid cuboid) {
    this.cuboid = cuboid;
  }

  public ScanDataContainer scan(String executor) {

    ScanDataContainer container = new ScanDataContainer(executor, this.cuboid.getMin(), this.cuboid.getMax());

    List<Chunk> chunks = this.cuboid.getChunks();

    for (Chunk chunk : chunks) {
      this.processTileEntities(chunk.getTileEntities(), container);
    }

    return container;
  }

  private void processTileEntities(BlockState[] tiles, ScanDataContainer container) {
    for (BlockState state : tiles) {

      if (!this.cuboid.isInside(state.getLocation())) {
        continue;
      }

      if (state instanceof CommandBlock) {
        container.tiles.add(new CommandBlockData((CommandBlock) state));
      }

      Inventory inventory = null;

      if (state instanceof Chest) {
        inventory = ((Chest) state).getBlockInventory();
      } else if (state instanceof Dropper) {
        inventory = ((Dropper) state).getInventory();
      } else if (state instanceof Dispenser) {
        inventory = ((Dispenser) state).getInventory();
      } else if (state instanceof Furnace) {
        inventory = ((Furnace) state).getInventory();
      } else if (state instanceof BrewingStand) {
        inventory = ((BrewingStand) state).getInventory();
      } else if (state instanceof Beacon) {
        inventory = ((Beacon) state).getInventory();
      } else if (state instanceof Hopper) {
        inventory = ((Hopper) state).getInventory();
      }

      if (inventory != null) {
        container.tiles.add(new ContainerData(state, inventory));
      }
    }
  }

}
