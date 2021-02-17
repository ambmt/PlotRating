package com.ambmt.plots;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OwnerList {
    private static List<String> offlineOwners;

    public OwnerList() {
       List<Player> offlineOwners = new ArrayList<>();
    }

    public static List<String> getList() {
        return offlineOwners;
    }
}