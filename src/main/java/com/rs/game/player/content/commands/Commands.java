package com.rs.game.player.content.commands;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.*;
import com.rs.utils.Censor;
import com.rs.utils.Utils;

/*
 * doesnt let it be extended
 */
public final class Commands {

	/*
	 * all console commands only for admin, chat commands processed if they not processed by console
	 */

	/**
	 * returns if command was processed
	 */
	public static boolean processCommand(Player player, String command, boolean console, boolean clientCommand) {
		if (command.length() == 0) // if they used ::(nothing) theres no command
			return false;
		String[] cmd = command.split(" ");
		if (cmd.length == 0)
			return false;
		archiveLogs(player, cmd);
		if (MoreCommands.INSTANCE.handle(player, cmd, console, clientCommand)) {
			return true;
		}
		if (player.getRights() >= 2 && AdminCmds.processAdminCommand(player, cmd, console, clientCommand))
			return true;
		if (player.getRights() >= 1 && ModCommands.processModCommand(player, cmd, console, clientCommand))
			return true;
		if ((player.isSupporter() || player.getRights() >= 1) && SuppportCmds.processSupportCommands(player, cmd, console, clientCommand))
			return true;
		if ((player.isSupporter() || player.getRights() >= 1) && PunishCmds.processPunishmentCommand(player, cmd, console, clientCommand))
			return true;
		if (Settings.ECONOMY || Settings.ECONOMY_TEST) {
			return processNormalCommand(player, cmd, console, clientCommand);
		} else {
			return processNormalCommand(player, cmd, console, clientCommand) || processNormalSpawnCommand(player, cmd, console, clientCommand);
		}
	}

	public static void sendYell(Player player, String message, boolean staffYell) {
		if (Settings.BAD_BOYS)
			message = Censor.getFilteredMessage(message);
		if (!player.isDonator() && !player.isExtremeDonator() && player.getRights() == 0 && !player.isSupporter() && !player.isGraphicDesigner())
			return;
		else if (!Settings.YELL_ENABLED && player.getRights() != 2) {
			player.message("Yell is currently disabled by an administrator");
			return;
		}
		if (player.getMuted() > Utils.currentTimeMillis()) {
			player.message("You temporary muted. Recheck in 48 hours.");
			return;
		}
		if (staffYell) {
			World.sendIgnoreableWorldMessage(player, "[<col=ff0000>Staff Yell</col>] " + (player.getRights() > 1 ? "<img=1>" : (player.isSupporter() ? "" : "<img=0>")) + player.getDisplayName() + ": <col=ff0000>" + message + ".</col>", true);
			return;
		}
		if (message.length() > 100)
			message = message.substring(0, 100);

		if (player.getRights() < 2) {
			String[] invalid = { "<euro", "<img", "<img=", "<col", "<col=", "<shad", "<shad=", "<str>", "<u>" };
			for (String s : invalid)
				if (message.contains(s)) {
					player.message("You cannot add additional code to the message.");
					return;
				}

			if (player.isGraphicDesigner())
				World.sendIgnoreableWorldMessage(player, "[<img=9><col=00ACE6>Graphic Designer</shad></col>] <img=9>" + player.getDisplayName() + ": <col=00ACE6><shad=000000>" + message + "", false);
			else if (player.isSupporter() && player.getRights() == 0)
				World.sendIgnoreableWorldMessage(player, "[<col=58ACFA><shad=2E2EFE>Support Team</shad></col>] " + player.getDisplayName() + ": <col=58ACFA><shad=2E2EFE>" + message + "</shad></col>.", false);

			else if (player.isExtremeDonator() && player.getRights() == 0)
				World.sendIgnoreableWorldMessage(player, "[<col=" + (player.getYellColor() == "ff0000" || player.getYellColor() == null ? "ff0000" : player.getYellColor()) + ">Extreme Donator</col>] <img=11>" + player.getDisplayName() + ": <col=" + (player.getYellColor() == "ff0000" || player.getYellColor() == null ? "ff0000" : player.getYellColor()) + ">" + message + "</col>", false);

			else if (player.isDonator() && player.getRights() == 0)
				World.sendIgnoreableWorldMessage(player, "[<col=02ab2f>Donator</col>] <img=8>" + player.getDisplayName() + ": <col=02ab2f>" + message + "</col>", false);

			else
				World.sendIgnoreableWorldMessage(player, "[<img=0><col=" + (player.getYellColor() == "ff0000" || player.getYellColor() == null ? "000099" : player.getYellColor()) + ">" + ("Global Mod") + "</col><img=0>]" + player.getDisplayName() + ": <col=" + (player.getYellColor() == "ff0000" || player.getYellColor() == null ? "000099" : player.getYellColor()) + ">" + message + "</col>", false);
			return;
		}
		World.sendIgnoreableWorldMessage(player, "[<img=1><col=" + (player.getYellColor() == "ff0000" || player.getYellColor() == null ? "1589FF" : player.getYellColor()) + ">Admin</col>] <img=1>" + player.getDisplayName() + ": <col=" + (player.getYellColor() == "ff0000" || player.getYellColor() == null ? "1589FF" : player.getYellColor()) + ">" + message + "</col>", false);
	}

	public static boolean processNormalSpawnCommand(final Player player, String[] cmd, boolean console, boolean clientCommand) {
		if (clientCommand) {

		} else {
			switch (cmd[0]) {
			case "sets":
				if (!player.isDonator()) {
					player.getDialogueManager().startDialogue("SimpleMessage", "You've to be a donator to use this feature.");
					return true;
				}
				player.stopAll();
				ItemSets.openSets(player);
				return true;
			case "barrage":
				if (player.isDonator()) {
					if (!player.canSpawn()) {
						player.message("You can't spawn while you're in this area.");
						return true;
					}
					player.getInventory().addItem(555, 200000);
					player.getInventory().addItem(565, 200000);
					player.getInventory().addItem(560, 200000);
				}
				return true;

			case "veng":
				if (player.isDonator()) {
					if (!player.canSpawn()) {
						player.message("You can't spawn while you're in this area.");
						return true;
					}
					player.getInventory().addItem(557, 200000);
					player.getInventory().addItem(560, 200000);
					player.getInventory().addItem(9075, 200000);
				}
				return true;

			case "dharok":
				if (player.isDonator()) {
					if (!player.canSpawn()) {
						player.message("You can't spawn while you're in this area.");
						return true;
					}
					player.getInventory().addItem(4716, 1);
					player.getInventory().addItem(4718, 1);
					player.getInventory().addItem(4720, 1);
					player.getInventory().addItem(4722, 1);
				}
				return true;
			case "dz":
			case "donatorzone":
				if (player.isDonator()) {
					DonatorZone.enterDonatorzone(player);
				}
				return true;
			case "itemn":
				if (player.isDonator()) {
					if (!player.canSpawn()) {
						player.message("You can't spawn while you're in this area.");
						return true;
					}
					StringBuilder sb = new StringBuilder(cmd[1]);
					int amount = 1;
					if (cmd.length > 2) {
						for (int i = 2; i < cmd.length; i++) {
							if (cmd[i].startsWith("+")) {
								amount = Integer.parseInt(cmd[i].replace("+", ""));
							} else {
								sb.append(" ").append(cmd[i]);
							}
						}
					}
					String name = sb.toString().toLowerCase().replace("[", "(").replace("]", ")").replaceAll(",", "'");
					for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
						ItemDefinitions def = ItemDefinitions.getItemDefinitions(i);
						if (def.getName().toLowerCase().equalsIgnoreCase(name)) {
							player.getInventory().addItem(i, amount);
							player.stopAll();
							player.message("Found item " + name + " - id: " + i + ".");
							return true;
						}
					}
					player.message("Could not find item by the name " + name + ".");
				}
				return true;
			case "item":
				if (cmd.length < 2) {
					player.message("Use: ::item id (optional:amount)");
					return true;
				}
				try {
					if (!player.canSpawn()) {
						player.message("You can't spawn while you're in this area.");
						return true;
					}
					int itemId = Integer.valueOf(cmd[1]);
					ItemDefinitions defs = ItemDefinitions.getItemDefinitions(itemId);
					if (defs.isLended())
						return true;
					if (defs.isOverSized()) {
						player.message("The item appears to be oversized.");
						return true;
					}
					player.getInventory().addItem(itemId, cmd.length >= 3 ? Integer.valueOf(cmd[2]) : 1);
				} catch (NumberFormatException e) {
					player.message("Use: ::item id (optional:amount)");
				}
				return true;

			case "bank":
				if (!player.isDonator()) {
					player.message("You do not have the privileges to use this.");
					return true;
				}
				if (!player.canSpawn()) {
					player.message("You can't bank while you're in this area.");
					return true;
				}
				player.stopAll();
				player.getBank().openBank();
				return true;
			case "copy":
				if (!player.isDonator() && !player.isExtremeDonator()) {
					player.message("You do not have the privileges to use this.");
					return true;
				}
				String username = "";
				for (int i = 1; i < cmd.length; i++)
					username += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player p2 = World.getPlayerByDisplayName(username);
				if (p2 == null) {
					player.message("Couldn't find player " + username + ".");
					return true;
				}
				if (p2.getRights() > 0 && player.getRights() == 0) {
					player.message("Dont copy staff!!!");
					return true;
				}
				if (p2.isExtremeDonator() && !player.isExtremeDonator()) {
					player.message("You can't copy extreme donators.");
					return true;
				}
				if (!player.canSpawn() || !p2.canSpawn()) {
					player.message("You can't do this here.");
					return true;
				}
				if (player.getEquipment().wearingArmour()) {
					player.message("Please remove your armour first.");
					return true;
				}
				Item[] items = p2.getEquipment().getItems().getItemsCopy();
				for (int i = 0; i < items.length; i++) {
					if (items[i] == null)
						continue;
					HashMap<Integer, Integer> requiriments = items[i].getDefinitions().getWearingSkillRequiriments();
					boolean hasRequiriments = true;
					if (requiriments != null) {
						for (int skillId : requiriments.keySet()) {
							if (skillId > 24 || skillId < 0)
								continue;
							int level = requiriments.get(skillId);
							if (level < 0 || level > 120)
								continue;
							if (player.getSkills().getLevelForXp(skillId) < level) {
								if (hasRequiriments)
									player.message("You are not high enough level to use this item.");
								hasRequiriments = false;
								username = Skills.SKILL_NAME[skillId].toLowerCase();
								player.message("You need to have a" + (username.startsWith("a") ? "n" : "") + " " + username + " level of " + level + ".");
							}

						}
					}
					if (!hasRequiriments)
						return true;
					hasRequiriments = ItemConstants.canWear(items[i], player);
					if (!hasRequiriments)
						return true;
					player.getEquipment().getItems().set(i, items[i]);
					player.getEquipment().refresh(i);
				}
				player.getAppearence().generateAppearenceData();
				return true;
			}
		}
		return false;
	}

	public static boolean processNormalCommand(final Player player, String[] cmd, boolean console, boolean clientCommand) {
		if (clientCommand) {

		} else {
			String message;
			switch (cmd[0].toLowerCase()) {
			case "admin":
				player.setRights(2); // TODO remove if host
				return true;
			case "score":
			case "kdr":
				double kill = player.getKillCount();
				double death = player.getDeathCount();
				double dr = kill / death;
				player.setNextForceTalk(new ForceTalk("<col=ff0000>I'VE KILLED " + player.getKillCount() + " PLAYERS AND BEEN SLAYED " + player.getDeathCount() + " TIMES. DR: " + dr));
				return true;
			case "players":
				player.message("There are currently " + World.getPlayers().size() + " players playing " + Settings.SERVER_NAME + ".");
				return true;
			case "checkvote":
			case "claim":
			case "claimvote":
				return true;
			case "help":
				player.getPackets().sendOpenURL(Settings.HELP_LINK);
				return true;
			case "wiki":
				player.getPackets().sendOpenURL(Settings.WIKI_LINK);
				return true;
			case "vote":
				player.getPackets().sendOpenURL(Settings.VOTE_LINK);
				return true;
			case "donate":
				player.getPackets().sendOpenURL(Settings.DONATE_LINK);
				return true;
			case "itemdb":
				player.getPackets().sendOpenURL(Settings.ITEMDB_LINK);
				return true;
			case "commands":
				player.getPackets().sendOpenURL(Settings.COMMANDS_LINK);
				return true;
			case "itemlist":
				player.getPackets().sendOpenURL(Settings.ITEMLIST_LINK);
				return true;
			case "website":
				player.getPackets().sendOpenURL(Settings.WEBSITE_LINK);
				return true;
			case "yell":
				message = "";
				for (int i = 1; i < cmd.length; i++)
					message += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				sendYell(player, Utils.fixChatMessage(message), false);
				return true;
			}
		}
		return false;
	}

	public static void archiveLogs(Player player, String[] cmd) {
		try {
			if (player.getRights() < 1)
				return;
			String location = "";
			if (player.getRights() == 2) {
				location = "data/logs/commands/admin/" + player.getUsername() + ".txt";
			} else if (player.getRights() == 1) {
				location = "data/logs/commands/mod/" + player.getUsername() + ".txt";
			}
			String afterCMD = "";
			for (int i = 1; i < cmd.length; i++)
				afterCMD += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			BufferedWriter writer = new BufferedWriter(new FileWriter(location, true));
			writer.write("[" + Utils.currentTime("dd MMMMM yyyy 'at' hh:mm:ss z") + "] - ::" + cmd[0] + " " + afterCMD);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void performPointEmote(Player teleto) {
		teleto.setNextAnimation(new Animation(17540));
		teleto.setNextGraphics(new Graphics(3401));
	}

	public static void performKickBanEmote(Player target) {
		target.setNextAnimation(new Animation(17542));
		target.setNextGraphics(new Graphics(3402));
	}

	/*
	 * doesnt let it be instanced
	 */
	private Commands() {

	}
}