package com.rs.game.player.content;

import com.rs.Launcher;
import com.rs.Settings;
import com.rs.cache.loaders.AnimationDefinitions;
import com.rs.cache.loaders.GraphicDefinitions;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.ColorChange;
import com.rs.game.Entity;
import com.rs.game.ForceMovement;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Region;
import com.rs.game.SecondaryBar;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.item.ItemsContainer;
import com.rs.game.minigames.FightPits;
import com.rs.game.minigames.clanwars.ClanWars;
import com.rs.game.minigames.clanwars.WallHandler;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.impl.NexCombat;
import com.rs.game.npc.others.Bork;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.SlayerManager;
import com.rs.game.player.content.grandExchange.GrandExchange;
import com.rs.game.player.cutscenes.NexCutScene;
import com.rs.game.route.Flags;
import com.rs.game.route.RouteFinder;
import com.rs.game.route.WalkRouteFinder;
import com.rs.game.route.strategy.FixedTileStrategy;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.IPBanL;
import com.rs.utils.IPMuteL;
import com.rs.utils.Logger;
import com.rs.utils.PkRank;
import com.rs.utils.Utils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

public class AdminCmds {
    private static boolean trollRunning = false;
    private static String trollTarget = null;
    private static TimerTask prjDebugTask = null;
    private static int prjDebugInterval = 600;
    private static int prjDebugTarget = -1;
    private static int prjDebugStartAnim = -1;
    private static int prjDebugStartGfx = -1;
    private static int prjDebugPrjGfx = -1;
    private static int prjDebugDestAnim = -1;
    private static int prjDebugDestGfx = -1;
    private static int prjDebugStartHeight = -1;
    private static int prjDebugEndHeight = -1;
    private static int prjDebugDelay = -1;
    private static int prjDebugSpeed = -1;
    private static int prjDebugSlope = -1;
    private static int prjDebugAngle = -1;

    public static boolean processAdminCommand(final Player player, String[] cmd, boolean console, boolean clientCommand) {
        if (clientCommand) {
            switch (cmd[0]) {
            case "tele":
                cmd = cmd[1].split(",");
                int plane = Integer.valueOf(cmd[0]);
                int x = Integer.valueOf(cmd[1]) << 6 | Integer.valueOf(cmd[3]);
                int y = Integer.valueOf(cmd[2]) << 6 | Integer.valueOf(cmd[4]);
                player.setNextWorldTile(new WorldTile(x, y, plane));
                return true;
            }
        } else {
            String name;
            Player target;
            WorldObject object;
            switch (cmd[0].toLowerCase()) {

            case "lscene":
                player.setLargeSceneView(!player.hasLargeSceneView());
                player.getSocialManager().sendPanelBoxMessage("Large Scene Rendering: " + player.hasLargeSceneView());
                return true;

            case "pmchatinter":
                CoresManager.fastExecutor.schedule(new TimerTask() {
                    int count = 10;
                    @Override
                    public void run() {
                        try {
                            player.getInterfaceManager().setWindowInterface(count, 754);
                            System.out.println(count);
                            count++;
                        } catch (Throwable e) {
                            Logger.handle(e);
                        }
                    }
                }, 0, 600);
                return true;

            case "summon":
                Summoning.openInfusionInterface(player);
                return true;

            case "nm":
                for (NPC n : World.getNPCs()) {

                    if (n != null && Utils.getDistance(player, n) < 30) {
                        n.setNextAnimation(new Animation(3550));
                        n.setNextGraphics(new Graphics(2259));
                        n.setNextGraphics(new Graphics(2260, 1, 200));
                        n.setNextFaceEntity(player);
                        n.setNextForceTalk(new ForceTalk("force talk"));
                        n.setNextNPCTransformation(1);
                        n.setNextSecondaryBar(new SecondaryBar(0, 350, 1, false));
                        n.setNextColorChange(new ColorChange(0, 150, new int[] { 43, 222, 222, 0 }));
                    }
                }

                return true;

            case "questinter":
                player.getInterfaceManager().sendInterface(275);
                player.getPackets().sendRunScript(1207, 12);
                return true;

            case "rinv":
                player.getInventory().refresh();
                return true;

            case "tmo":
                PlayerLook.openThessaliasMakeOver(player);
                return true;

            case "mo":
                PlayerLook.openMageMakeOver(player);
                return true;

            case "cd":
                PlayerLook.openCharacterCustomizing(player);
                return true;

            case "yrs":
                PlayerLook.openYrsaShop(player);
                return true;

            case "in":
            case "itemn":
            case "itn":
                case "fi":
                name = "";
                for (int i = 1; i < cmd.length; i++) {
                    name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
                }

                int count = 0;
                for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
                    Item item = new Item(i);
                    if (item.getDefinitions().getName().toLowerCase().contains(name.toLowerCase())) {
                        count++;
                        if (count >= 150) {
                            player.getSocialManager().sendPanelBoxMessage("<col=FF00 00>Found over 150 results for " + Utils.formatPlayerNameForDisplay(name) + ". Only 150 listed.");
                            return true;
                        }
                        String suffix = item.getDefinitions().isNoted() ? "(noted)" : "";
                        player.getSocialManager().sendPanelBoxMessage("<col=00FF FF>" + Utils.formatPlayerNameForDisplay(item.getName()) + suffix + "</col> (Id: <col=00FF00>" + item.getId() + "</col>)");
                    }
                }
                player.getSocialManager().sendPanelBoxMessage("<col=FF00 00>Found " + count + " results for the item " + Utils.formatPlayerNameForDisplay(name) + ".");

                return true;

                case "fn":
                    name = "";
                    for (int i = 1; i < cmd.length; i++) {
                        name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
                    }

                    count = 0;
                    for (int i = 0; i < Utils.getNPCDefinitionsSize(); i++) {
                        NPCDefinitions item = NPCDefinitions.getNPCDefinitions(i);
                        if (item.name.toLowerCase().contains(name.toLowerCase())) {
                            count++;
                            if (count >= 150) {
                                player.getSocialManager().sendPanelBoxMessage("<col=FF00 00>Found over 150 results for " + Utils.formatPlayerNameForDisplay(name) + ". Only 150 listed.");
                                return true;
                            }
                            player.getSocialManager().sendPanelBoxMessage("<col=00FF FF>" + Utils.formatPlayerNameForDisplay(item.getName()) + "</col> (Id: <col=00FF00>" + i + "</col>)");
                        }
                    }
                    player.getSocialManager().sendPanelBoxMessage("<col=FF00 00>Found " + count + " results for the item " + Utils.formatPlayerNameForDisplay(name) + ".");

                    return true;

            case "spellbook":
                player.getCombatDefinitions().setSpellBook(Integer.valueOf(cmd[1]));
                return true;
            case "curses":
                player.getDialogueManager().startDialogue("ZarosAltar");
                return true;

            case "findanim":
                int gfx = Integer.valueOf(cmd[1]);
                GraphicDefinitions def = GraphicDefinitions.getAnimationDefinitions(gfx);
                player.getSocialManager().sendPanelBoxMessage("Finding animation for graphics(" + gfx + ") = " + (def.animation - 1));
                return true;

            case "bounty":
                // skull... interfaceconfigs/hideicomponent
                // 3 is safe
                // 4 is safe faded for timer
                // 6 PVP hot spot

                // 591 is bounty hunter interface
                // It looks like the lower 6 bits (i.e. bits 0 to 5) of
                // config/varp/setting 1410 control how filled the circle is.
                // The circle should be automatically updated when the value
                // gets to 8, 15, 23, 30, 38, 45, 53, and 60 -- maybe the value
                // represents some time in minutes?

                /* Timer for PVP */
                /*
                 * /player.getPackets().sendHideIComponent(745, 4, false); player.getPackets().sendHideIComponent(745, 5, false);
                 * player.getPackets().sendIComponentText(745, 5, "100"); //set timer player.getPackets().sendRunScriptBlank(1178); /
                 */
                // EP

                // player.getPackets().sendIComponentText(548, 8, "EP: 1000");
                // player.getPackets().sendIComponentText(746, 158, "EP: 1002");

                return true;

            case "title":
                player.getAppearence().setTitle(Integer.valueOf(cmd[1]));
                return true;
            case "prjdebugmisc":
                prjDebugSlope = Integer.parseInt(cmd[1]);
                prjDebugAngle = Integer.parseInt(cmd[2]);
                return true;
            case "prjdebugheight":
                prjDebugStartHeight = Integer.parseInt(cmd[1]);
                prjDebugEndHeight = Integer.parseInt(cmd[2]);
                return true;
            case "prjdebugdelay":
                prjDebugDelay = Integer.parseInt(cmd[1]);
                prjDebugSpeed = Integer.parseInt(cmd[2]);
                return true;
            case "prjdebugemote":
                prjDebugStartAnim = Integer.parseInt(cmd[1]);
                prjDebugStartGfx = Integer.parseInt(cmd[2]);
                prjDebugPrjGfx = Integer.parseInt(cmd[3]);
                prjDebugDestAnim = Integer.parseInt(cmd[4]);
                prjDebugDestGfx = Integer.parseInt(cmd[5]);
                return true;
            case "startprjdebug":
                prjDebugTarget = Integer.parseInt(cmd[1]);
                int interval = Integer.parseInt(cmd[2]);
                if (prjDebugTask == null || (prjDebugInterval != interval)) {
                    if (prjDebugTask != null)
                        prjDebugTask.cancel();
                    prjDebugInterval = interval;
                    CoresManager.fastExecutor.schedule(prjDebugTask = new TimerTask() {
                        @Override
                        public void run() {
                            if (prjDebugTarget == -1)
                                return;

                            Entity _target = null;
                            if (prjDebugTarget >= 0)
                                _target = World.getNPCs().get(prjDebugTarget);
                            else
                                _target = World.getPlayers().get((-prjDebugTarget) - 2);

                            if (_target == null)
                                return;

                            player.getPackets().sendProjectileProper(player, player.getSize(), player.getSize(), _target, _target.getSize(), _target.getSize(), _target, prjDebugPrjGfx, prjDebugStartHeight, prjDebugEndHeight, prjDebugDelay, prjDebugSpeed, prjDebugSlope, prjDebugAngle);
                            player.setNextAnimation(new Animation(prjDebugStartAnim));
                            player.setNextGraphics(new Graphics(prjDebugStartGfx));
                            _target.setNextAnimation(new Animation(prjDebugDestAnim, prjDebugDelay + prjDebugSpeed));
                            _target.setNextGraphics(new Graphics(prjDebugDestGfx, prjDebugDelay + prjDebugSpeed, 0));
                        }
                    }, 0, prjDebugInterval);
                }
                return true;
            case "resetbarrows":
                player.resetBarrows();
                return true;
            case "stopprjdebug":
                prjDebugTarget = -1;
                return true;
            case "startevent":
                name = "";
                for (int i = 1; i < cmd.length; i++)
                    name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
                if (name.length() <= 0) {
                    player.getSocialManager().sendGameMessage("bad name.");
                    return true;
                }
                if (player.getControlerManager().getControler() != null) {
                    player.getSocialManager().sendGameMessage("You can't start event here");
                    return true;
                }
                EconomyManager.startEvent(name, new WorldTile(player.getX(), player.getY(), player.getPlane()));
                return true;
            case "enablebxp":
                World.sendWorldMessage("<col=551177>[Server Message] Bonus EXP has been" + "<col=88aa11> enabled.", false);
                if (!Settings.XP_BONUS_ENABLED)
                    World.addIncreaseElapsedBonusMinutesTak();
                Settings.XP_BONUS_ENABLED = true;
                return true;
            case "disablebxp":
                World.sendWorldMessage("<col=551177>[Server Message] Bonus EXP has been" + "<col=990022> disabled.", false);
                Settings.XP_BONUS_ENABLED = false;
                return true;
            case "stopevent":
                EconomyManager.stopEvent();
                return true;

            case "supertroll":
                name = "";
                for (int i = 1; i < cmd.length; i++)
                    name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
                trollTarget = name;
                if (!trollRunning) {
                    trollRunning = true;
                    CoresManager.fastExecutor.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (trollTarget == null)
                                return;

                            Player target = World.getPlayerByDisplayName(trollTarget);
                            if (target == null || !target.isRunning())
                                return;

                            String[] messages = new String[100];
                            int count = 0;
                            messages[count++] = "Oh look, it's %a again l0l0l0l";
                            messages[count++] = "L0l! Try harder %a";
                            messages[count++] = "It's him! It's him! Everyone! It's %a";
                            messages[count++] = "Sometimes I wonder why %a tries so hard just to fail :L";
                            messages[count++] = "%a, afraid of getting owned by k00ldogs? :L";
                            messages[count++] = "#K00LDOGS #1";
                            messages[count++] = "Lol!!";
                            messages[count++] = "Lmfao";
                            messages[count++] = "ROFL!!";
                            messages[count++] = "%a, why are you even trying :L";
                            messages[count++] = "lolololoolololo";
                            if (target.isDead()) {
                                messages[count++] = "GF";
                                messages[count++] = "GFGFGFGF";
                                messages[count++] = "Owned ahahahahah";
                                messages[count++] = "Kleared k9k9k9k9k9k9k9k9k9k";
                                messages[count++] = "GG";
                                messages[count++] = "IMAFGT";
                            }

                            for (NPC npc : World.getNPCs()) {
                                if (npc == null || npc.isDead() || npc.getPlane() != target.getPlane() || npc.isFrozen())
                                    continue;
                                int deltaX = npc.getX() - target.getX();
                                int deltaY = npc.getY() - target.getY();
                                if (deltaX < -8 || deltaX > 8 || deltaY < -8 || deltaY > 8)
                                    continue;
                                if (Utils.random(4) != 0)
                                    continue;

                                npc.faceEntity(target);
                                npc.addFreezeDelay(2000);
                                npc.setNextForceTalk(new ForceTalk(messages[Utils.random(count)].replace("%a", target.getDisplayName())));
                            }
                        }
                    }, 0, 600);
                }
                player.getSocialManager().sendGameMessage("Found:" + (World.getPlayerByDisplayName(name) != null));
                return true;
            case "stopsupertroll":
                trollTarget = null;
                return true;
            case "scshop":
                StealingCreationShop.openInterface(player);
                return true;
            case "clipflag":
                int mask = World.getMask(player.getPlane(), player.getX(), player.getY());
                StringBuilder flagbuilder = new StringBuilder();
                flagbuilder.append('(');
                for (Field field : Flags.class.getDeclaredFields()) {
                    try {
                        if ((mask & field.getInt(null)) == 0)
                            continue;
                    } catch (Throwable t) {
                        continue;
                    }

                    if (flagbuilder.length() <= 1) {
                        flagbuilder.append("Flags." + field.getName());
                    } else {
                        flagbuilder.append(" | Flags." + field.getName());
                    }
                }
                flagbuilder.append(')');
                System.err.println("Flag is:" + flagbuilder.toString());
                System.out.println(player.getXInRegion() + ", " + player.getYInRegion());
                return true;
            case "walkto":
                int wx = Integer.parseInt(cmd[1]);
                int wy = Integer.parseInt(cmd[2]);
                boolean checked = cmd.length > 3 ? Boolean.parseBoolean(cmd[3]) : false;
                long rstart = System.nanoTime();
                int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, player.getX(), player.getY(), player.getPlane(), player.getSize(), new FixedTileStrategy(wx, wy), false);
                long rtook = (System.nanoTime() - rstart) - WalkRouteFinder.debug_transmittime;
                player.getSocialManager().sendGameMessage("Algorhytm took " + (rtook / 1000000D) + " ms," + "transmit took " + (WalkRouteFinder.debug_transmittime / 1000000D) + " ms, steps:" + steps);
                int[] bufferX = RouteFinder.getLastPathBufferX();
                int[] bufferY = RouteFinder.getLastPathBufferY();
                for (int i = steps - 1; i >= 0; i--) {
                    player.addWalkSteps(bufferX[i], bufferY[i], Integer.MAX_VALUE, checked);
                }

                return true;

            case "test":

                return true;

            case "ugd":
                player.getControlerManager().startControler("UnderGroundDungeon", false, true, true);
                return true;

            case "scb":
                player.getPackets().sendRunScriptBlank(Integer.parseInt(cmd[1]));
                return true;
            case "sc":
                player.getPackets().sendRunScript(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]));
                return true;
            case "sendscriptstr":
                player.getPackets().sendRunScript(Integer.parseInt(cmd[1]), cmd[2]);
                return true;
            case "testresetsof":
                player.getPackets().sendRunScript(5879); // sof_setupHooks();
                // should work
                return true;
            case "sendsofempty":
                player.getPackets().sendItems(665, new Item[13]);
                return true;
            case "sendsofitems":
                Item[] items = new Item[13];
                for (int i = 0; i < items.length; i++)
                    items[i] = new Item(995, i + 1);// items[i] = new
                // Item(995,
                // Utils.random(1000000000)
                // + 1);
                player.getPackets().sendItems(665, items);
                return true;
            case "senditems":
                for (int i = 0; i < 2000; i++)
                    player.getPackets().sendItems(i, new Item[] { new Item(i, 1) });
                return true;
            case "forcewep":
                player.getAppearence().setForcedWeapon(Integer.parseInt(cmd[1]));
                return true;
            case "clearst":
                for (Player p2 : World.getPlayers())
                    p2.getSlayerManager().skipCurrentTask();
                return true;
            case "ectest":
                player.getDialogueManager().startDialogue("EconomyTutorialCutsceneDialog");
                return true;
            case "ecotestcutscene":
                player.getCutscenesManager().play("EconomyTutorialCutscene");
                return true;
            case "istest":
                player.getSlayerManager().sendSlayerInterface(SlayerManager.BUY_INTERFACE);
                return true;
            case "st":
                player.getSlayerManager().setCurrentMaster(Slayer.SlayerMaster.KURADAL);
                player.getSlayerManager().setCurrentTask(true);
                return true;
            case "addpoints":
                player.getSlayerManager().setPoints(5000);
                return true;
            case "testdeath":
                player.getInterfaceManager().sendInterface(18);
                player.getPackets().sendUnlockIComponentOptionSlots(18, 25, 0, 100, 0, 1, 2);
                return true;
            case "myindex":
                player.getSocialManager().sendGameMessage("My index is:" + player.getIndex());
                return true;
            case "defauth": // do not use
                player.setForumAuthID(-1);
                return true;
            case "gw":
                player.getControlerManager().startControler("GodWars");
                return true;
            case "getspawned": {
                List<WorldObject> spawned = World.getRegion(player.getRegionId()).getSpawnedObjects();
                player.getSocialManager().sendGameMessage("region:" + player.getRegionId());
                player.getSocialManager().sendGameMessage("-------");
                for (WorldObject o : spawned) {
                    if (o.getChunkX() == player.getChunkX() && o.getChunkY() == player.getChunkY() && o.getPlane() == player.getPlane()) {
                        player.getSocialManager().sendGameMessage(o.getId() + "," + o.getX() + "," + o.getY() + "," + o.getPlane());
                    }
                }
                player.getSocialManager().sendGameMessage("-------");
                return true;
            }
            case "removeobjects": {
                List<WorldObject> objects = World.getRegion(player.getRegionId()).getAllObjects();
                for (WorldObject o : objects) {
                    if (o.getChunkX() == player.getChunkX() && o.getChunkY() == player.getChunkY() && o.getPlane() == player.getPlane()) {
                        World.removeObject(o);
                    }
                }
                return true;
            }
            case "clearspot":
                name = "";
                for (int i = 1; i < cmd.length; i++)
                    name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
                target = World.getPlayerByDisplayName(name);
                if (target != null) {
                    target.getFarmingManager().resetSpots();
                    player.getSocialManager().sendGameMessage("You have cleared the target's spot.");
                }
                return true;
            case "switchyell":
                Settings.YELL_ENABLED = !Settings.YELL_ENABLED;
                player.getSocialManager().sendGameMessage("All yells are currently " + Settings.YELL_ENABLED);
                return true;
            case "switchbadboy":
                Settings.BAD_BOYS = !Settings.BAD_BOYS;
                player.getSocialManager().sendGameMessage("The donators are currently " + (Settings.BAD_BOYS ? "bad boys like obito." : "good boys like tobi."));
                return true;
            case "clearall":// fail safe only
                for (Player p2 : World.getPlayers()) {
                    if (p2 == null)
                        continue;
                    p2.getFarmingManager().resetSpots();
                }
                return true;
            case "pouches":
                Summoning.openInfusionInterface(player);
                return true;
            case "getclipflag": {
                mask = World.getMask(player.getPlane(), player.getX(), player.getY());
                player.getSocialManager().sendGameMessage("[" + mask + "]");
                return true;
            }

            case "hugemap":
                player.setMapSize(3);
                return true;
            case "normmap":
                player.setMapSize(0);
                return true;

            case "sgar":
                player.getControlerManager().startControler("SorceressGarden");
                return true;

            case "gesearch":
                player.getInterfaceManager().setInterface(true, 752, 7, 389);
                player.getPackets().sendRunScript(570, "Grand Exchange Item Search");
                return true;
            case "ge":
                player.getGeManager().openGrandExchange();
                return true;
            case "ge2":
                player.getGeManager().openCollectionBox();
                return true;
            case "ge3":
                player.getGeManager().openHistory();
                return true;
            case "setpin":
                // player.getBank().openPinSettings();
                return true;
            case "bankpin":
                player.getBank().openPin();
                player.getTemporaryAttributtes().put("recovering_pin", true);
                return true;
            case "configsize":
                player.getSocialManager().sendGameMessage("Config definitions size: 2633, BConfig size: 1929.");
                return true;

            case "runespan":
                player.getControlerManager().startControler("RuneSpanControler");
                return true;
            case "house":
                player.getHouse().enterMyHouse();
                return true;
            case "killingfields":
                player.getControlerManager().startControler("KillingFields");
                return true;
            case "pptest":
                player.getDialogueManager().startDialogue("SimplePlayerMessage", "123");
                return true;
            case "clearfriends":
                player.getFriendsIgnores().getFriends().clear();
                return true;
            case "test2":
                player.getPackets().sendRunScript(800);
                System.out.println("SENT");
                return true;

            case "debugobjects":
                System.out.println("Standing on " + World.getStandartObject(player).getId() + "," + World.getStandartObject(player).getType() + "," + World.getStandartObject(player).getRotation());
                Region r = World.getRegion(player.getRegionY() | (player.getRegionX() << 8));
                if (r == null) {
                    player.getSocialManager().sendGameMessage("Region is null!");
                    return true;
                }
                List<WorldObject> objects = r.getAllObjects();
                if (objects == null) {
                    player.getSocialManager().sendGameMessage("Objects are null!");
                    return true;
                }
                for (WorldObject o : objects) {
                    if (o == null || !o.matches(player)) {
                        continue;
                    }
                    System.out.println("Objects coords: " + o.getX() + ", " + o.getY());
                    System.out.println("[Object]: id=" + o.getId() + ", type=" + o.getType() + ", rot=" + o.getRotation() + ".");
                }
                return true;
            case "telesupport":
                for (Player staff : World.getPlayers()) {
                    if (!staff.isSupporter())
                        continue;
                    staff.setNextWorldTile(player);
                    staff.getSocialManager().sendGameMessage("You been teleported for a staff meeting by " + player.getDisplayName());
                }
                return true;
            case "telemods":
                for (Player staff : World.getPlayers()) {
                    if (staff.getRights() != 1)
                        continue;
                    staff.setNextWorldTile(player);
                    staff.getSocialManager().sendGameMessage("You been teleported for a staff meeting by " + player.getDisplayName());
                }
                return true;
            case "telestaff":
                for (Player staff : World.getPlayers()) {
                    if (!staff.isSupporter() && staff.getRights() != 1)
                        continue;
                    staff.setNextWorldTile(player);
                    staff.getSocialManager().sendGameMessage("You been teleported for a staff meeting by " + player.getDisplayName());
                }
                return true;
            case "teleallfree":
                for (Player p2 : World.getPlayers()) {
                    if (p2 == null || p2.getControlerManager().getControler() != null)
                        continue;
                    p2.setNextWorldTile(player);
                }
                return true;
            case "pickuppet":
                if (player.getPet() != null) {
                    player.getPet().pickup();
                    return true;
                }
                player.getSocialManager().sendGameMessage("You do not have a pet to pickup!");
                return true;
            case "canceltask":
                name = "";
                for (int i = 1; i < cmd.length; i++) {
                    name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
                }
                target = World.getPlayerByDisplayName(name);
                if (target != null) {
                    target.getSlayerManager().skipCurrentTask();
                }
                return true;
            case "messagetest":
                player.getSocialManager().sendMessage(Integer.parseInt(cmd[1]), "YO", player);
                return true;
            case "restartfp":
                FightPits.endGame();
                player.getSocialManager().sendGameMessage("Fight pits restarted!");
                return true;
            case "modelid":
                int id = Integer.parseInt(cmd[1]);
                player.getSocialManager().sendMessage(99, "Model id for item " + id + " is: " + ItemDefinitions.getItemDefinitions(id).modelId, player);
                return true;

            case "teletome":
                name = "";
                for (int i = 1; i < cmd.length; i++)
                    name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
                target = World.getPlayerByDisplayName(name);
                if (target == null)
                    player.getSocialManager().sendGameMessage("Couldn't find player " + name + ".");
                else {
                    target.lock(15);
                    performTeleEmote(target);
                    final Player _target = target;
                    WorldTasksManager.schedule(new WorldTask() {
                        @Override
                        public void run() {
                            _target.setNextAnimation(new Animation(-1));
                            _target.setNextWorldTile(player);

                        }
                    }, 5);
                }
                return true;
            case "pos":
                System.out.println(player+" at "+player.getX()+" "+player.getY()+" "+player.getPlane());
                try {
                    File file = new File("data/positions.txt");
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                    writer.write("|| player.getX() == " + player.getX() + " && player.getY() == " + player.getY() + "");
                    writer.newLine();
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;

            case "agilitytest":
                player.getControlerManager().startControler("BrimhavenAgility");
                return true;
            case "scare":
                name = "";
                for (int i = 1; i < cmd.length; i++)
                    name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
                target = World.getPlayerByDisplayName(name);
                if (target != null) {
                    target.getPackets().sendOpenURL("http://puu.sh/1BUNT");
                    player.getSocialManager().sendGameMessage("You have scared: " + target.getDisplayName() + ".");
                }
                return true;

            case "partyroom":
                player.getInterfaceManager().sendInterface(647);
                player.getInterfaceManager().sendInventoryInterface(336);
                player.getPackets().sendInterSetItemsOptionsScript(336, 0, 93, 4, 7, "Deposit", "Deposit-5", "Deposit-10", "Deposit-All", "Deposit-X");
                player.getPackets().sendIComponentSettings(336, 0, 0, 27, 1278);
                player.getPackets().sendInterSetItemsOptionsScript(336, 30, 90, 4, 7, "Value");
                player.getPackets().sendIComponentSettings(647, 30, 0, 27, 1150);
                player.getPackets().sendInterSetItemsOptionsScript(647, 33, 90, 4, 7, "Examine");
                player.getPackets().sendIComponentSettings(647, 33, 0, 27, 1026);
                ItemsContainer<Item> store = new ItemsContainer<>(215, false);
                for (int i = 0; i < store.getSize(); i++) {
                    store.add(new Item(1048, i));
                }
                player.getPackets().sendItems(529, true, store); // .sendItems(-1,
                // -2, 529,
                // store);

                ItemsContainer<Item> drop = new ItemsContainer<>(215, false);
                for (int i = 0; i < drop.getSize(); i++) {
                    drop.add(new Item(1048, i));
                }
                player.getPackets().sendItems(91, true, drop);// sendItems(-1,
                // -2, 91,
                // drop);

                ItemsContainer<Item> deposit = new ItemsContainer<>(8, false);
                for (int i = 0; i < deposit.getSize(); i++) {
                    deposit.add(new Item(1048, i));
                }
                player.getPackets().sendItems(92, true, deposit);// sendItems(-1,
                // -2, 92,
                // deposit);
                return true;

            case "objectname":
                name = cmd[1].replaceAll("_", " ");
                String option = cmd.length > 2 ? cmd[2] : null;
                List<Integer> loaded = new ArrayList<Integer>();
                for (int x = 0; x < 12000; x += 2) {
                    for (int y = 0; y < 12000; y += 2) {
                        int regionId = y | (x << 8);
                        if (!loaded.contains(regionId)) {
                            loaded.add(regionId);
                            r = World.getRegion(regionId, false);
                            r.loadRegionMap();
                            List<WorldObject> list = r.getAllObjects();
                            if (list == null) {
                                continue;
                            }
                            for (WorldObject o : list) {
                                if (o.getDefinitions().name.equalsIgnoreCase(name) && (option == null || o.getDefinitions().containsOption(option))) {
                                    System.out.println("Object found - [id=" + o.getId() + ", x=" + o.getX() + ", y=" + o.getY() + "]");
                                    // player.getSocialManager().sendGameMessage("Object found - [id="
                                    // + o.getId() + ", x=" + o.getX() +
                                    // ", y="
                                    // + o.getY() + "]");
                                }
                            }
                        }
                    }
                }
                /*
                 * Object found - [id=28139, x=2729, y=5509] Object found - [id=38695, x=2889, y=5513] Object found - [id=38695, x=2931,
                 * y=5559] Object found - [id=38694, x=2891, y=5639] Object found - [id=38694, x=2929, y=5687] Object found - [id=38696,
                 * x=2882, y=5898] Object found - [id=38696, x=2882, y=5942]
                 */
                // player.getSocialManager().sendGameMessage("Done!");
                System.out.println("Done!");
                return true;

            case "bork":
                if (Bork.deadTime > Utils.currentTimeMillis()) {
                    player.getSocialManager().sendGameMessage(Bork.convertToTime());
                    return true;
                }
                player.getControlerManager().startControler("BorkControler", 0, null);
                return true;

            case "killnpc":
                for (NPC n : World.getNPCs()) {
                    if (n == null || n.getId() != Integer.parseInt(cmd[1]))
                        continue;
                    n.applyHit(new Hit(player, n.getMaxHitpoints(), Hit.HitLook.MELEE_DAMAGE));
                }
                return true;

            case "unlockmusic":
                player.getMusicsManager().unlockAll();
                player.getSocialManager().sendPanelBoxMessage("Unlocked almost all music!");
                return true;

            case "sound":
                if (cmd.length < 2) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::sound soundid effecttype");
                    return true;
                }
                try {
                    player.getPackets().sendSound(Integer.valueOf(cmd[1]), 0, cmd.length > 2 ? Integer.valueOf(cmd[2]) : 1);
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::sound soundid");
                }
                return true;

            case "voice":
                if (cmd.length < 2) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::sound soundid effecttype");
                    return true;
                }
                try {
                    player.getPackets().sendSound(Integer.parseInt(cmd[1]), 0, 2);
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::sound soundid");
                }
                return true;

            case "music":
                if (cmd.length < 2) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::sound soundid effecttype");
                    return true;
                }
                try {
                    player.getPackets().sendMusic(Integer.valueOf(cmd[1]));
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::sound soundid");
                }
                return true;

            case "emusic":
                if (cmd.length < 2) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::emusic soundid effecttype");
                    return true;
                }
                try {
                    player.getPackets().sendMusicEffect(Integer.valueOf(cmd[1]));
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::emusic soundid");
                }
                return true;
            case "testdialogue":
                player.getDialogueManager().startDialogue("DagonHai", 7137, player, Integer.parseInt(cmd[1]));
                return true;

            case "removenpcs":
                for (NPC n : World.getNPCs()) {
                    if (n.getId() == Integer.parseInt(cmd[1])) {
                        n.reset();
                        n.finish();
                    }
                }
                return true;
            case "resetkdr":
                player.setKillCount(0);
                player.setDeathCount(0);
                return true;

            case "newtut":
                player.getControlerManager().startControler("TutorialIsland", 0);
                return true;

            case "removecontroler":
                player.getControlerManager().forceStop();
                player.getInterfaceManager().sendInterfaces();
                return true;

            case "nomads":
                for (Player p : World.getPlayers())
                    p.getControlerManager().startControler("NomadsRequiem");
                return true;

            case "item":
                if (cmd.length < 2) {
                    player.getSocialManager().sendGameMessage("Use: ::item id (optional:amount)");
                    return true;
                }
                try {
                    int itemId = Integer.valueOf(cmd[1]);
                    int amount = cmd.length >= 3 ? Integer.valueOf(cmd[2]) : 1;
                    player.getInventory().addItem(itemId, amount);
                    player.stopAll();
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendGameMessage("Use: ::item id (optional:amount)");
                }
                return true;

            case "testp":
                // PartyRoom.startParty(player);
                return true;

            case "copy":
                name = "";
                for (int i = 1; i < cmd.length; i++)
                    name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
                Player p2 = World.getPlayerByDisplayName(name);
                if (p2 == null) {
                    player.getSocialManager().sendGameMessage("Couldn't find player " + name + ".");
                    return true;
                }
                items = p2.getEquipment().getItems().getItemsCopy();
                for (int i = 0; i < items.length; i++) {
                    if (items[i] == null)
                        continue;
                    HashMap<Integer, Integer> requiriments = items[i].getDefinitions().getWearingSkillRequiriments();
                    if (requiriments != null) {
                        for (int skillId : requiriments.keySet()) {
                            if (skillId > 24 || skillId < 0)
                                continue;
                            int level = requiriments.get(skillId);
                            if (level < 0 || level > 120)
                                continue;
                            if (player.getSkills().getLevelForXp(skillId) < level) {
                                name = Skills.SKILL_NAME[skillId].toLowerCase();
                                player.getSocialManager().sendGameMessage("You need to have a" + (name.startsWith("a") ? "n" : "") + " " + name + " level of " + level + ".");
                            }

                        }
                    }
                    player.getEquipment().getItems().set(i, items[i]);
                    player.getEquipment().refresh(i);
                }
                player.getAppearence().generateAppearenceData();
                return true;

            case "god":
                player.setHitpoints(Short.MAX_VALUE);
                player.getEquipment().setEquipmentHpIncrease(Short.MAX_VALUE - 990);
                if (player.getUsername().equalsIgnoreCase("discardedx2"))
                    return true;
                for (int i = 0; i < 10; i++)
                    player.getCombatDefinitions().getBonuses()[i] = 5000;
                for (int i = 14; i < player.getCombatDefinitions().getBonuses().length; i++)
                    player.getCombatDefinitions().getBonuses()[i] = 5000;
                return true;

            case "prayer":
                player.getPrayer().setPrayerpoints(99);
                return true;

            case "karamja":
                player.getDialogueManager().startDialogue("KaramjaTrip", Utils.getRandom(1) == 0 ? 11701 : (Utils.getRandom(1) == 0 ? 11702 : 11703));
                return true;
            case "clanwars":
                // player.setClanWars(new ClanWars(player, player));
                // player.getClanWars().setWhiteTeam(true);
                // ClanChallengeInterface.openInterface(player);
                return true;
            case "checkdisplay":
                for (Player p : World.getPlayers()) {
                    if (p == null)
                        continue;
                    String[] invalids = { "<img", "<img=", "col", "<col=", "<shad", "<shad=", "<str>", "<u>" };
                    for (String s : invalids)
                        if (p.getDisplayName().contains(s)) {
                            player.getSocialManager().sendGameMessage(Utils.formatPlayerNameForDisplay(p.getUsername()));
                        } else {
                            player.getSocialManager().sendGameMessage("None exist!");
                        }
                }
                return true;
            case "cutscene":
                player.getPackets().sendCutscene(Integer.parseInt(cmd[1]));
                return true;
            case "noescape":
                player.getCutscenesManager().play(new NexCutScene(NexCombat.NO_ESCAPE_TELEPORTS[1], 1));
                return true;
            case "coords":
                StringSelection selection = new StringSelection(player.getX() + " " + player.getY() + " " + player.getPlane());

                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
                player.getSocialManager().sendPanelBoxMessage("Coords: " + player.getX() + ", " + player.getY() + ", " + player.getPlane() + ", regionId: " + player.getRegionId() + ", rx: " + player.getChunkX() + ", ry: " + player.getChunkY() + ", int: " + player.getTileHash());
                return true;
            case "ccoords":
                selection = new StringSelection(player.getX() + ", " + player.getY() + ", " + player.getPlane());
                clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
                player.getSocialManager().sendPanelBoxMessage("Coords: " + player.getX() + ", " + player.getY() + ", " + player.getPlane() + ", regionId: " + player.getRegionId() + ", rx: " + player.getChunkX() + ", ry: " + player.getChunkY() + ", int: " + player.getTileHash());
                return true;
            case "itemoni":
                player.getPackets().sendItemOnIComponent(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]), Integer.valueOf(cmd[3]), 1);
                return true;

            case "save":
                Launcher.saveFiles();
                return true;

            case "items":
                for (int i = 0; i < 2000; i++) {
                    player.getPackets().sendItems(i, new Item[] { new Item(i, 1) });
                }
                return true;

            case "trade":

                name = "";
                for (int i = 1; i < cmd.length; i++)
                    name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

                target = World.getPlayerByDisplayName(name);
                if (target != null) {
                    player.getTrade().openTrade(target);
                    target.getTrade().openTrade(player);
                }
                return true;

            case "setlevel":
                if (cmd.length < 3) {
                    player.getSocialManager().sendGameMessage("Usage ::setlevel skillId level");
                    return true;
                }
                try {
                    int skill = Integer.parseInt(cmd[1]);
                    int level = Integer.parseInt(cmd[2]);
                    if (level < 0 || level > 99) {
                        player.getSocialManager().sendGameMessage("Please choose a valid level.");
                        return true;
                    }
                    player.getSkills().set(skill, level);
                    player.getSkills().setXp(skill, Skills.getXPForLevel(level));
                    player.getAppearence().generateAppearenceData();
                    return true;
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendGameMessage("Usage ::setlevel skillId level");
                }
                return true;

            case "npc":
                try {
                    NPC n = World.spawnNPC(Integer.parseInt(cmd[1]), player, -1, true, true);
                    n.setHitpoints(20000);
                    return true;
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::npc id(Integer)");
                }
                return true;

            case "loadwalls":
                WallHandler.loadWall(player.getCurrentFriendChat().getClanWars());
                return true;

            case "cwbase":
                ClanWars cw = player.getCurrentFriendChat().getClanWars();
                WorldTile base = cw.getBaseLocation();
                player.getSocialManager().sendGameMessage("Base x=" + base.getX() + ", base y=" + base.getY());
                base = cw.getBaseLocation().transform(cw.getAreaType().getNorthEastTile().getX() - cw.getAreaType().getSouthWestTile().getX(), cw.getAreaType().getNorthEastTile().getY() - cw.getAreaType().getSouthWestTile().getY(), 0);
                player.getSocialManager().sendGameMessage("Offset x=" + base.getX() + ", offset y=" + base.getY());
                return true;

            case "object":
                try {
                    int type = cmd.length > 2 ? Integer.parseInt(cmd[2]) : 10;
                    int rotation = cmd.length > 3 ? Integer.parseInt(cmd[3]) : 0;
                    if (type > 22 || type < 0) {
                        type = 10;
                    }
                    World.spawnObject(new WorldObject(Integer.valueOf(cmd[1]), type, rotation, player.getX(), player.getY(), player.getPlane()));
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: setkills id");
                }
                return true;

            case "tab":
                try {
                    player.getInterfaceManager().setWindowInterface(Integer.valueOf(cmd[2]), Integer.valueOf(cmd[1]));
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: tab id inter");
                }
                return true;

            case "killme":
                player.applyHit(new Hit(player, 2000, Hit.HitLook.REGULAR_DAMAGE));
                return true;
            case "hidec":
                if (cmd.length < 4) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::hidec interfaceid componentId hidden");
                    return true;
                }
                try {
                    player.getPackets().sendHideIComponent(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]), Boolean.valueOf(cmd[3]));
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::hidec interfaceid componentId hidden");
                }
                return true;

            case "string":
                try {
                    player.getInterfaceManager().sendInterface(Integer.valueOf(cmd[1]));
                    for (int i = 0; i <= Integer.valueOf(cmd[2]); i++)
                        player.getPackets().sendIComponentText(Integer.valueOf(cmd[1]), i, "child: " + i);
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: string inter childid");
                }
                return true;

            case "istringl":
                if (cmd.length < 2) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                    return true;
                }

                try {
                    for (int i = 0; i < Integer.valueOf(cmd[1]); i++) {
                        player.getPackets().sendGlobalString(i, "String " + i);
                    }
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                }
                return true;

            case "istring":
                if (cmd.length < 2) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                    return true;
                }
                try {
                    player.getPackets().sendGlobalString(Integer.valueOf(cmd[1]), "String " + Integer.valueOf(cmd[2]));
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: String id value");
                }
                return true;

            case "iconfig":
                if (cmd.length < 2) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                    return true;
                }
                try {
                    for (int i = 0; i < Integer.valueOf(cmd[1]); i++) {
                        player.getPackets().sendGlobalConfig(Integer.parseInt(cmd[2]), i);
                    }
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                }
                return true;

            case "config":
                if (cmd.length < 3) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                    return true;
                }
                try {
                    player.getVarsManager().sendVar(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]));
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                }
                return true;
            case "forcemovement":
                WorldTile toTile = player.transform(0, 5, 0);
                player.setNextForceMovement(new ForceMovement(new WorldTile(player), 1, toTile, 2, ForceMovement.NORTH));

                return true;
            case "configf":
                if (cmd.length < 3) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                    return true;
                }
                try {
                    player.getVarsManager().sendVarBit(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]));
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                }
                return true;

            case "hit":
                player.applyHit(new Hit(player, Integer.valueOf(cmd[1]), Hit.HitLook.REGULAR_DAMAGE));
                return true;

            case "heal":
                int amount = cmd.length > 1 ? Integer.parseInt(cmd[1]) : player.getMaxHitpoints();
                player.heal(amount);
                player.getSocialManager().sendPanelBoxMessage("Restored " + amount + " hitpoints!");
                return true;

            case "iloop":
                if (cmd.length < 3) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                    return true;
                }
                try {
                    for (int i = Integer.valueOf(cmd[1]); i < Integer.valueOf(cmd[2]); i++)
                        player.getInterfaceManager().sendInterface(i);
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                }
                return true;

            case "tloop":
                if (cmd.length < 3) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                    return true;
                }
                try {
                    for (int i = Integer.valueOf(cmd[1]); i < Integer.valueOf(cmd[2]); i++)
                        player.getInterfaceManager().setWindowInterface(i, Integer.valueOf(cmd[3]));
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                }
                return true;
            case "hloop":
                if (cmd.length < 5) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                    return true;
                }
                try {
                    for (int i = Integer.valueOf(cmd[2]); i < Integer.valueOf(cmd[3]); i++) {
                        player.getPackets().sendHideIComponent(Integer.valueOf(cmd[1]), i, Boolean.valueOf(cmd[4]));
                    }
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                }
                return true;
            case "configloop":
                if (cmd.length < 3) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                    return true;
                }
                try {
                    for (int i = Integer.valueOf(cmd[1]); i < Integer.valueOf(cmd[2]); i++) {
                        if (i >= 2633) {
                            break;
                        }
                        player.getVarsManager().sendVar(i, Integer.valueOf(cmd[3]));
                    }
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                }
                return true;
            case "configfloop":
                if (cmd.length < 3) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                    return true;
                }
                try {
                    for (int i = Integer.valueOf(cmd[1]); i < Integer.valueOf(cmd[2]); i++)
                        player.getVarsManager().sendVarBit(i, Integer.valueOf(cmd[3]));
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                }
                return true;
            case "objectanim":

                object = cmd.length == 4 ? World.getStandartObject(new WorldTile(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]), player.getPlane())) : World.getObjectWithType(new WorldTile(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]), player.getPlane()), Integer.parseInt(cmd[3]));
                if (object == null) {
                    player.getSocialManager().sendPanelBoxMessage("No object was found.");
                    return true;
                }
                player.getPackets().sendObjectAnimation(object, new Animation(Integer.parseInt(cmd[cmd.length == 4 ? 3 : 4])));
                return true;
            case "loopoanim":
                int x = Integer.parseInt(cmd[1]);
                int y = Integer.parseInt(cmd[2]);
                final WorldObject object1 = World.getObjectWithSlot(player, Region.OBJECT_SLOT_FLOOR);
                if (object1 == null) {
                    player.getSocialManager().sendPanelBoxMessage("Could not find object at [x=" + x + ", y=" + y + ", z=" + player.getPlane() + "].");
                    return true;
                }
                System.out.println("Object found: " + object1.getId());
                final int start = cmd.length > 3 ? Integer.parseInt(cmd[3]) : 10;
                final int end = cmd.length > 4 ? Integer.parseInt(cmd[4]) : 20000;
                CoresManager.fastExecutor.scheduleAtFixedRate(new TimerTask() {
                    int current = start;

                    @Override
                    public void run() {
                        while (AnimationDefinitions.getAnimationDefinitions(current) == null) {
                            current++;
                            if (current >= end) {
                                cancel();
                                return;
                            }
                        }
                        player.getSocialManager().sendPanelBoxMessage("Current object animation: " + current + ".");
                        player.getPackets().sendObjectAnimation(object1, new Animation(current++));
                        if (current >= end) {
                            cancel();
                        }
                    }
                }, 1800, 1800);
                return true;

            case "unmuteall":
                for (Player targets : World.getPlayers()) {
                    if (player == null)
                        continue;
                    targets.setMuted(0);
                }
                return true;

            case "bconfigloop":
                if (cmd.length < 3) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                    return true;
                }
                try {
                    for (int i = Integer.valueOf(cmd[1]); i < Integer.valueOf(cmd[2]); i++) {
                        if (i >= 1929) {
                            break;
                        }
                        player.getPackets().sendGlobalConfig(i, Integer.valueOf(cmd[3]));
                    }
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: config id value");
                }
                return true;

            case "reset":
                if (cmd.length < 2) {
                    for (int skill = 0; skill < 25; skill++)
                        player.getSkills().setXp(skill, 0);
                    player.getSkills().init();
                    return true;
                }
                try {
                    player.getSkills().setXp(Integer.valueOf(cmd[1]), 0);
                    player.getSkills().set(Integer.valueOf(cmd[1]), 1);
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::master skill");
                }
                return true;
            case "build":
                player.getVarsManager().sendVar(483, 1024);
                player.getVarsManager().sendVar(483, 1025);
                player.getVarsManager().sendVar(483, 1026);
                player.getVarsManager().sendVar(483, 1027);
                player.getVarsManager().sendVar(483, 1028);
                player.getVarsManager().sendVar(483, 1029);
                player.getVarsManager().sendVar(483, 1030);
                player.getVarsManager().sendVar(483, 1031);
                player.getVarsManager().sendVar(483, 1032);
                player.getVarsManager().sendVar(483, 1033);
                player.getVarsManager().sendVar(483, 1034);
                player.getVarsManager().sendVar(483, 1035);
                player.getVarsManager().sendVar(483, 1036);
                player.getVarsManager().sendVar(483, 1037);
                player.getVarsManager().sendVar(483, 1038);
                player.getVarsManager().sendVar(483, 1039);
                player.getVarsManager().sendVar(483, 1040);
                player.getVarsManager().sendVar(483, 1041);
                player.getVarsManager().sendVar(483, 1042);
                player.getVarsManager().sendVar(483, 1043);
                player.getVarsManager().sendVar(483, 1044);
                player.getVarsManager().sendVar(483, 1045);
                player.getVarsManager().sendVar(483, 1024);
                player.getVarsManager().sendVar(483, 1027);
                player.getPackets().sendGlobalConfig(841, 0);
                player.getPackets().sendGlobalConfig(199, -1);
                player.getPackets().sendIComponentSettings(1306, 55, -1, -1, 0);
                player.getPackets().sendIComponentSettings(1306, 8, 4, 4, 1);
                player.getPackets().sendIComponentSettings(1306, 15, 4, 4, 1);
                player.getPackets().sendIComponentSettings(1306, 22, 4, 4, 1);
                player.getPackets().sendIComponentSettings(1306, 29, 4, 4, 1);
                player.getPackets().sendIComponentSettings(1306, 36, 4, 4, 1);
                player.getPackets().sendIComponentSettings(1306, 43, 4, 4, 1);
                player.getPackets().sendIComponentSettings(1306, 50, 4, 4, 1);
                System.out.println("Build");
                return true;
            case "level":
                player.getSkills().addXp(Integer.valueOf(cmd[1]), Skills.getXPForLevel(Integer.valueOf(cmd[2])));
                return true;

            case "master":
                if (cmd.length < 2) {
                    for (int skill = 0; skill < Skills.SKILL_NAME.length; skill++)
                        player.getSkills().addXp(skill, 150000000);
                    return true;
                }
                try {
                    player.getSkills().addXp(Integer.valueOf(cmd[1]), 150000000);
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::master skill");
                }
                return true;

            case "window":
                player.getInterfaceManager().setRootInterface(Integer.parseInt(cmd[1]), false);
                return true;
            case "bconfig":
                if (cmd.length < 3) {
                    player.getSocialManager().sendPanelBoxMessage("Use: bconfig id value");
                    return true;
                }
                try {
                    player.getPackets().sendGlobalConfig(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]));
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: bconfig id value");
                }
                return true;

            case "tonpc":
                if (cmd.length < 2) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::tonpc id(-1 for player)");
                    return true;
                }
                try {
                    player.getAppearence().transformIntoNPC(Integer.valueOf(cmd[1]));
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::tonpc id(-1 for player)");
                }
                return true;

            case "inter":
                if (cmd.length < 2) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::inter interfaceId");
                    return true;
                }
                try {
                    if (Integer.valueOf(cmd[1]) > Utils.getInterfaceDefinitionsSize())
                        return true;
                    player.getInterfaceManager().sendInterface(Integer.valueOf(cmd[1]));
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::inter interfaceId");
                }
                return true;
            case "pane":
                if (cmd.length < 2) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::pane interfaceId");
                    return true;
                }
                try {
                    player.getPackets().sendRootInterface(Integer.valueOf(cmd[1]), 0);
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::pane interfaceId");
                }
                return true;
            case "overlay":
                if (cmd.length < 2) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::inter interfaceId");
                    return true;
                }
                int child = cmd.length > 2 ? Integer.parseInt(cmd[2]) : 28;
                try {
                    player.getInterfaceManager().setInterface(true, player.getInterfaceManager().hasRezizableScreen() ? 746 : 548, child, Integer.valueOf(cmd[1]));
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::inter interfaceId");
                }
                return true;

            case "resetprices":
                player.getSocialManager().sendGameMessage("Starting!");
                GrandExchange.reset(true, false);
                player.getSocialManager().sendGameMessage("Done!");
                return true;
            case "recalcprices":
                player.getSocialManager().sendGameMessage("Starting!");
                GrandExchange.recalcPrices();
                player.getSocialManager().sendGameMessage("Done!");
                return true;

            case "interh":
                if (cmd.length < 2) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::inter interfaceId");
                    return true;
                }

                try {
                    int interId = Integer.valueOf(cmd[1]);
                    for (int componentId = 0; componentId < Utils.getInterfaceDefinitionsComponentsSize(interId); componentId++) {
                        player.getPackets().sendHideIComponent(interId, componentId, false);
                    }
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::inter interfaceId");
                }
                return true;

            case "inters":
                if (cmd.length < 2) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::inter interfaceId");
                    return true;
                }

                try {
                    int interId = Integer.valueOf(cmd[1]);
                    for (int componentId = 0; componentId < Utils.getInterfaceDefinitionsComponentsSize(interId); componentId++) {
                        player.getPackets().sendIComponentText(interId, componentId, "cid: " + componentId);
                    }
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::inter interfaceId");
                }
                return true;

            case "kill":
                name = "";
                for (int i = 1; i < cmd.length; i++)
                    name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
                target = World.getPlayerByDisplayName(name);
                if (target == null)
                    return true;
                target.applyHit(new Hit(target, player.getHitpoints(), Hit.HitLook.REGULAR_DAMAGE));
                target.stopAll();
                return true;

            case "killall":
                if (Settings.ECONOMY || Settings.ECONOMY_TEST) {
                    player.getSocialManager().sendGameMessage("What are you doing?!?!");
                    return true;
                }
                for (Player loop : World.getPlayers()) {
                    loop.applyHit(new Hit(loop, player.getHitpoints(), Hit.HitLook.REGULAR_DAMAGE));
                    loop.stopAll();
                }
                return true;
            case "bank":
                player.getBank().openBank();
                return true;
            case "reloadfiles":
                IPBanL.init();
                IPMuteL.init();
                PkRank.init();
                return true;

            case "tele":
                if (cmd.length < 3) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::tele coordX coordY");
                    return true;
                }
                try {
                    player.resetWalkSteps();
                    player.setNextWorldTile(new WorldTile(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]), cmd.length >= 4 ? Integer.valueOf(cmd[3]) : player.getPlane()));
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::tele coordX coordY plane");
                }
                return true;

            case "shutdown":
                int delay = 60;
                if (cmd.length >= 2) {
                    try {
                        delay = Integer.valueOf(cmd[1]);
                    } catch (NumberFormatException e) {
                        player.getSocialManager().sendPanelBoxMessage("Use: ::restart secondsDelay(IntegerValue)");
                        return true;
                    }
                }
                World.safeShutdown(false, delay);
                return true;

            case "blackout":
                player.getPackets().sendBlackOut(5);
                return true;

            case "emote":
                if (cmd.length < 2) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::emote id");
                    return true;
                }
                try {
                    player.setNextAnimation(new Animation(Integer.valueOf(cmd[1])));
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::emote id");
                }
                return true;

            case "remote":
                if (cmd.length < 2) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::emote id");
                    return true;
                }
                try {
                    player.getAppearence().setRenderEmote(Integer.valueOf(cmd[1]));
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::emote id");
                }
                return true;

            case "quake":
                player.getPackets().sendCameraShake(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]), Integer.valueOf(cmd[3]), Integer.valueOf(cmd[4]), Integer.valueOf(cmd[5]), Integer.valueOf(cmd[6]));
                return true;

            case "getrender":
                player.getSocialManager().sendGameMessage("Testing renders");
                for (int i = 0; i < 3000; i++) {
                    try {
                        player.getAppearence().setRenderEmote(i);
                        player.getSocialManager().sendGameMessage("Testing " + i);
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return true;

            case "spec":
                player.getCombatDefinitions().resetSpecialAttack();
                return true;

            case "trylook":
                final int look = Integer.parseInt(cmd[1]);
                WorldTasksManager.schedule(new WorldTask() {
                    int i = 269;// 200

                    @Override
                    public void run() {
                        if (player.hasFinished()) {
                            stop();
                        }
                        player.getAppearence().setLook(look, i);
                        player.getAppearence().generateAppearenceData();
                        player.getSocialManager().sendGameMessage("Look " + i + ".");
                        i++;
                    }
                }, 0, 1);
                return true;

            case "tryinter":
                WorldTasksManager.schedule(new WorldTask() {
                    int i = 1;

                    @Override
                    public void run() {
                        if (player.hasFinished()) {
                            stop();
                        }
                        player.getInterfaceManager().sendInterface(i);
                        System.out.println("Inter - " + i);
                        i++;
                    }
                }, 0, 1);
                return true;

            case "tryanim":

                // 11155

                WorldTasksManager.schedule(new WorldTask() {
                    int i = 10223;

                    @Override
                    public void run() {
                        if (i >= Utils.getAnimationDefinitionsSize()) {
                            stop();
                            return;
                        }
                        if (player.getLastAnimationEnd() > Utils.currentTimeMillis()) {
                            player.setNextAnimation(new Animation(-1));
                        }
                        if (player.hasFinished()) {
                            stop();
                        }
                        player.setNextAnimation(new Animation(i));
                        System.out.println("Anim - " + i);
                        i++;
                    }
                }, 0, 2);

                WorldTasksManager.schedule(new WorldTask() {

                    @Override
                    public void run() {
                        player.setNextAnimation(new Animation(-1));
                        System.out.println("stop");

                        if (player.hasFinished()) {
                            stop();
                        }

                    }
                }, 1, 2);

                return true;

            case "animcount":
                System.out.println(Utils.getAnimationDefinitionsSize() + " anims.");
                return true;

            case "trygfx":
                WorldTasksManager.schedule(new WorldTask() {
                    int i = 1400;

                    @Override
                    public void run() {
                        if (i >= Utils.getGraphicDefinitionsSize()) {
                            stop();
                        }
                        if (player.hasFinished()) {
                            stop();
                        }
                        player.setNextGraphics(new Graphics(i));
                        System.out.println("GFX - " + i);
                        i++;
                    }
                }, 0, 3);
                return true;

            case "gfx":
                if (cmd.length < 2) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::gfx id");
                    return true;
                }
                try {
                    player.setNextGraphics(new Graphics(Integer.valueOf(cmd[1]), 0, 0));
                } catch (NumberFormatException e) {
                    player.getSocialManager().sendPanelBoxMessage("Use: ::gfx id");
                }
                return true;

            case "ci":
                player.getInventory().reset();
                return true;

            case "sync":
                int animId = Integer.parseInt(cmd[1]);
                int gfxId = Integer.parseInt(cmd[2]);
                int height = cmd.length > 3 ? Integer.parseInt(cmd[3]) : 0;
                player.setNextAnimation(new Animation(animId));
                player.setNextGraphics(new Graphics(gfxId, 0, height));
                return true;
            case "staffmeeting":
                for (Player staff : World.getPlayers()) {
                    if (staff.getRights() == 0)
                        continue;
                    staff.setNextWorldTile(new WorldTile(2675, 10418, 0));
                    staff.getSocialManager().sendGameMessage("You been teleported for a staff meeting by " + player.getDisplayName());
                }
                return true;
            }
        }
        return false;
    }

    public static void performTeleEmote(Player target) {
        target.setNextAnimation(new Animation(17544));
        target.setNextGraphics(new Graphics(3403));
    }
}
