package com.rs.game.player.content

import com.rs.game.WorldTile
import com.rs.game.item.Item
import com.rs.game.player.Player
import com.rs.game.player.content.Summoning.Pouch
import com.rs.network.DummyChannel
import com.rs.network.protocol.packet.impl.ButtonHandler
import com.rs.utils.IsaacKeyPair
import com.rs.utils.MachineInformation

object MoreCommands {

    fun handle(player: Player, cmd: Array<String>, console: Boolean, clientCommand: Boolean): Boolean {
        when (cmd[0].toLowerCase()) {
            "gear" -> {
                Commands.processCommand(player, "sets", true, false)
                return true
            }
            "binu" -> {
                player.bank.depositAllEquipment(false)
                player.bank.depositAllInventory(false)
                val inv = arrayListOf<Item>(
                        Item(11732), Item(4151), Item(11283), Item(12681),
                        Item(10551), Item(11726), Item(6570), Item(6585),
                        Item(15220), Item(7462)
                )
                inv.forEach {
                    player.inventory.addItem(it)
                    ButtonHandler.sendWear(player, 0, it.getId())
                }
                if (player.getFamiliar() == null) {
                    player.inventory.addItem(Item(12093))
                    val pouch = Pouch.forId(12093)
                    Summoning.spawnFamiliar(player, pouch)
                }
                ButtonHandler.refreshEquipBonuses(player)
                val inv2 = arrayListOf<Item>(
                        Item(14484), Item(4712), Item(4714), Item(2412),
                        Item(13867), Item(13740), // zuriel staff, divine
                        Item(6889), Item(13738), // mage book, arcane
                        Item(6685, 7), Item(3024, 4), Item(2436), Item(2440),
                        Item(15272, 2), // rocktail

                )
                inv2.forEach {
                    player.inventory.addItem(it)
                }
                Commands.processCommand(player, "barrage", true, false)
            }
            "testbot" -> {
                val bot = Player("test")
                bot.init(DummyChannel, "testbot", 0, 700,
                        700,
                        MachineInformation(0, false, 0,
                                0, 0, 0,
                                0, false, 0,
                                0, 0, 0,
                                0, 0, 0),
                        IsaacKeyPair(intArrayOf(0, 0, 0, 0)))
                bot.rights = 2
                bot.start()
                Commands.processCommand(bot, "master", true, false)
                bot.nextWorldTile = WorldTile(player.x, player.y, player.plane)
                bot.controlerManager.startControler("Wilderness")
                bot.hitpoints = Short.MAX_VALUE.toInt()
                bot.equipment.equipmentHpIncrease = Short.MAX_VALUE - 990
                return true
            }
            "wild" -> {
                player.controlerManager.startControler("Wilderness")
                return true
            }
        }
        return false
    }
}