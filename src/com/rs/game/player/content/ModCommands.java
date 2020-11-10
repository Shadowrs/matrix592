package com.rs.game.player.content;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.player.Player;

public class ModCommands {
    public static boolean processModCommand(Player player, String[] cmd, boolean console, boolean clientCommand) {
        if (clientCommand) {

        } else {
            switch (cmd[0].toLowerCase()) {
            case "teleto":
                if ((player.isLocked() || player.getControlerManager().getControler() != null) && player.getRights() != 2) {
                    player.getSocialManager().sendGameMessage("You cannot tele anywhere from here.");
                    return true;
                }
                String name = "";
                for (int i = 1; i < cmd.length; i++)
                    name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
                Player target = World.getPlayerByDisplayName(name);
                if (target == null)
                    player.getSocialManager().sendGameMessage("Couldn't find player " + name + ".");
                else {
                    player.setNextWorldTile(target);
                }
                return true;
            case "teletome":
                name = "";
                for (int i = 1; i < cmd.length; i++)
                    name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
                target = World.getPlayerByDisplayName(name);
                if (target == null)
                    player.getSocialManager().sendGameMessage("Couldn't find player " + name + ".");
                else {
                    if (target.isLocked() || target.getControlerManager().getControler() != null) {
                        player.getSocialManager().sendGameMessage("You cannot teleport this player.");
                        return true;
                    }
                    if (target.getRights() > 1) {
                        player.getSocialManager().sendGameMessage("Unable to teleport a developer to you.");
                        return true;
                    }
                    target.setNextWorldTile(player);
                }
                return true;
            case "sendhome":
                name = "";
                for (int i = 1; i < cmd.length; i++)
                    name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
                target = World.getPlayerByDisplayName(name);
                if (target == null)
                    player.getSocialManager().sendGameMessage("Couldn't find player " + name + ".");
                else {
                    target.unlock();
                    target.getControlerManager().forceStop();
                    if (target.getNextWorldTile() == null) // if controler
                        // wont tele the
                        // player
                        target.setNextWorldTile(Settings.START_PLAYER_LOCATION);
                    player.getSocialManager().sendGameMessage("You have unnulled: " + target.getDisplayName() + ".");
                    return true;
                }
                return true;
            }
        }
        return false;
    }
}
