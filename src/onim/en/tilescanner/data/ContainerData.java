package onim.en.tilescanner.data;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ContainerData extends TileData {

  public String title;
  
  public List<String> contents;

  public ContainerData(BlockState block, Inventory inventory) {
    super(block);
    this.title = inventory.getTitle();
    this.contents = new LinkedList<>();
    
    for (ItemStack stack : inventory.getContents()) {
      if (stack == null) {
        continue;
      }

      String thelowItemID = getNBTTag(stack, "thelow_item_id");
    
      if (thelowItemID == null) {
        this.contents.add(String.format("%s(%s)", getItemName(stack), stack.getType().name()));
      } else {
        this.contents.add(thelowItemID);
      }
    }
  }
  
  private static String getItemName(ItemStack stack) {
    if (!stack.hasItemMeta()) {
      return "";
    }

    ItemMeta meta = stack.getItemMeta();
  
    if (!meta.hasDisplayName()) {
      return "";
    }
    
    return meta.getDisplayName();
  }
  
  public static String getNBTTag(ItemStack item, String name) {
    net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
    if (nmsStack == null) { return null; }
    if (nmsStack.getTag() == null) { return null; }

    // NBTタグが不正な時はnullを返す
    String string = nmsStack.getTag().getString(name);
    if (string == null || string.isEmpty()) {
      return null;
    } else {
      return string;
    }
  }
}
