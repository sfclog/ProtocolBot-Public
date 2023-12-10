package me.sfclog.protocolbot;

import me.sfclog.protocolbot.control.BotControlThread;

public final class Main  {


    public static void main(String[] s) {


        BotControlThread.start();

    }

    public static void sendlog(String log) {

        System.out.println(log + "\n");
    }

}
