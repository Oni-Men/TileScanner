package onim.en.tilescanner.data;

import org.bukkit.block.CommandBlock;

public class CommandBlockData extends TileData {

  public String command;

  public CommandBlockData(CommandBlock block) {
    super(block);
    this.command = block.getCommand();
  }

}
