package onim.en.tilescanner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import onim.en.tilescanner.data.ScanDataContainer;

public class CommandScanTile implements CommandExecutor {

  private static HashMap<UUID, CuboidBuilder> cuboidBuilders = Maps.newHashMap();

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return false;
    }

    Player player = (Player) sender;

    boolean pos1 = false;
    switch (args[0]) {
      case "pos1":
        pos1 = true;
      case "pos2":
        CuboidBuilder builder = cuboidBuilders.get(player.getUniqueId());
        if (builder == null) {
          builder = new CuboidBuilder();
          cuboidBuilders.put(player.getUniqueId(), builder);
        }

        if (pos1) {
          builder.pos1(player.getLocation());
        } else {
          builder.pos2(player.getLocation());
        }

        return true;
      case "scan":
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        return processScan(player, subArgs);
    }

    return false;
  }

  public static boolean processScan(Player player, String[] args) {
    Cuboid cuboid = null;
    Location pos1 = null, pos2 = null;

    if (args.length == 0) {
      CuboidBuilder builder = cuboidBuilders.get(player.getUniqueId());

      if (builder == null) {
        player.sendMessage(ChatColor.RED + "範囲を正しく設定してください");
        return false;
      }
      
      cuboid = builder.create();
      
    } else if (args.length == 1) {
      Location loc = player.getLocation();
      double r = Numbers.parseDouble(args[0], -1);
      if (r < 0) {
        player.sendMessage(ChatColor.RED + "数値を正しく入力してください");
        return false;
      }
      pos1 = loc.clone().subtract(r, r, r);
      pos2 = loc.clone().add(r, r, r);
      cuboid = new Cuboid (pos1, pos2);
    } else if (args.length == 2) {
      World world = player.getWorld();
      pos1 = Locations.parseCommaSplittedLocation(world, args[0]);
      pos2 = Locations.parseCommaSplittedLocation(world, args[1]);
      cuboid = new Cuboid (pos1, pos2);
    }

    if (cuboid == null) {
      player.sendMessage(ChatColor.RED + "範囲が正しく設定されていません");
      return false;
    }

    TileScanner scanner = new TileScanner(cuboid);
    ScanDataContainer container = scanner.scan(player.getName());
    container.save(TileScannerPlugin.dataDirectory);
    return true;
  }

}
