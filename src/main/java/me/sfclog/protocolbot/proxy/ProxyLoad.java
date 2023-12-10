package me.sfclog.protocolbot.proxy;

import me.sfclog.protocolbot.Main;
import me.sfclog.protocolbot.okproxy.OkProxy;
import me.sfclog.protocolbot.utils.Color;
import me.sfclog.protocolbot.utils.RandomUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ProxyLoad {

    public static List<InetSocketAddress> proxy = new ArrayList<>();

    public static void get_and_load() {

        OkProxy.load();

        String[] uris = {
                "https://raw.githubusercontent.com/TheSpeedX/PROXY-List/master/socks4.txt",
                "https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/socks4.txt",
                "https://raw.githubusercontent.com/monosans/proxy-list/main/proxies/socks4.txt",
                "https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-socks4.txt"
        };

        for (String uri : uris) {
            try {
                URL url = new URL(uri);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(":");
                    proxy.add(new InetSocketAddress(data[0],Integer.parseInt(data[1])));
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Main.sendlog(Color.GREEN + " Load " + proxy.size() + " Proxy!");
    }

    public static InetSocketAddress getRandom() {
        return proxy.get(RandomUtils.getRandomNumber(0,proxy.size() -1));
    }

}
