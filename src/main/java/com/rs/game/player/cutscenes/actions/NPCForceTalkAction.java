package com.rs.game.player.cutscenes.actions;

import com.rs.game.ForceTalk;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.dialogues.Dialogue;

public class NPCForceTalkAction extends CutsceneAction {

	private String text;

	public NPCForceTalkAction(int cachedObjectIndex, String text, int actionDelay) {
		super(cachedObjectIndex, actionDelay);
		this.text = text;
	}

	@Override
	public void process(Player player, Object[] cache) {
		NPC npc = (NPC) cache[getCachedObjectIndex()];
		npc.setNextForceTalk(new ForceTalk(text));
		Dialogue.sendNPCDialogueNoContinue(player, npc.getId(), 9827, text);
	}

}
