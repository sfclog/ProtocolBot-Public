package me.sfclog.protocolbot.inventory;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BotContainer {

    public int id,state;
    public Map<Integer, ItemStack> map;
    public ItemStack item;

    public BotContainer(int id,int state) {
        this.id = id;
        this.state = state;
        this.map = new HashMap<>();
        this.item = null;
    }
    public int getID() {
        return this.id;
    }
    public int getStateID() {
        return this.state;
    }

    public void add(Integer slot, ItemStack item) {
        this.map.put(slot,item);
    }

    public Map<Integer, ItemStack> getItems() {
        return this.map;
    }

    public ItemStack getCarriedItem() {
        return this.item;
    }
    public void setCarriedItem(ItemStack carriedItem) {
        this.item = carriedItem;
    }

}
