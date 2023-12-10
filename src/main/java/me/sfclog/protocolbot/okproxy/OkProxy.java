package me.sfclog.protocolbot.okproxy;

import me.sfclog.protocolbot.proxy.ProxyLoad;

import java.io.*;
import java.net.InetSocketAddress;

public class OkProxy {

    public static File file = new File("proxyok.txt");

    public static void write_proxy(String proxy) {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(file,true));
            printWriter.println(proxy );
            printWriter.close();
        } catch (IOException e) {
            throw new RuntimeException("Error writing proxy to file", e);
        }
    }

    public static void load() {
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            // Read and print each line from the file
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(":");
                ProxyLoad.proxy.add(new InetSocketAddress(data[0],Integer.parseInt(data[1])));
            }
            // Close the BufferedReader and FileReader
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
