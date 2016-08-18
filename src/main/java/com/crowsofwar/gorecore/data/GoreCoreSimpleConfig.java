package com.crowsofwar.gorecore.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.crowsofwar.gorecore.GoreCore;
import com.crowsofwar.gorecore.util.GoreCoreParsingResult;
import com.crowsofwar.gorecore.util.GoreCoreParsingUtil;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.FMLLog;

/**
 * <p>
 * A compact key=value style config file, without comments.
 * </p>
 * 
 * <p>
 * They go in <code>.minecraft\config\MODID\FILENAME.cfg</code>.
 * </p>
 * 
 * @author CrowsOfWar
 */
public class GoreCoreSimpleConfig {
	
	private File storedAt;
	private Map<String, String> contents;
	private List<String> comments;
	private String modVersion;
	
	public GoreCoreSimpleConfig(String modID, String modVersion, String fileName) {
		try {
			this.modVersion = modVersion;
			comments = new ArrayList<String>();
			
			File dir = new File(GoreCore.proxy.getMinecraftDir(), "config/" + modID);
			if (!dir.exists()) dir.mkdir();
			
			storedAt = new File(dir, fileName + ".cfg");
			if (!storedAt.exists()) storedAt.createNewFile();
		} catch (Exception e) {
			FMLLog.warning("GoreCore> Error creating simple config file at \"" + GoreCore.proxy.getMinecraftDir().getAbsolutePath() + "/"
					+ modID + "/" + fileName + ".cfg\"!");
			e.printStackTrace();
			// TODO Do something - leaving storedAt null could result in NPEs
		}
		
	}
	
	/**
	 * Fills the contents map with data so that you can use the "get..." methods.
	 */
	public void load() {
		try {
			contents = new HashMap<String, String>();
			
			boolean firstLine = true;
			if (!storedAt.exists()) storedAt.createNewFile();
			BufferedReader br = new BufferedReader(new FileReader(storedAt));
			String ln;
			while ((ln = br.readLine()) != null) {
				if (!ln.startsWith("#")) {
					
					if (ln.contains("=")) {
						String[] kv = ln.split("=", 2);
						String k = kv[0];
						String v = kv[1];
						contents.put(k, v);
					} else if (firstLine) {
						// Load version
						if (!modVersion.equals(ln)) FMLLog.warning("GoreCore> Note: The simple config file " + storedAt.getName()
								+ " was created for a different mod version than this one. It is"
								+ " recommended that you delete it in case the mod author changed" + " any values.");
					}
					
				}
				firstLine = false;
			}
		} catch (Exception e) {
			FMLLog.warning("GoreCore> Error reading simple config file!");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Saves the stored data to the file.
	 */
	public void save() {
		try {
			if (!storedAt.exists()) storedAt.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(storedAt, false));
			
			bw.write(modVersion + "\n");
			bw.write("# The above line is the version of the mod this config file was first generated for.");
			bw.write("# ");
			
			for (String comment : comments) {
				bw.write("# " + comment + "\n");
			}
			
			Set<Map.Entry<String, String>> entrySet = contents.entrySet();
			for (Map.Entry<String, String> entry : entrySet) {
				bw.write(entry.getKey() + "=" + entry.getValue() + "\n");
			}
			
			bw.close(); // also flushes
			
		} catch (Exception e) {
			FMLLog.warning("GoreCore> Error writing simple config file!");
			e.printStackTrace();
		}
	}
	
	public void addComment(String comment) {
		comments.add(comment);
	}
	
	public String getString(String key, String defaultValue) {
		if (!contents.containsKey(key)) contents.put(key, defaultValue);
		return contents.get(key);
	}
	
	public String getStringWithoutCreate(String key, String defaultValue) {
		return contents.containsKey(key) ? contents.get(key) : "";
	}
	
	public int getInt(String key, int defaultValue) {
		try {
			return Integer.parseInt(getString(key, defaultValue + ""));
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public int getIntWithoutCreate(String key, int defaultValue) {
		try {
			return Integer.parseInt(getStringWithoutCreate(key, defaultValue + ""));
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public float getFloat(String key, float defaultValue) {
		try {
			return Float.parseFloat(getString(key, defaultValue + ""));
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public float getFloatWithoutCreate(String key, float defaultValue) {
		try {
			return Float.parseFloat(getStringWithoutCreate(key, defaultValue + ""));
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public double getDouble(String key, double defaultValue) {
		try {
			return Double.parseDouble(getString(key, defaultValue + ""));
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public double getDoubleWithoutCreate(String key, double defaultValue) {
		try {
			return Double.parseDouble(getStringWithoutCreate(key, defaultValue + ""));
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public long getLong(String key, long defaultValue) {
		try {
			return Long.parseLong(getString(key, defaultValue + ""));
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public long getLongWithoutCreate(String key, long defaultValue) {
		try {
			return Long.parseLong(getString(key, defaultValue + ""));
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public boolean getBoolean(String key, boolean defaultValue) {
		return getString(key, defaultValue + "").equals("true");
	}
	
	public boolean getBooleanWithoutCreate(String key, boolean defaultValue) {
		return getStringWithoutCreate(key, defaultValue + "").equals("true");
	}
	
	/**
	 * Gets a configured item stored in the config. How does this happen? It's by default stored by
	 * its unlocalized name, but can also be stored as the en-US translation.
	 */
	public Item getItem(String key, Item defaultValue) {
		String itemStr = getString(key, "[n]" + Item.itemRegistry.getNameForObject(defaultValue));
		Item item = defaultValue;
		
		// Get by name
		if (itemStr.startsWith("[n]")) item = (Item) Item.itemRegistry.getObject(itemStr.substring("[u]".length()));
		
		// Get by ID
		if (itemStr.startsWith("[i]")) {
			GoreCoreParsingResult.ResultInteger result = GoreCoreParsingUtil.parseInt(itemStr.substring("[i]".length()));
			if (result.wasSuccessful()) item = (Item) Item.itemRegistry.getObjectById(result.getResult());
		}
		
		return item;
		
	}
	
	/**
	 * Gets a configured item stored in the config. How does this happen? It's by default stored by
	 * its unlocalized name, but can also be stored as the en-US translation.
	 */
	public Item getItemWithoutCreate(String key, Item defaultValue) {
		String itemStr = getStringWithoutCreate(key, "[n]" + Item.itemRegistry.getNameForObject(defaultValue));
		Item item = defaultValue;
		
		// Get by name
		if (itemStr.startsWith("[n]")) item = (Item) Item.itemRegistry.getObject(itemStr.substring("[u]".length()));
		
		// Get by ID
		if (itemStr.startsWith("[i]")) {
			GoreCoreParsingResult.ResultInteger result = GoreCoreParsingUtil.parseInt(itemStr.substring("[i]".length()));
			if (result.wasSuccessful()) item = (Item) Item.itemRegistry.getObjectById(result.getResult());
		}
		
		return item;
		
	}
	
	/**
	 * Get the string key for that item - it's either the name of that item (like minecraft:apple),
	 * or the ID of that item. If there isn't any data stored under that key, this returns the name
	 * of the item.
	 */
	public String getItemKey(Item key) {
		final String name = Item.itemRegistry.getNameForObject(key);
		final String id = Item.itemRegistry.getIDForObject(key) + "";
		String result = name;
		// don't need to check for containsKey(name) because it's already name anyways
		if (contents.containsKey(id)) result = id;
		return result;
	}
	
	/**
	 * Gets a configured block stored in the config. How does this happen? It's by default stored by
	 * its unlocalized name, but can also be stored as the en-US translation.
	 */
	public Block getBlock(String key, Block defaultValue) {
		String blockStr = getString(key, "[n]" + Block.blockRegistry.getNameForObject(defaultValue));
		Block block = defaultValue;
		
		// Get by name
		if (blockStr.startsWith("[n]")) block = (Block) Block.blockRegistry.getObject(blockStr.substring("[u]".length()));
		
		// Get by ID
		if (blockStr.startsWith("[i]")) {
			GoreCoreParsingResult.ResultInteger result = GoreCoreParsingUtil.parseInt(blockStr.substring("[i]".length()));
			if (result.wasSuccessful()) block = (Block) Block.blockRegistry.getObjectById(result.getResult());
		}
		
		return block;
	}
	
	/**
	 * Gets a configured block stored in the config. How does this happen? It's by default stored by
	 * its unlocalized name, but can also be stored as the en-US translation.
	 */
	public Block getBlockWithoutCreate(String key, Block defaultValue) {
		String blockStr = getStringWithoutCreate(key, "[n]" + Block.blockRegistry.getNameForObject(defaultValue));
		Block block = defaultValue;
		
		// Get by name
		if (blockStr.startsWith("[n]")) block = (Block) Block.blockRegistry.getObject(blockStr.substring("[u]".length()));
		
		// Get by ID
		if (blockStr.startsWith("[i]")) {
			GoreCoreParsingResult.ResultInteger result = GoreCoreParsingUtil.parseInt(blockStr.substring("[i]".length()));
			if (result.wasSuccessful()) block = (Block) Block.blockRegistry.getObjectById(result.getResult());
		}
		
		return block;
	}
	
	/**
	 * Get the string key for that block - it's either the name of that block (like minecraft:dirt),
	 * or the ID of that item. If there isn't any data stored under that key, this returns the name
	 * of the block.
	 */
	public String getBlockKey(Block key) {
		final String name = Block.blockRegistry.getNameForObject(key);
		final String id = Block.blockRegistry.getIDForObject(key) + "";
		String result = name;
		// don't need to check for containsKey(name) because it's already name anyways
		if (contents.containsKey(id)) result = id;
		return result;
	}
	
}
