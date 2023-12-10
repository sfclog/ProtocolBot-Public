package me.sfclog.protocolbot.control;

import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.packetlib.ProxyInfo;
import me.sfclog.protocolbot.Main;
import me.sfclog.protocolbot.bot.Bot;
import me.sfclog.protocolbot.proxy.ProxyLoad;
import me.sfclog.protocolbot.utils.RandomUtils;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;


public class BotControlThread  {

    private static Timer timer = new Timer();

    public static HashMap<String,Bot> map = new HashMap<>();

    public static void start() {
        //ProxyLoad.get_and_load();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                map.values().forEach(Bot::fallDown);
            }
        }, 1000L, 500L);
        while (true) {
                //ProxyInfo info = new ProxyInfo(ProxyInfo.Type.SOCKS4, ProxyLoad.getRandom());
                String name = RandomUtils.getRandomNumber(0, 50) + "_ProtocolEvil_" + RandomUtils.getRandomNumber(0, 999);
                Bot bot = new Bot(name, new InetSocketAddress("103.67.197.8", 4015), null);
                bot.start();
                bot.register_listen();
                map.put(name, bot);

            //chat();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }



    public static void chat() {
        map.values().forEach(a -> {
            a.sendChat("Hello world (" + RandomUtils.getRandomNumber(0,99999999) + ")");
        });
    }
}
