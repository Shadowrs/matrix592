package com.rs.game.player.actions;

import com.rs.game.WorldTile;
import com.rs.game.player.Player;

public class FightPitsViewingOrb extends Action {

	public static final WorldTile[] ORB_TELEPORTS = { new WorldTile(4571, 5092, 0), new WorldTile(4571, 5107, 0), new WorldTile(4590, 5092, 0), new WorldTile(4571, 5077, 0), new WorldTile(4557, 5092, 0) };

	private WorldTile tile;

	@Override
	public boolean start(Player player) {
		if (!process(player))
			return false;
		tile = new WorldTile(player);
		player.lock();
		player.getAppearence().switchHidden();
		player.getPackets().sendBlackOut(5);
		player.teleport(ORB_TELEPORTS[0]);
		player.getInterfaceManager().sendInventoryInterface(374);
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (player.getPoison().isPoisoned()) {
			player.message("You can't use orb while you're poisoned.");
			return false;
		}
		if (player.getFamiliar() != null) {
			player.message("You can't use orb with a familiar.");
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		return 0;
	}

	@Override
	public void stop(final Player player) {
		player.lock(2);
		player.getInterfaceManager().removeInventoryInterface();
		player.getAppearence().switchHidden();
		player.getPackets().sendBlackOut(0);
		player.teleport(tile);
	}
}
