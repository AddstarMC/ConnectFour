package ch.dragon252525.connectFour.listeners;

import ch.dragon252525.connectFour.ConnectFour;
import ch.dragon252525.connectFour.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.Listener;

import java.util.Map;

/**
 * Created for the AddstarMC.
 * Created by Narimm on 6/12/2016.
 */
public class PlayerEvents implements Listener{
    
    private final ConnectFour instance;
    
    public PlayerEvents(ConnectFour plugin){
        this.instance = plugin;
    }

    @EventHandler(priority= EventPriority.LOWEST)
    public void onDropItem(PlayerDropItemEvent event)
    {
        Player pe = event.getPlayer();
        if ((instance.gameStarted != null) &&
                (instance.playerIsInGame.containsKey(pe.getName().toLowerCase())))
        {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onCmd(PlayerCommandPreprocessEvent event)
    {
        Player pe = event.getPlayer();
        if ((instance.gameStarted != null) &&
                (instance.playerIsInGame.containsKey(pe.getName().toLowerCase())) &&
                (!event.getMessage().startsWith("/login")) && (!event.getMessage().startsWith("/cfour leave")) && (!event.getMessage().startsWith("/c4 leave")))
        {
            event.setCancelled(true);
            Utilities.msg(pe, instance.getMessages().getMessage("blockCmd", ChatColor.ITALIC + "/cfour leave" + ChatColor.RESET + ChatColor.YELLOW));
            return;
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPickUpItem(PlayerPickupItemEvent event)
    {
        Player pe = event.getPlayer();
        if ((instance.gameStarted != null) &&
                (instance.playerIsInGame.containsKey(pe.getName().toLowerCase())))
        {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if ((instance.enableJoinSigns) &&
                (event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                ((event.getClickedBlock().getState() instanceof Sign)))
        {
            Sign sign = (Sign)event.getClickedBlock().getState();
            if ((sign.getLine(0).equals(ChatColor.BOLD + "ConnectFour")) &&
                    (sign.getLine(1).startsWith(ChatColor.DARK_BLUE.toString())) &&
                    (sign.getLine(2).startsWith(ChatColor.GOLD +""+ ChatColor.BOLD)) &&
                    (sign.getLine(3).startsWith(ChatColor.GREEN.toString())))
            {
                event.setCancelled(true);
                Player p = event.getPlayer();
                String game = ChatColor.stripColor(sign.getLine(2));
                instance.tryToJoinGame(p, game);
            }
        }
    }

    @EventHandler
    public void onSignCreate(SignChangeEvent event)
    {
        if ((instance.enableJoinSigns) &&
                (event.getLine(0).equalsIgnoreCase("[cfour]")))
        {
            Player p = event.getPlayer();
            String game = event.getLine(1);
            if (!instance.list.contains(game.toLowerCase()))
            {
                Utilities.msg(p, instance.getMessages().getMessage("game_notExists"));
                event.setCancelled(true);
                return;
            }
            event.setLine(0, ChatColor.BOLD + "ConnectFour");
            event.setLine(1, ChatColor.DARK_BLUE + instance.getMessages().getMessage("sign1"));
            event.setLine(2, ChatColor.GOLD +""+ ChatColor.BOLD + game);
            event.setLine(3, ChatColor.GREEN + instance.getMessages().getMessage("sign2"));
            Utilities.msg(p, instance.getMessages().getMessage("signCreated"));
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        onPlayerDisconnect(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent event)
    {
        onPlayerDisconnect(event.getPlayer());
    }

    private void onPlayerDisconnect(Player p)
    {
        if (instance.playerIsInGame.containsKey(p.getName().toLowerCase()))
        {
            String game = instance.playerIsInGame.get(p.getName().toLowerCase());
            if (instance.gameStarted.containsKey(game))
            {
                Utilities.msg(p, instance.getMessages().getMessage("left_self"));
                if (((String)((Map)instance.playersInGame.get(game)).get(1)).equalsIgnoreCase(p.getName()))
                {
                    instance.sendMsg(game, "p2", ConnectFour.prefix + instance.getMessages().getMessage("left_other", "{p1}"));
                    instance.sendMsg(game, "beide", ConnectFour.prefix + instance.getMessages().getMessage("game_stoped"));
                }
                if (((String)((Map)instance.playersInGame.get(game)).get(2)).equalsIgnoreCase(p.getName()))
                {
                    instance.sendMsg(game, "p1", ConnectFour.prefix + instance.getMessages().getMessage("left_other", "{p2}"));
                    instance.sendMsg(game, "beide", ConnectFour.prefix + instance.getMessages().getMessage("game_stoped"));
                }
                instance.stopGame(game);
                return;
            }
            Utilities.msg(p, instance.getMessages().getMessage("left_self"));
            instance.playerIsInGame.remove(p.getName().toLowerCase());
            instance.playersInGame.remove(game);

            return;
        }
    }
}
