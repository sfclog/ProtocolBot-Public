package me.sfclog.protocolbot.bot;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.ClientCommand;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.inventory.ClickItemAction;
import com.github.steveice10.mc.protocol.data.game.inventory.ContainerAction;
import com.github.steveice10.mc.protocol.data.game.inventory.ContainerActionType;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerCombatKillPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundClientCommandPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.inventory.ServerboundContainerClickPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerPosPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.ServerboundSwingPacket;
import com.github.steveice10.packetlib.ProxyInfo;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.sfclog.protocolbot.Main;
import me.sfclog.protocolbot.inventory.BotContainer;
import me.sfclog.protocolbot.listen.MainListen;
import me.sfclog.protocolbot.okproxy.OkProxy;
import me.sfclog.protocolbot.utils.Color;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bot extends Thread {

    private String nickname;
    private ProxyInfo proxy;
    private InetSocketAddress address;
    public Session client;
    private MinecraftProtocol protocol;

    private boolean connected;

    private double lastX, lastY, lastZ = -1;

    //container
    public BotContainer container;


    public boolean is_switch;

    public Bot(String nickname, InetSocketAddress address, ProxyInfo proxy) {
       this.nickname = nickname;
       this.address = address;
       this.proxy = proxy;
       this.protocol = new MinecraftProtocol(nickname);
       this.client = new TcpClientSession(address.getHostString(), address.getPort(), protocol, proxy);
       this.container = null;
       is_switch = false;

    }

    @Override
    public void run() {
        //packet
        client.addListener(new SessionAdapter() {
            @Override
            public void packetReceived(Session session, Packet packet) {

                if (packet instanceof ClientboundLoginPacket && is_switch == false) {
                    Main.sendlog(Color.PURPLE + "[Bot] " + Color.GREEN + nickname + " join the game.");
                    connected = true;

                    if(proxy!= null) {
                        InetSocketAddress inetSocketAddress = (InetSocketAddress) proxy.getAddress();
                        String ipAddress = inetSocketAddress.getAddress().getHostAddress();
                        int port = inetSocketAddress.getPort();
                        OkProxy.write_proxy(ipAddress + ":" + port);
                    }
                    try {
                        Thread.sleep(1000);
                        //send command
                        sendChat("/register 123456987 123456987");
                        Main.sendlog(Color.GREEN +"Register success");
                        try {
                            Thread.sleep(1000);
                            //send command
                            sendChat("/login 123456987");
                            Main.sendlog(Color.GREEN +"Login success");
                            try {
                                Thread.sleep(1000);
//                                for(int i = 0; i < 10 ; i ++) {
//                                    sendChat("/laban");
//                                    Thread.sleep(100);
//                                    Main.sendlog(Color.GREEN + "send !");
//                                }
                                is_switch = true;
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                } else if (packet instanceof ClientboundPlayerPositionPacket) {
                ClientboundPlayerPositionPacket p = (ClientboundPlayerPositionPacket) packet;

                } else if (packet instanceof ClientboundPlayerCombatKillPacket) {
                    client.send(new ServerboundClientCommandPacket(ClientCommand.RESPAWN));
                }
            }

            @Override
            public void disconnected(DisconnectedEvent event) {
                     connected = false;
                    // Fix broken reason string by finding the content with regex
                    Pattern pattern = Pattern.compile("content=\"(.*?)\"");
                    Matcher matcher = pattern.matcher(String.valueOf(event.getReason()));

                    Main.sendlog(Color.PURPLE + "[Bot] " + Color.RED + nickname + " left the game. " + event.getReason());
                    Thread.currentThread().interrupt();

                    try {
                    Thread.sleep(1000);
                    start(); //reconnect
                     Main.sendlog(Color.PURPLE + "[Bot] " + Color.GREEN + nickname + " reconnect.");
                    } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                   }

            }

        });
        client.connect();

    }



    public void send_click_container(int slot) {
        if(container != null) {
            ServerboundContainerClickPacket click = new ServerboundContainerClickPacket(
                    container.getID(),
                    container.getStateID(),
                    slot,
                    ContainerActionType.CLICK_ITEM,
                    ClickItemAction.LEFT_CLICK,
                    container.getCarriedItem(),
                    container.getItems()
            );
            client.send(click);
        }
    }

    public void click(int id,int slot) {
       ServerboundContainerClickPacket click = new ServerboundContainerClickPacket(id,0,slot,ContainerActionType.CLICK_ITEM,null,null,new Int2ObjectOpenHashMap<>());
       client.send(click);
    }




    public void sendChat(String text) {
        if (isConnected()) {
            // timeStamp will provide when this message was sent by the user. If this value was not set or was set to 0,
            // The server console will print out that the message was "expired". To avoid this, set timeStamp as now.
            long timeStamp = Instant.now().toEpochMilli();

            // Send command
            if (text.startsWith("/")) {
                client.send(new ServerboundChatCommandPacket(
                        text.substring(1),
                        timeStamp,
                        0L,
                        Collections.emptyList(),
                        0,
                        new BitSet()
                ));
            } else {
                // Send chat message
                // From 1.19.1 or 1.19, the ServerboundChatPacket needs timestamp, salt and signed signature to generate packet.
                // tmpSignature will provide an empty byte array that can pretend it as signature.
                // salt is set 0 since this is offline server and no body will check it.

                client.send(new ServerboundChatPacket(
                        text,
                        Instant.now().toEpochMilli(),
                        0L,
                        null,
                        0,
                        new BitSet()
                ));
            }
        }
    }


    public void register_listen() {
        client.addListener(new MainListen(this));
    }
    public boolean isConnected() {
        return connected;
    }

    public void fallDown()
    {
        if (connected && lastY > 0) {
            move(0, -0.5, 0);
        }
    }

    public void move(double x, double y, double z)
    {
        lastX += x;
        lastY += y;
        lastZ += z;
        moveTo(lastX, lastY, lastZ);
    }

    public void moveTo(double x, double y, double z)
    {
        client.send(new ServerboundMovePlayerPosPacket(true, x, y, z));
    }


    public void sw_hand(Hand hand) {
       if(isConnected()) {
           client.send(new ServerboundSwingPacket(hand));
       }
    }
}

