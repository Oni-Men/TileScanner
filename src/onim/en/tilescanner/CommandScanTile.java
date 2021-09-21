package onim.en.tilescanner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Maps;

import onim.en.tilescanner.data.CommandBlockData;
import onim.en.tilescanner.data.ContainerData;
import onim.en.tilescanner.data.ScanDataContainer;

public class CommandScanTile implements CommandExecutor {

  private static HashMap<String, BiFunction<String, String, Boolean>> optionHandlers = Maps.newHashMap();

  public CommandScanTile() {
    optionHandlers.put("blocksPerTick", (k, v) -> {
      try {
        TileScanner.blocksPerTick = Integer.parseInt(v);
        return true;
      } catch (Exception e) { 
        return false;
      }
    });
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return false;
    }

    if (args.length == 0) {
      return false;
    }

    Player player = (Player) sender;
    String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

    switch (args[0]) {
      case "option":
        return processOption(player, subArgs);
      case "scan":
        return processScan(player, subArgs);
    }

    return false;
  }

  public static boolean processOption(Player player, String[] args) {
    if (args.length < 2) {
      return false;
    }

    String key = args[0];
    String value = args[1];

    if (optionHandlers.containsKey(key)) {
      return optionHandlers.get(key).apply(key, value);
    }
    
    return false;
  }

  public static boolean processScan(Player player, String[] args) {
    Location pos1 = null, pos2 = null;

    if (args.length == 1) {
      Location loc = player.getLocation();
      try {
        double r = Double.parseDouble(args[0]);
        pos1 = loc.clone().subtract(r, r, r);
        pos2 = loc.clone().add(r, r, r);
      } catch (NumberFormatException e) {
        player.sendMessage(ChatColor.RED + "範囲を正しく入力してください");
        return false;
      }
    } else if (args.length == 2) {
      World world = player.getWorld();
      pos1 = Locations.parseCommaSplittedLocation(world, args[0]);
      pos2 = Locations.parseCommaSplittedLocation(world, args[1]);
    }

    if (pos1 == null || pos2 == null) {
      player.sendMessage(ChatColor.RED + "座標を正しく設定できません");
      return false;
    }

    if (!pos1.getWorld().equals(pos2.getWorld())) {
      player.sendMessage(ChatColor.RED + "同じワールドを指定してください");
      return false;
    }

    Location[] cuboid = toCuboid(pos1, pos2);
    Location max = cuboid[0];
    Location min = cuboid[1];

    ScanDataContainer container = new ScanDataContainer(player.getName(), min, max);
    scanTileBlocks(min, max, container);
    return true;
  }

  private static void scanTileBlocks(Location min, Location max, ScanDataContainer container) {
    World world = min.getWorld();

    int fromX = min.getBlockX();
    int fromY = min.getBlockY();
    int fromZ = min.getBlockZ();

    int toX = max.getBlockX();
    int toY = max.getBlockY();
    int toZ = max.getBlockZ();

    AtomicInteger x = new AtomicInteger(fromX);
    AtomicInteger y = new AtomicInteger(fromY);
    AtomicInteger z = new AtomicInteger(fromZ);

    new BukkitRunnable() {

      @Override
      public void run() {
        int i = 0;
        while (y.get() <= toY) {
          while (x.get() <= toX) {
            while (z.get() <= toZ) {
              Block block = world.getBlockAt(x.get(), y.get(), z.getAndIncrement());
              processBlock(block, container);
              i++;
              if (i >= TileScanner.blocksPerTick) {
                return;
              }
            }
            x.incrementAndGet();
            z.set(fromZ);
          }
          y.incrementAndGet();
          x.set(fromX);
        }

        this.cancel();
        container.save(TileScanner.dataDirectory);
      }
    }.runTaskTimer(TileScanner.instance, 0, 1);

  }

  private static void processBlock(Block block, ScanDataContainer container) {
    BlockState state = block.getState();

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

  /**
   * 2つのLocationが示す直方体の頂点の最大点と最小点を得る
   * 
   * @param a
   * @param b
   * @return
   */
  private static Location[] toCuboid(Location a, Location b) {
    World world = a.getWorld();
    Location[] res = new Location[2];

    double x, y, z;

    x = Math.max(a.getX(), b.getX());
    y = Math.max(a.getY(), b.getY());
    z = Math.max(a.getZ(), b.getZ());

    res[0] = new Location(world, x, y, z);

    x = Math.min(a.getX(), b.getX());
    y = Math.min(a.getY(), b.getY());
    z = Math.min(a.getZ(), b.getZ());

    res[1] = new Location(world, x, y, z);
    return res;
  }

}
