package com.rs.game.player.dialogues.impl;

import com.rs.game.WorldTile;
import com.rs.game.minigames.ZarosGodwars;
import com.rs.game.player.dialogues.Dialogue;

public final class NexEntrance extends Dialogue {

	@Override
	public void start() {
		sendDialogue("The room beyond this point is a prison!", "There is no way out other than death or teleport.", "Only those who endure dangerous encounters should proceed.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue("There are currently " + ZarosGodwars.getPlayers().size() + " people fighting.<br>Do you wish to join them?", "Climb down.", "Stay here.");
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				player.teleport(new WorldTile(2911, 5204, 0));
				player.getControlerManager().startControler("ZGDControler");
			}
			end();
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
