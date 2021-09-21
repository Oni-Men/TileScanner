package onim.en.tilescanner.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import onim.en.tilescanner.Locations;

public class ScanDataContainer {

  private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private static final DateFormat dateFormat= new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
  
  public String executor;
  
  public String world;
  
  public String min;
  
  public String max;

  public final Date executeAt;
  
  public List<TileData> tiles;

  public ScanDataContainer(String executor, Location min, Location max) {
    this.executor = executor;
    this.world = min.getWorld().getName();
    this.min = Locations.toStringSpaceSplitted(min);
    this.max = Locations.toStringSpaceSplitted(max);
    this.executeAt = new Date();
    this.tiles = new LinkedList<>();
  }
  
  public String getFilename() {
    return dateFormat.format(executeAt) + ".json";
  }
  
  public void save(Path dir) {
    Path path = dir.resolve(this.getFilename());
    
    new Thread(() -> {
      try {
        BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        writer.write(gson.toJson(this));
        writer.flush();
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
  }
  
}
