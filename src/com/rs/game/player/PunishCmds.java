package com.rs.game.player;

import com.rs.game.World;
import com.rs.utils.Encrypt;
import com.rs.utils.IPBanL;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

public class PunishCmds {
    public static boolean processPunishmentCommand(final Player player, String[] cmd, boolean console, boolean clientCommand) {
        if (clientCommand)
            return false;

        switch (cmd[0].toLowerCase()) {
        case "spawnnpcs":
            for (int i = 0; i < Integer.parseInt(cmd[2]); i++)
                World.spawnNPC(Integer.parseInt(cmd[1]), player, -1, true, true);
            return true;
        case "nexcontroler":
            player.getControlerManager().startControler("ZGDControler");
            return true;
        case "changepassother":
            if (player.getRights() < 2) {
                player.message("Admin+ only!");
                return true;
            }

            String name = cmd[1];
            Player target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
            if (target == null) {
                player.message("Target not found.");
                return true;
            }
            target.setUsername(Utils.formatPlayerNameForProtocol(name));
            target.setPassword(Encrypt.encryptSHA1(cmd[2]));
            player.message("Password changed.");
            SerializableFilesManager.savePlayer(target);
            return true;
        case "ipban":
            if (player.getRights() < 1) {
                player.message("Mod+ only!");
                return true;
            }

            name = "";
            for (int i = 1; i < cmd.length; i++)
                name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
            target = World.getPlayerByDisplayName(name);
            boolean loggedIn = true;
            if (target == null) {
                target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
                if (target != null)
                    target.setUsername(Utils.formatPlayerNameForProtocol(name));
                loggedIn = false;
            }
            if (target != null) {
                if (target.getRights() == 2)
                    return true;
                IPBanL.ban(target, loggedIn);
                player.message("You've permanently ipbanned " + (loggedIn ? target.getDisplayName() : name) + ".");
            } else {
                player.message("Couldn't find player " + name + ".");
            }
            return true;
        case "permban":
            if (player.getRights() < 1) {
                player.message("Mod+ only!");
                return true;
            }

            name = "";
            for (int i = 1; i < cmd.length; i++)
                name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
            target = World.getPlayerByDisplayName(name);
            if (target != null) {
                if (target.getRights() == 2)
                    return true;
                target.setPermBanned(true);
                target.message("You've been perm banned by " + Utils.formatPlayerNameForDisplay(player.getUsername()) + ".");
                player.message("You have perm banned: " + target.getDisplayName() + ".");
                target.getSession().close();
                SerializableFilesManager.savePlayer(target);
            } else {
                target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
                if (target.getRights() == 2)
                    return true;
                target.setPermBanned(true);
                player.message("You have perm banned: " + Utils.formatPlayerNameForDisplay(name) + ".");
                SerializableFilesManager.savePlayer(target);
            }
            return true;
        case "ban":
            if (player.getRights() < 1) {
                player.message("Mod+ only!");
                return true;
            }

            name = "";
            for (int i = 1; i < cmd.length; i++)
                name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
            target = World.getPlayerByDisplayName(name);
            if (target != null) {
                target.setBanned(Utils.currentTimeMillis() + (48 * 60 * 60 * 1000));
                target.getSession().close();
                player.message("You have banned 48 hours: " + target.getDisplayName() + ".");
            } else {
                name = Utils.formatPlayerNameForProtocol(name);
                if (!SerializableFilesManager.containsPlayer(name)) {
                    player.message("Account name " + Utils.formatPlayerNameForDisplay(name) + " doesn't exist.");
                    return true;
                }
                target = SerializableFilesManager.loadPlayer(name);
                target.setUsername(name);
                target.setBanned(Utils.currentTimeMillis() + (48 * 60 * 60 * 1000));
                player.message("You have banned 48 hours: " + Utils.formatPlayerNameForDisplay(name) + ".");
                SerializableFilesManager.savePlayer(target);
            }
            return true;
        case "unban":
            if (player.getRights() < 2) {
                player.message("Admin+ only!");
                return true;
            }

            name = "";
            for (int i = 1; i < cmd.length; i++)
                name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
            target = World.getPlayerByDisplayName(name);
            if (target != null) {
                IPBanL.unban(target);
                player.message("You have unbanned: " + target.getDisplayName() + ".");
            } else {
                name = Utils.formatPlayerNameForProtocol(name);
                if (!SerializableFilesManager.containsPlayer(name)) {
                    player.message("Account name " + Utils.formatPlayerNameForDisplay(name) + " doesn't exist.");
                    return true;
                }
                target = SerializableFilesManager.loadPlayer(name);
                target.setUsername(name);
                IPBanL.unban(target);
                player.message("You have unbanned: " + target.getDisplayName() + ".");
                SerializableFilesManager.savePlayer(target);
            }
            return true;
        case "mute":
            name = "";
            for (int i = 1; i < cmd.length; i++)
                name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
            target = World.getPlayerByDisplayName(name);
            if (target != null) {
                target.setMuted(Utils.currentTimeMillis() + (player.getRights() >= 1 ? (48 * 60 * 60 * 1000) : (1 * 60 * 60 * 1000)));
                target.message("You've been muted for " + (player.getRights() >= 1 ? " 48 hours by " : "1 hour by ") + Utils.formatPlayerNameForDisplay(player.getUsername()) + ".");
                player.message("You have muted " + (player.getRights() >= 1 ? " 48 hours by " : "1 hour by ") + target.getDisplayName() + ".");
            } else {
                name = Utils.formatPlayerNameForProtocol(name);
                if (!SerializableFilesManager.containsPlayer(name)) {
                    player.message("Account name " + Utils.formatPlayerNameForDisplay(name) + " doesn't exist.");
                    return true;
                }
                target = SerializableFilesManager.loadPlayer(name);
                target.setUsername(name);
                target.setMuted(Utils.currentTimeMillis() + (player.getRights() >= 1 ? (48 * 60 * 60 * 1000) : (1 * 60 * 60 * 1000)));
                player.message("You have muted " + (player.getRights() >= 1 ? " 48 hours by " : "1 hour by ") + target.getDisplayName() + ".");
                SerializableFilesManager.savePlayer(target);
            }
            return true;
        case "unmute":
            if (player.getRights() < 1) {
                player.message("Mod+ only!");
                return true;
            }

            name = "";
            for (int i = 1; i < cmd.length; i++)
                name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
            target = World.getPlayerByDisplayName(name);
            if (target != null) {
                target.setMuted(0);
                target.message("You've been unmuted by " + Utils.formatPlayerNameForDisplay(player.getUsername()) + ".");
                player.message("You have unmuted: " + target.getDisplayName() + ".");
                SerializableFilesManager.savePlayer(target);
            } else {
                target = (Player) SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
                target.setMuted(0);
                player.message("You have unmuted: " + Utils.formatPlayerNameForDisplay(name) + ".");
                SerializableFilesManager.savePlayer(target);
            }
            return true;
        case "jail":
            if (player.getRights() < 1) {
                player.message("Mod+ only!");
                return true;
            }

            name = "";
            for (int i = 1; i < cmd.length; i++)
                name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
            target = World.getPlayerByDisplayName(name);
            if (target != null) {
                target.setJailed(Utils.currentTimeMillis() + (24 * 60 * 60 * 1000));
                target.getControlerManager().startControler("JailControler");
                target.message("You've been Jailed for 24 hours by " + Utils.formatPlayerNameForDisplay(player.getUsername()) + ".");
                player.message("You have Jailed 24 hours: " + target.getDisplayName() + ".");
                SerializableFilesManager.savePlayer(target);
            } else {
                target = (Player) SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
                target.setJailed(Utils.currentTimeMillis() + (24 * 60 * 60 * 1000));
                player.message("You have muted 24 hours: " + Utils.formatPlayerNameForDisplay(name) + ".");
                SerializableFilesManager.savePlayer(target);
            }
            return true;
        case "unjail":
            if (player.getRights() < 1) {
                player.message("Mod+ only!");
                return true;
            }

            name = "";
            for (int i = 1; i < cmd.length; i++)
                name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
            target = World.getPlayerByDisplayName(name);
            if (target != null) {
                target.setJailed(0);
                target.getControlerManager().startControler("JailControler");
                target.message("You've been unjailed by " + Utils.formatPlayerNameForDisplay(player.getUsername()) + ".");
                player.message("You have unjailed: " + target.getDisplayName() + ".");
                SerializableFilesManager.savePlayer(target);
            } else {
                target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
                target.setJailed(0);
                player.message("You have unjailed: " + Utils.formatPlayerNameForDisplay(name) + ".");
                SerializableFilesManager.savePlayer(target);
            }
            return true;

        case "kick":
            if (player.getRights() < 1) {
                player.message("Mod+ only!");
                return true;
            }

            name = "";
            for (int i = 1; i < cmd.length; i++)
                name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
            target = World.getPlayerByDisplayName(name);
            if (target == null) {
                player.message(Utils.formatPlayerNameForDisplay(name) + " is not logged in.");
                return true;
            }
            target.getSession().close();
            player.message("You have kicked: " + target.getDisplayName() + ".");
            return true;
        case "forcekick":
            if (player.getRights() < 1) {
                player.message("Mod+ only!");
                return true;
            }

            name = "";
            for (int i = 1; i < cmd.length; i++)
                name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
            target = World.getPlayerByDisplayName(name);
            if (target == null) {
                player.message(Utils.formatPlayerNameForDisplay(name) + " is not logged in.");
                return true;
            }
            target.forceLogout();
            player.message("You have kicked: " + target.getDisplayName() + ".");
            return true;
        default:
            return false;
        }
    }
}
