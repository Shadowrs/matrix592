package com.rs.game.player;

import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.player.content.Commands;
import com.rs.game.player.content.TicketSystem;
import com.rs.utils.Utils;

public class SuppportCmds {
    public static boolean processSupportCommands(Player player, String[] cmd, boolean console, boolean clientCommand) {
        if (clientCommand) {

        } else {
            switch (cmd[0].toLowerCase()) {
            case "sz":
                if (player.isLocked() || player.getControlerManager().getControler() != null) {
                    player.getSocialManager().sendGameMessage("You cannot tele anywhere from here.");
                    return true;
                }
                player.setNextWorldTile(new WorldTile(2667, 10396, 0));
                return true;
            case "realnames":
                for (int i = 10; i < World.getPlayers().size() + 10; i++)
                    player.getPackets().sendIComponentText(275, i, "");
                for (int i = 0; i < World.getPlayers().size() + 1; i++) {
                    Player p2 = World.getPlayers().get(i);
                    if (p2 == null)
                        continue;
                    player.getPackets().sendIComponentText(275, i + 10, p2.getDisplayName() + " - " + Utils.formatPlayerNameForDisplay(p2.getUsername()));
                }
                player.getPackets().sendIComponentText(275, 1, "Displayname - Username");
                player.getInterfaceManager().sendInterface(275);
                return true;
            case "staffyell":
                String message = "";
                for (int i = 1; i < cmd.length; i++)
                    message += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
                Commands.sendYell(player, Utils.fixChatMessage(message), true);
                return true;

            case "ticket":
                TicketSystem.answerTicket(player);
                return true;

            case "finishticket":
                TicketSystem.removeTicket(player);
                return true;

            }
        }
        return false;
    }
}
