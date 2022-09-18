package com.rs;

import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.alex.store.Index;
import com.larxstar.Hostgate;
import com.larxstar.HostgateLink;
import com.rs.cache.Cache;
import com.rs.cache.loaders.AttackSpeed;
import com.rs.cache.loaders.EquipData;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cores.CoresManager;
import com.rs.game.Region;
import com.rs.game.World;
import com.rs.game.map.MapBuilder;
import com.rs.game.npc.combat.CombatScriptsHandler;
import com.rs.game.player.Player;
import com.rs.game.player.content.FishingSpotsHandler;
import com.rs.game.player.content.FriendChatsManager;
import com.rs.game.player.content.grandExchange.GrandExchange;
import com.rs.game.player.controllers.ControllerHandler;
import com.rs.game.player.cutscenes.CutscenesHandler;
import com.rs.game.player.dialogues.DialogueHandler;
import com.rs.network.Network;
import com.rs.utils.*;
import com.rs.utils.huffman.Huffman;
import com.webrs.responder.Responder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Launcher {

	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.out.println("USE: guimode(boolean) debug(boolean) hosted(boolean)");
			return;
		}
		ConfigLoader.load();
		Settings.PORT_ID = ConfigLoader.intProp("port");
		Settings.HOST = ConfigLoader.prop("host");
		Settings.HOSTED = Boolean.parseBoolean(args[2]);
		Settings.DEBUG = true;// Boolean.parseBoolean(args[1]);
		long currentTime = Utils.currentTimeMillis();
		Logger.log("Launcher", "Initing Cache...");
		Cache.init();
		Huffman.init();
		Logger.log("Launcher", "Initing Data Files...");
		EquipData.init();
		AttackSpeed.init();
		ItemBonuses.init();
		Censor.init();
		DisplayNames.init();
		IPBanL.init();
		IPMuteL.init();
		PkRank.init();
		MapArchiveKeys.init();
		MapAreas.init();
		ObjectSpawns.init();
		NPCSpawns.init();
		NPCCombatDefinitionsL.init();
		NPCBonuses.init();
		NPCDrops.init();
		NPCExamines.init();
		ItemExamines.init();
		ItemDestroys.init();
		ItemSpawns.init();
		MusicHints.init();
		ShopsHandler.init();
		GrandExchange.init();
		Logger.log("Launcher", "Initing Controlers...");
		ControllerHandler.init();
		Logger.log("Launcher", "Initing Fishing Spots...");
		FishingSpotsHandler.init();
		Logger.log("Launcher", "Initing NPC Combat Scripts...");
		CombatScriptsHandler.init();
		Logger.log("Launcher", "Initing Dialogues...");
		DialogueHandler.init();
		Logger.log("Launcher", "Initing Cutscenes...");
		CutscenesHandler.init();
		Logger.log("Launcher", "Initing Friend Chats Manager...");
		FriendChatsManager.init();
		Logger.log("Launcher", "Initing Cores Manager...");
		CoresManager.init();
		Logger.log("Launcher", "Initing World...");
		World.init();
		Logger.log("Launcher", "Initing Region Builder...");
		MapBuilder.init();
		Logger.log("Launcher", "Starting Network...");
		Network.load();
		// Logger.log("Launcher", "Initing Server Channel Handler...");
		// try {
		// ServerChannelHandler.init();
		// } catch (Throwable e) {
		// Logger.handle(e);
		// Logger.log("Launcher", "Failed initing Server Channel Handler. Shutting down...");
		// System.exit(1);
		// return;
		// }
		// Logger.log("Launcher", "Initing Web responder...");
		// try {
		// if (!Settings.HOSTED)
		// Responder.init();
		// } catch (Throwable e) {
		// Logger.handle(e);
		// Logger.log("Launcher", "Failed initing web responder... Shutting down...");
		// System.exit(1);
		// return;
		// }
		Logger.log("Launcher", "Server took " + (Utils.currentTimeMillis() - currentTime) + " milli seconds to launch on port "+Settings.PORT_ID+".");
		addFilesSavingTask();
		addCleanMemoryTask();
		addrecalcPricesTask();
		Hostgate.connectHostgateBlockingStartup();
	}

	private static void addCleanMemoryTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					cleanMemory(Runtime.getRuntime().freeMemory() < Settings.MIN_FREE_MEM_ALLOWED);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 10, TimeUnit.MINUTES);
	}

	private static void addFilesSavingTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					saveFiles();
				} catch (Throwable e) {
					Logger.handle(e);
				}

			}
		}, 15, 15, TimeUnit.MINUTES);
	}

	private static void addrecalcPricesTask() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		int minutes = (int) ((c.getTimeInMillis() - Utils.currentTimeMillis()) / 1000 / 60);
		int halfDay = 12 * 60;
		if (minutes > halfDay)
			minutes -= halfDay;
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					GrandExchange.recalcPrices();
				} catch (Throwable e) {
					Logger.handle(e);
				}

			}
		}, minutes, halfDay, TimeUnit.MINUTES);
	}

	public static void saveFiles() {
		for (Player player : World.getPlayers()) {
			if (player == null || !player.hasStarted() || player.hasFinished())
				continue;
			SerializableFilesManager.savePlayer(player);
		}
		saveOtherFiles();
	}

	public static void saveOtherFiles() {
		DisplayNames.save();
		IPBanL.save();
		IPMuteL.save();
		PkRank.save();
		GrandExchange.save();
	}

	public static void cleanMemory(boolean force) {
		if (force) {
			ItemDefinitions.clearItemsDefinitions();
			NPCDefinitions.clearNPCDefinitions();
			ObjectDefinitions.clearObjectDefinitions();
			skip: for (Region region : World.getRegions().values()) {
				for (int regionId : MapBuilder.FORCE_LOAD_REGIONS)
					if (regionId == region.getRegionId())
						continue skip;
				region.unloadMap();
			}
		}
		for (Index index : Cache.STORE.getIndexes())
			index.resetCachedFiles();
		CoresManager.fastExecutor.purge();
		System.gc();
	}

	public static void shutdown() {
		try {
			closeServices();
		} finally {
			System.exit(0);
		}
	}

	public static void closeServices() {
		//if (!Settings.HOSTED)
		//	Responder.shutdown();
		// ServerChannelHandler.shutdown();
		Network.shutdown();
		CoresManager.shutdown();
		log.info("closed services");
	}

	public static void restart() {
		closeServices();
		System.gc();
		try {
			Runtime.getRuntime().exec("java -server -Xms2048m -Xmx20000m -cp bin;/data/libs/netty-3.2.7.Final.jar;/data/libs/FileStore.jar Launcher false false true false");
			System.exit(0);
		} catch (Throwable e) {
			Logger.handle(e);
		}

	}

	private Launcher() {

	}

}
