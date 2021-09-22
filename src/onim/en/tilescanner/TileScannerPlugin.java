package onim.en.tilescanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.bukkit.plugin.java.JavaPlugin;

public class TileScannerPlugin extends JavaPlugin {

  public static JavaPlugin instance;

  public static Path dataDirectory;
  
  @Override
  public void onEnable() {
    instance = this;
    dataDirectory = getDataFolder().toPath().resolve("scan_datas");

    try {
      Files.createDirectories(dataDirectory);
    } catch (IOException e) {
      e.printStackTrace();
    }

    getCommand("scantile").setExecutor(new CommandScanTile());
    
  }

}
