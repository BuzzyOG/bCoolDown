package com.beecub.bCoolDown;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class bWarmUpManager {
    
    private static HashMap<String, Integer> playercommands = new HashMap<String, Integer>();

    public static void startWarmUp(bCoolDown bCoolDown, Player player, String pre, String message, int warmUpSeconds) {
        if(!isWarmUpProcess(player, pre, message)) {
            bCoolDownManager.removeWarmUpOK(player, pre, message);
            String msg = bConfigManager.getWarmUpMessage();
            msg = msg.replaceAll("&command&", pre);
            msg = msg.replaceAll("&seconds&", Long.toString(warmUpSeconds));
            bChat.sendMessageToPlayer(player, msg);
            int taskIndex = bCoolDown.getServer().getScheduler().scheduleSyncDelayedTask(bCoolDown, new WarmTask(player, pre, message), warmUpSeconds * 20);
            playercommands.put(player.getName() + pre, Integer.valueOf(taskIndex));
        }
        else {
            String msg = bConfigManager.getWarmUpAlreadyStartedMessage();
            msg = msg.replaceAll("&command&", pre);
            bChat.sendMessageToPlayer(player, msg);
        }
    }
    
    public static boolean isWarmUpProcess(Player player, String pre, String message) {
        if (playercommands.containsKey(player.getName() + pre)) {
            return true;
        }
        return false;
    }
    
    private static class WarmTask implements Runnable {
        private Player player;
        private String pre;
        private String message;

        public WarmTask(Player player, String pre, String message) {
            this.player = player;
            this.pre = pre;
            this.message = message;
        }

        public void run()
        {
            if(player.isOnline()) {
                bCoolDownManager.setWarmUpOK(player, pre, message);
                bWarmUpManager.playercommands.remove(this.player.getName() + pre);
                player.chat(pre + message);
            }
        }
    }
}
