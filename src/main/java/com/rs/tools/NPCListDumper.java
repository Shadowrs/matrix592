package com.rs.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.rs.cache.Cache;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.utils.Utils;

public class NPCListDumper {

	public static void main(String[] args) throws IOException {
		Cache.init();
		File file = new File("npcList.txt");
		if (file.exists())
			file.delete();
		else
			file.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		// writer.append("//Version = 667.704\n");
		writer.flush();
		for (int id = 0; id < Utils.getNPCDefinitionsSize(); id++) {
			NPCDefinitions def = NPCDefinitions.getNPCDefinitions(id);
			// writer.append("FORMAT1"+id+"FORMAT2"+def.name.replaceAll("`",
			// "")+"FORMAT3\n");
			writer.write(id + " - " + def.name);
			writer.newLine();
			writer.flush();
		}
		writer.close();
	}

}
