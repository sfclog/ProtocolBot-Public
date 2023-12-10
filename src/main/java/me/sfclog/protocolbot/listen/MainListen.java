package me.sfclog.protocolbot.listen;


import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.inventory.ClickItemAction;
import com.github.steveice10.mc.protocol.data.game.inventory.ContainerAction;
import com.github.steveice10.mc.protocol.data.game.inventory.ContainerActionType;
import com.github.steveice10.mc.protocol.data.game.inventory.DropItemAction;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.inventory.ClientboundContainerClosePacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.inventory.ClientboundContainerSetContentPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.inventory.ClientboundContainerSetSlotPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.inventory.ClientboundOpenScreenPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.inventory.ServerboundContainerButtonClickPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.inventory.ServerboundContainerClickPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.*;
import com.github.steveice10.packetlib.packet.Packet;
import me.sfclog.protocolbot.Main;
import me.sfclog.protocolbot.bot.Bot;
import me.sfclog.protocolbot.inventory.BotContainer;


public class MainListen implements SessionListener {

    public Bot bot;

    public MainListen(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void packetReceived(Session session, Packet packet) {
        if(packet instanceof ClientboundContainerSetSlotPacket) {
            ClientboundContainerSetSlotPacket c = (ClientboundContainerSetSlotPacket) packet;

            Main.sendlog(""+c.getContainerId());

            if (c.getContainerId() != 0) {
                Main.sendlog("not null !");
                if (bot.container == null) {
                    bot.container = new BotContainer(c.getContainerId(), c.getStateId());
                    bot.container.add(c.getSlot(), c.getItem());
                    Main.sendlog("add " + c.getSlot());

                } else {
                    bot.container.add(c.getSlot(), c.getItem());

                    Main.sendlog("add " + c.getSlot());
                }
            } else {
                Main.sendlog("null !");
            }
        }
        if(packet instanceof ClientboundContainerSetContentPacket) {
            ClientboundContainerSetContentPacket c = (ClientboundContainerSetContentPacket) packet;
            if(c.getContainerId() != 0 && bot.container != null) {
                ServerboundContainerClickPacket click = new ServerboundContainerClickPacket(
                        c.getContainerId(),
                        c.getStateId(),
                        12,
                        ContainerActionType.CLICK_ITEM,
                        ClickItemAction.LEFT_CLICK,
                        c.getCarriedItem(),
                        bot.container.getItems()
                );
                bot.client.send(click);
            }

        }
        if(packet instanceof ClientboundContainerClosePacket) {
            if(bot.container != null) {
                bot.container = null;
            }
        }
    }

    @Override
    public void packetSending(PacketSendingEvent packetSendingEvent) {

    }

    @Override
    public void packetSent(Session session, Packet packet) {

    }

    @Override
    public void packetError(PacketErrorEvent packetErrorEvent) {

    }

    @Override
    public void connected(ConnectedEvent connectedEvent) {

    }

    @Override
    public void disconnecting(DisconnectingEvent disconnectingEvent) {

    }

    @Override
    public void disconnected(DisconnectedEvent disconnectedEvent) {

    }
}
