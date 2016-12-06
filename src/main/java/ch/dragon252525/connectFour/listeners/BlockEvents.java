package ch.dragon252525.connectFour.listeners;

import ch.dragon252525.connectFour.ConnectFour;
import ch.dragon252525.connectFour.Utilities;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created for the AddstarMC
 * Created by Narimm on 6/12/2016.
 */
public class BlockEvents implements Listener {

    private final ConnectFour instance;

    public final Map<String, Boolean> isPlacingBlocked = new HashMap();
    private final Map<String, Integer> placedBlocks = new HashMap();

    public BlockEvents(ConnectFour plugin) {
        instance = plugin;
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlaceBlockCheck(BlockPlaceEvent event) {
        Block be = event.getBlock();
        if (instance.list != null) {
            for (String game : instance.list) {
                if (isBlockOnDisplay(game, be)) {
                    event.setCancelled(true);
                    return;
                }
                if (isBlockInEinwurf(be, game) != 0) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onSetBlock(BlockPlaceEvent event) {
        Player pe = event.getPlayer();
        Block b = event.getBlock();
        if ((instance.select.containsKey("throwIn")) &&
                (pe.getName().equalsIgnoreCase(instance.select.get("throwIn"))) &&
                (b.getTypeId() == 3)) {
            if ((instance.locs.containsKey("a2")) && (instance.locs.containsKey("a1"))) {
                Utilities.msg(pe, instance.getMessages().getMessage("create_error1"));
                Utilities.msg(pe, instance.getMessages().getMessage("create_error2"));
                event.setCancelled(true);
            } else if (instance.locs.containsKey("a1")) {
                if (((b.getLocation().getBlockX() == instance.locs.get("a1").getBlockX()) || (b.getLocation().getBlockZ() == instance.locs.get("a1").getBlockZ())) && (instance.locs.get("a1").getBlockY() == b.getLocation().getBlockY())) {
                    if ((difference(b.getLocation().getBlockX(), instance.locs.get("a1").getBlockX()) == 8) || (difference(b.getLocation().getBlockZ(), instance.locs.get("a1").getBlockZ()) == 8)) {
                        instance.locs.put("a2", b.getLocation());
                        Utilities.msg(pe, instance.getMessages().getMessage("create_saved_corner", "2"));
                        if ((instance.locs.containsKey("a2")) && (instance.locs.containsKey("a1")) && (instance.locs.containsKey("b2")) && (instance.locs.containsKey("b1"))) {
                            Utilities.msg(pe, instance.getMessages().getMessage("create_plz_save", ChatColor.ITALIC + "/cfour save" + ChatColor.RESET + ChatColor.YELLOW));
                        }
                    } else {
                        event.setCancelled(true);
                        Utilities.msg(pe, instance.getMessages().getMessage("create_error5"));
                    }
                } else {
                    event.setCancelled(true);
                    Utilities.msg(pe, instance.getMessages().getMessage("create_error3"));
                }
            } else if (instance.locs.containsKey("a2")) {
                if (((b.getLocation().getBlockX() == instance.locs.get("a2").getBlockX()) || (b.getLocation().getBlockZ() == instance.locs.get("a2").getBlockZ())) && (instance.locs.get("a2").getBlockY() == b.getLocation().getBlockY())) {
                    if ((difference(b.getLocation().getBlockX(), instance.locs.get("a2").getBlockX()) == 8) || (difference(b.getLocation().getBlockZ(), instance.locs.get("a2").getBlockZ()) == 8)) {
                        instance.locs.put("a1", b.getLocation());
                        Utilities.msg(pe, instance.getMessages().getMessage("create_saved_corner", "1"));
                        if ((instance.locs.containsKey("a2")) && (instance.locs.containsKey("a1")) && (instance.locs.containsKey("b2")) && (instance.locs.containsKey("b1"))) {
                            Utilities.msg(pe, instance.getMessages().getMessage("create_plz_save", ChatColor.ITALIC + "/cfour save" + ChatColor.RESET + ChatColor.YELLOW));
                        }
                    } else {
                        event.setCancelled(true);
                        Utilities.msg(pe, instance.getMessages().getMessage("create_error5"));
                    }
                } else {
                    event.setCancelled(true);
                    Utilities.msg(pe, instance.getMessages().getMessage("create_error3"));
                }
            } else {
                if (b.getTypeId() == 3) {
                }
                instance.locs.put("a1", b.getLocation());
                Utilities.msg(pe, instance.getMessages().getMessage("create_saved_corner", "1"));
            }
        }
        if ((instance.select.containsKey("display")) &&
                (pe.getName().equalsIgnoreCase(instance.select.get("display"))) &&
                (b.getTypeId() == 4)) {
            if ((instance.locs.containsKey("b2")) && (instance.locs.containsKey("b1"))) {
                Utilities.msg(pe, instance.getMessages().getMessage("create_error1"));
                Utilities.msg(pe, instance.getMessages().getMessage("create_error2"));
                event.setCancelled(true);
            } else if (instance.locs.containsKey("b1")) {
                if (((b.getLocation().getBlockX() == instance.locs.get("b1").getBlockX()) || (b.getLocation().getBlockZ() == instance.locs.get("b1").getBlockZ())) && (instance.locs.get("b1").getBlockY() == b.getLocation().getBlockY())) {
                    if ((difference(b.getLocation().getBlockX(), instance.locs.get("b1").getBlockX()) == 8) || (difference(b.getLocation().getBlockZ(), instance.locs.get("b1").getBlockZ()) == 8)) {
                        instance.locs.put("b2", b.getLocation());
                        Utilities.msg(pe, instance.getMessages().getMessage("create_saved_corner2", "2"));
                        if ((instance.locs.containsKey("a2")) && (instance.locs.containsKey("a1")) && (instance.locs.containsKey("b2")) && (instance.locs.containsKey("b1"))) {
                            Utilities.msg(pe, instance.getMessages().getMessage("create_plz_save", ChatColor.ITALIC + "/cfour save" + ChatColor.RESET + ChatColor.YELLOW));
                        }
                    } else {
                        event.setCancelled(true);
                        Utilities.msg(pe, instance.getMessages().getMessage("create_error5"));
                    }
                } else {
                    event.setCancelled(true);
                    Utilities.msg(pe, instance.getMessages().getMessage("create_error4"));
                }
            } else if (instance.locs.containsKey("b2")) {
                if (((b.getLocation().getBlockX() == instance.locs.get("b2").getBlockX()) || (b.getLocation().getBlockZ() == instance.locs.get("b2").getBlockZ())) && (instance.locs.get("b2").getBlockY() == b.getLocation().getBlockY())) {
                    if ((difference(b.getLocation().getBlockX(), instance.locs.get("b2").getBlockX()) == 8) || (difference(b.getLocation().getBlockZ(), instance.locs.get("b2").getBlockZ()) == 8)) {
                        instance.locs.put("b1", b.getLocation());
                        Utilities.msg(pe, instance.getMessages().getMessage("create_saved_corner2", "1"));
                        if ((instance.locs.containsKey("a2")) && (instance.locs.containsKey("a1")) && (instance.locs.containsKey("b2")) && (instance.locs.containsKey("b1"))) {
                            Utilities.msg(pe, instance.getMessages().getMessage("create_plz_save", ChatColor.ITALIC + "/cfour save" + ChatColor.RESET + ChatColor.YELLOW));
                        }
                    } else {
                        event.setCancelled(true);
                        Utilities.msg(pe, instance.getMessages().getMessage("create_error5"));
                    }
                } else {
                    event.setCancelled(true);
                    Utilities.msg(pe, instance.getMessages().getMessage("create_error4"));
                }
            } else {
                if (b.getTypeId() == 3) {
                }
                instance.locs.put("b1", b.getLocation());
                Utilities.msg(pe, instance.getMessages().getMessage("create_saved_corner2", "1"));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlaceBlock(BlockPlaceEvent event) {
        Player pe = event.getPlayer();
        Block be = event.getBlock();
        if ((instance.gameStarted != null) &&
                (instance.playerIsInGame.containsKey(pe.getName().toLowerCase()))) {
            String game = instance.playerIsInGame.get(pe.getName().toLowerCase());
            Player whoseTurn = instance.getServer().getPlayer(instance.whoseTurnN.get(game));
            int pos = isBlockInEinwurf(be, game);
            if (pos != 0) {
                if (whoseTurn == pe) {
                    event.setCancelled(true);
                    if (be.getType().name() != instance.playersMaterial.get(pe.getName().toLowerCase())) {
                        event.setCancelled(true);
                        Utilities.msg(pe, instance.getMessages().getMessage("play_falseBlock"));
                        return;
                    }
                    if (isPlacingBlocked.get(game)) {
                        event.setCancelled(true);
                        Utilities.msg(pe, instance.getMessages().getMessage("plz_wait"));
                        return;
                    }
                    Player p1 = instance.getServer().getPlayer((String) ((Map) instance.playersInGame.get(game)).get(1));
                    Player p2 = instance.getServer().getPlayer((String) ((Map) instance.playersInGame.get(game)).get(2));
                    if (isColumnFull(game, pos)) {
                        event.setCancelled(true);
                        Utilities.msg(pe, instance.getMessages().getMessage("play_full"));
                        return;
                    }
                    updateColumn(game, pos, be.getTypeId());
                    int id = be.getTypeId();
                    warpBlock(game, be, pos);
                    int amount = instance.playersBlocks.get(pe.getName().toLowerCase());
                    amount--;
                    if (amount == 0) {
                        pe.getInventory().setItemInHand(new ItemStack(0));
                    } else {
                        pe.getInventory().getItemInHand().setAmount(amount);
                    }
                    pe.updateInventory();
                    startPlaceScheduler(game);
                    instance.playersBlocks.put(pe.getName().toLowerCase(), amount);
                    int pB = 0;
                    if (placedBlocks.containsKey(game)) {
                        pB = placedBlocks.get(game);
                    }
                    placedBlocks.put(game, pB + 1);
                    if (checkLines(game, id, pos)) {
                        submitStatistics(game, false, placedBlocks.get(game), instance.playersMaterial.get(pe.getName().toLowerCase()));
                        placedBlocks.remove(game);
                        if (p1 == pe) {
                            instance.sendMsg(game, "p1", ConnectFour.prefix + ChatColor.BOLD + instance.getMessages().getMessage("play_won"));
                            instance.sendMsg(game, "p2", ConnectFour.prefix + ChatColor.BOLD + instance.getMessages().getMessage("play_lost", "{GEGNER}"));
                            instance.getSt().submitPlayedGame(game, p1.getName(), p2.getName());
                        } else if (p2 == pe) {
                            instance.sendMsg(game, "p2", ConnectFour.prefix + ChatColor.BOLD + instance.getMessages().getMessage("play_won"));
                            instance.sendMsg(game, "p1", ConnectFour.prefix + ChatColor.BOLD + instance.getMessages().getMessage("play_lost", "{GEGNER}"));
                            instance.getSt().submitPlayedGame(game, p2.getName(), p1.getName());
                        }
                        instance.stopGame(game);
                        giveRewards(pe, game);
                        return;
                    }
                    if ((instance.playersBlocks.get(p1.getName().toLowerCase()) == 0) && (instance.playersBlocks.get(p2.getName().toLowerCase()) == 0)) {
                        submitStatistics(game, true, placedBlocks.get(game), "");
                        instance.sendMsg(game, "beide", ConnectFour.prefix + ChatColor.BOLD + instance.getMessages().getMessage("play_undecided"));
                        placedBlocks.remove(game);
                        instance.stopGame(game);
                        return;
                    }
                    if (p1 == pe) {
                        instance.whoseTurnN.put(game, p2.getName().toLowerCase());
                        if (instance.enableSpam) {
                            instance.sendMsg(game, "p1", ConnectFour.prefix + instance.getMessages().getMessage("play_next1", "{GEGNER}"));
                            instance.sendMsg(game, "p2", ConnectFour.prefix + instance.getMessages().getMessage("play_next2", "{GEGNER}"));
                        }
                    } else if (p2 == pe) {
                        instance.whoseTurnN.put(game, p1.getName().toLowerCase());
                        if (instance.enableSpam) {
                            instance.sendMsg(game, "p2", ConnectFour.prefix + instance.getMessages().getMessage("play_next1", "{GEGNER}"));
                            instance.sendMsg(game, "p1", ConnectFour.prefix + instance.getMessages().getMessage("play_next2", "{GEGNER}"));
                        }
                    }
                    return;
                }
                event.setCancelled(true);
                Utilities.msg(pe, instance.getMessages().getMessage("play_error1"));
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onKillBlock(BlockBreakEvent event) {
        Player pe = event.getPlayer();
        if ((instance.gameStarted != null) &&
                (instance.playerIsInGame.containsKey(pe.getName().toLowerCase()))) {
            event.setCancelled(true);
            return;
        }
        if (instance.list != null) {
            for (String game : instance.list) {
                if (isBlockOnDisplay(game, event.getBlock())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if (instance.select.containsKey("throwIn")) {
            Block b = event.getBlock();
            if (pe.getName().equalsIgnoreCase(instance.select.get("throwIn"))) {
                if (b.getTypeId() == 3) {
                    if ((instance.locs.containsKey("a1")) &&
                            (b.getLocation().getBlockX() == instance.locs.get("a1").getBlockX()) && (b.getLocation().getBlockY() == instance.locs.get("a1").getBlockY()) && (b.getLocation().getBlockZ() == instance.locs.get("a1").getBlockZ())) {
                        instance.locs.remove("a1");
                        Utilities.msg(pe, instance.getMessages().getMessage("create_removed_corner", "1"));
                        return;
                    }
                    if ((instance.locs.containsKey("a2")) &&
                            (b.getLocation().getBlockX() == instance.locs.get("a2").getBlockX()) && (b.getLocation().getBlockY() == instance.locs.get("a2").getBlockY()) && (b.getLocation().getBlockZ() == instance.locs.get("a2").getBlockZ())) {
                        instance.locs.remove("a2");
                        Utilities.msg(pe, instance.getMessages().getMessage("create_removed_corner", "2"));
                        return;
                    }
                }
                if (b.getTypeId() == 4) {
                    if ((instance.locs.containsKey("b1")) &&
                            (b.getLocation().getBlockX() == instance.locs.get("b1").getBlockX()) && (b.getLocation().getBlockY() == instance.locs.get("b1").getBlockY()) && (b.getLocation().getBlockZ() == instance.locs.get("b1").getBlockZ())) {
                        instance.locs.remove("b1");
                        Utilities.msg(pe, instance.getMessages().getMessage("create_removed_corner", "1"));
                        return;
                    }
                    if ((instance.locs.containsKey("b2")) &&
                            (b.getLocation().getBlockX() == instance.locs.get("b2").getBlockX()) && (b.getLocation().getBlockY() == instance.locs.get("b2").getBlockY()) && (b.getLocation().getBlockZ() == instance.locs.get("b2").getBlockZ())) {
                        instance.locs.remove("b2");
                        Utilities.msg(pe, instance.getMessages().getMessage("create_removed_corner", "2"));
                        return;
                    }
                }
            }
        }
    }

    private boolean isBlockOnDisplay(String game, Block b) {
        if ((instance.gameWorlds.containsKey(game)) && (instance.gameLocs.containsKey(game))) {
            Map<String, Integer> map = instance.gameLocs.get(game);
            String world = instance.gameWorlds.get(game);
            int ukX = map.get("kX");
            int ugX = map.get("gX");
            int ukZ = map.get("kZ");
            int ugZ = map.get("gZ");
            if (ukX == ugX) {
                int kZ = map.get("kZ");
                int gZ = map.get("gZ");
                int X = map.get("kX");
                int Y = map.get("Y");
                int bX = b.getLocation().getBlockX();
                int bZ = b.getLocation().getBlockZ();
                int bY = b.getLocation().getBlockY();
                if ((b.getWorld().getName().equalsIgnoreCase(world)) &&
                        (bY <= Y) && (bY >= Y - 7) &&
                        (bX == X) &&
                        (bZ >= kZ) && (bZ <= gZ)) {
                    return true;
                }
            } else if (ukZ == ugZ) {
                int kX = map.get("kX");
                int gX = map.get("gX");
                int Z = map.get("kZ");
                int Y = map.get("Y");
                int bX = b.getLocation().getBlockX();
                int bZ = b.getLocation().getBlockZ();
                int bY = b.getLocation().getBlockY();
                if ((b.getWorld().getName().equalsIgnoreCase(world)) &&
                        (bY <= Y) && (bY >= Y - 7) &&
                        (bZ == Z) &&
                        (bX >= kX) && (bX <= gX)) {
                    return true;
                }
            }
        }
        return false;
    }

    private int isBlockInEinwurf(Block b, String game) {
        if ((instance.gameWorlds.containsKey(game)) && (instance.gameLocs.containsKey(game))) {
            Map<String, Integer> map = instance.gameLocs.get(game);
            String world = instance.gameWorlds.get(game);
            int tkX = map.get("tkX");
            int tgX = map.get("tgX");
            int tkZ = map.get("tkZ");
            int tgZ = map.get("tgZ");
            if (tkX == tgX) {
                int kZ = map.get("tkZ");
                int gZ = map.get("tgZ");
                int X = map.get("tkX");
                int Y = map.get("tY");
                int bX = b.getLocation().getBlockX();
                int bZ = b.getLocation().getBlockZ();
                int bY = b.getLocation().getBlockY();
                if ((b.getWorld().getName().equalsIgnoreCase(world)) &&
                        (bY == Y) &&
                        (bX == X) &&
                        (bZ >= kZ) && (bZ <= gZ)) {
                    return getPositionInEinwurf(b, game);
                }
            } else if (tkZ == tgZ) {
                int kX = map.get("tkX");
                int gX = map.get("tgX");
                int Z = map.get("tkZ");
                int Y = map.get("tY");
                int bX = b.getLocation().getBlockX();
                int bZ = b.getLocation().getBlockZ();
                int bY = b.getLocation().getBlockY();
                if ((b.getWorld().getName().equalsIgnoreCase(world)) &&
                        (bY == Y) &&
                        (bZ == Z) &&
                        (bX >= kX) && (bX <= gX)) {
                    return getPositionInEinwurf(b, game);
                }
            }
        }
        return 0;
    }

    private Integer getPositionInEinwurf(Block b, String game) {
        int kX = instance.getConfig().getInt("games." + game + ".throwIn.kX");
        int gX = instance.getConfig().getInt("games." + game + ".throwIn.gX");
        int kZ = instance.getConfig().getInt("games." + game + ".throwIn.kZ");
        int gZ = instance.getConfig().getInt("games." + game + ".throwIn.gZ");
        if (kX == gX) {
            int i = gZ - b.getLocation().getBlockZ() + 1;
            return i;
        }
        if (kZ == gZ) {
            int i = gX - b.getLocation().getBlockX() + 1;
            return i;
        }
        return null;
    }

    private int difference(int a, int b) {
        if (a < b) {
            return b - a;
        }
        return a - b;
    }

    private boolean isColumnFull(String game, int pos) {
        if (!instance.columns.containsKey(game)) {
            List<Integer> column1 = new ArrayList();
            List<Integer> column2 = new ArrayList();
            List<Integer> column3 = new ArrayList();
            List<Integer> column4 = new ArrayList();
            List<Integer> column5 = new ArrayList();
            List<Integer> column6 = new ArrayList();
            List<Integer> column7 = new ArrayList();
            Map<Integer, List<Integer>> columns1to7 = new HashMap();
            columns1to7.put(1, column1);
            columns1to7.put(2, column2);
            columns1to7.put(3, column3);
            columns1to7.put(4, column4);
            columns1to7.put(5, column5);
            columns1to7.put(6, column6);
            columns1to7.put(7, column7);
            instance.columns.put(game, columns1to7);
        }
        return ((List) ((Map) instance.columns.get(game)).get(pos)).size() >= 6;
    }

    private void updateColumn(String game, int pos, int ID) {
        if (!instance.columns.containsKey(game)) {
            List<Integer> column1 = new ArrayList();
            List<Integer> column2 = new ArrayList();
            List<Integer> column3 = new ArrayList();
            List<Integer> column4 = new ArrayList();
            List<Integer> column5 = new ArrayList();
            List<Integer> column6 = new ArrayList();
            List<Integer> column7 = new ArrayList();
            Map<Integer, List<Integer>> columns1to7 = new HashMap();
            columns1to7.put(1, column1);
            columns1to7.put(2, column2);
            columns1to7.put(3, column3);
            columns1to7.put(4, column4);
            columns1to7.put(5, column5);
            columns1to7.put(6, column6);
            columns1to7.put(7, column7);
            instance.columns.put(game, columns1to7);
        }
        List<Integer> c1 = (List) ((Map) instance.columns.get(game)).get(1);
        List<Integer> c2 = (List) ((Map) instance.columns.get(game)).get(2);
        List<Integer> c3 = (List) ((Map) instance.columns.get(game)).get(3);
        List<Integer> c4 = (List) ((Map) instance.columns.get(game)).get(4);
        List<Integer> c5 = (List) ((Map) instance.columns.get(game)).get(5);
        List<Integer> c6 = (List) ((Map) instance.columns.get(game)).get(6);
        List<Integer> c7 = (List) ((Map) instance.columns.get(game)).get(7);
        if (pos == 1) {
            c1.add(ID);

            return;
        }
        if (pos == 2) {
            c2.add(ID);
            return;
        }
        if (pos == 3) {
            c3.add(ID);
            return;
        }
        if (pos == 4) {
            c4.add(ID);
            return;
        }
        if (pos == 5) {
            c5.add(ID);
            return;
        }
        if (pos == 6) {
            c6.add(ID);
            return;
        }
        if (pos == 7) {
            c7.add(ID);
            return;
        }
    }

    private void warpBlock(String game, Block b, int pos) {
        Material type = b.getType();
        b.setTypeId(0);
        String world = instance.getConfig().getString("games." + game + ".spawn.world");
        instance.getServer().getWorld(world).spawnFallingBlock(getLocPos(game, pos), type, (byte) 0);
    }

    private Location getLocPos(String game, int pos) {
        int Y = instance.getConfig().getInt("games." + game + ".display.y");
        int kX = instance.getConfig().getInt("games." + game + ".display.kX");
        int gX = instance.getConfig().getInt("games." + game + ".display.gX");
        int kZ = instance.getConfig().getInt("games." + game + ".display.kZ");
        int gZ = instance.getConfig().getInt("games." + game + ".display.gZ");
        String world = instance.getConfig().getString("games." + game + ".spawn.world");
        if (kX == gX) {
            int Z = gZ + 1 - pos;
            Location loc = new Location(instance.getServer().getWorld(world), gX, Y, Z);
            return loc;
        }
        if (kZ == gZ) {
            int X = gX + 1 - pos;
            Location loc = new Location(instance.getServer().getWorld(world), X, Y, gZ);
            return loc;
        }
        return null;
    }

    private void startPlaceScheduler(final String game) {
        isPlacingBlocked.put(game, Boolean.TRUE);
        instance.getServer().getScheduler().scheduleSyncDelayedTask(instance,
                new Runnable() {
                    public void run() {
                        isPlacingBlocked.put(game, Boolean.FALSE);
                    }
                }, 20L);
    }

    private boolean checkLines(String game, int id, int pos) {
        Map<Integer, List<Integer>> columnS = instance.columns.get(game);
        if (checkHorizontal(game, id, pos, columnS)) {
            return true;
        }
        if (checkVertical(game, id, pos, columnS)) {
            return true;
        }
        return checkDiagonal(game, id, pos, columnS);
    }

    private boolean checkDiagonal(String game, int id, int pos, Map<Integer, List<Integer>> c) {
        int e = c.get(pos).size();
        int s = pos;
        if ((c.containsKey(s + 1)) && (c.containsKey(s + 2)) && (c.containsKey(s + 3)) &&
                (c.get(s + 1).size() >= e + 1) && (c.get(s + 2).size() >= e + 2) && (c.get(s + 3).size() >= e + 3) &&
                ((Integer) ((List) c.get(s + 1)).get(e) == id) && ((Integer) ((List) c.get(s + 2)).get(e + 1) == id) && ((Integer) ((List) c.get(s + 3)).get(e + 2) == id)) {
            return true;
        }
        if ((c.containsKey(s + 1)) && (c.containsKey(s + 2)) && (c.containsKey(s - 1)) &&
                (c.get(s + 1).size() >= e + 1) && (c.get(s + 2).size() >= e + 2) && (c.get(s - 1).size() >= e - 1) &&
                (e - 2 >= 0) &&
                ((Integer) ((List) c.get(s + 1)).get(e) == id) && ((Integer) ((List) c.get(s + 2)).get(e + 1) == id) && ((Integer) ((List) c.get(s - 1)).get(e - 2) == id)) {
            return true;
        }
        if ((c.containsKey(s + 1)) && (c.containsKey(s - 1)) && (c.containsKey(s - 2)) &&
                (c.get(s + 1).size() >= e + 1) && (c.get(s - 1).size() >= e - 1) && (c.get(s - 2).size() >= e - 2) &&
                (e - 3 >= 0) &&
                ((Integer) ((List) c.get(s + 1)).get(e) == id) && ((Integer) ((List) c.get(s - 1)).get(e - 2) == id) && ((Integer) ((List) c.get(s - 2)).get(e - 3) == id)) {
            return true;
        }
        if ((c.containsKey(s - 1)) && (c.containsKey(s - 2)) && (c.containsKey(s - 3)) &&
                (c.get(s - 1).size() >= e - 1) && (c.get(s - 2).size() >= e - 2) && (c.get(s - 3).size() >= e - 3) &&
                (e - 4 >= 0) &&
                ((Integer) ((List) c.get(s - 1)).get(e - 2) == id) && ((Integer) ((List) c.get(s - 2)).get(e - 3) == id) && ((Integer) ((List) c.get(s - 3)).get(e - 4) == id)) {
            return true;
        }
        if ((c.containsKey(s + 1)) && (c.containsKey(s + 2)) && (c.containsKey(s + 3)) &&
                (c.get(s + 1).size() >= e - 1) && (c.get(s + 2).size() >= e - 2) && (c.get(s + 3).size() >= e - 3) &&
                (e - 4 >= 0) &&
                ((Integer) ((List) c.get(s + 1)).get(e - 2) == id) && ((Integer) ((List) c.get(s + 2)).get(e - 3) == id) && ((Integer) ((List) c.get(s + 3)).get(e - 4) == id)) {
            return true;
        }
        if ((c.containsKey(s + 1)) && (c.containsKey(s + 2)) && (c.containsKey(s - 1)) &&
                (c.get(s + 1).size() >= e - 1) && (c.get(s + 2).size() >= e - 2) && (c.get(s - 1).size() >= e + 1) &&
                (e - 3 >= 0) &&
                ((Integer) ((List) c.get(s + 1)).get(e - 2) == id) && ((Integer) ((List) c.get(s + 2)).get(e - 3) == id) && ((Integer) ((List) c.get(s - 1)).get(e) == id)) {
            return true;
        }
        if ((c.containsKey(s + 1)) && (c.containsKey(s - 1)) && (c.containsKey(s - 2)) &&
                (c.get(s + 1).size() >= e - 1) && (c.get(s - 1).size() >= e + 1) && (c.get(s - 2).size() >= e + 2) &&
                (e - 2 >= 0) &&
                ((Integer) ((List) c.get(s + 1)).get(e - 2) == id) && ((Integer) ((List) c.get(s - 1)).get(e) == id) && ((Integer) ((List) c.get(s - 2)).get(e + 1) == id)) {
            return true;
        }
        return (c.containsKey(s - 1)) && (c.containsKey(s - 2)) && (c.containsKey(s - 3)) &&
                (c.get(s - 1).size() >= e + 1) && (c.get(s - 2).size() >= e + 2) && (c.get(s - 3).size() >= e + 3) &&
                (e - 3 >= 0) &&
                ((Integer) ((List) c.get(s - 1)).get(e) == id) && ((Integer) ((List) c.get(s - 2)).get(e + 1) == id) && ((Integer) ((List) c.get(s - 3)).get(e + 2) == id);
    }

    private boolean checkHorizontal(String game, int id, int pos, Map<Integer, List<Integer>> c) {
        int e = c.get(pos).size();
        int s = pos;
        if ((c.containsKey(s + 1)) && (c.containsKey(s + 2)) && (c.containsKey(s + 3)) &&
                (c.get(s + 1).size() >= e) && (c.get(s + 2).size() >= e) && (c.get(s + 3).size() >= e) &&
                ((Integer) ((List) c.get(s + 1)).get(e - 1) == id) && ((Integer) ((List) c.get(s + 2)).get(e - 1) == id) && ((Integer) ((List) c.get(s + 3)).get(e - 1) == id)) {
            return true;
        }
        if ((c.containsKey(s + 1)) && (c.containsKey(s + 2)) && (c.containsKey(s - 1)) &&
                (c.get(s + 1).size() >= e) && (c.get(s + 2).size() >= e) && (c.get(s - 1).size() >= e) &&
                ((Integer) ((List) c.get(s + 1)).get(e - 1) == id) && ((Integer) ((List) c.get(s + 2)).get(e - 1) == id) && ((Integer) ((List) c.get(s - 1)).get(e - 1) == id)) {
            return true;
        }
        if ((c.containsKey(s + 1)) && (c.containsKey(s - 1)) && (c.containsKey(s - 2)) &&
                (c.get(s + 1).size() >= e) && (c.get(s - 1).size() >= e) && (c.get(s - 2).size() >= e) &&
                ((Integer) ((List) c.get(s + 1)).get(e - 1) == id) && ((Integer) ((List) c.get(s - 1)).get(e - 1) == id) && ((Integer) ((List) c.get(s - 2)).get(e - 1) == id)) {
            return true;
        }
        return (c.containsKey(s - 1)) && (c.containsKey(s - 2)) && (c.containsKey(s - 3)) &&
                (c.get(s - 1).size() >= e) && (c.get(s - 2).size() >= e) && (c.get(s - 3).size() >= e) &&
                ((Integer) ((List) c.get(s - 1)).get(e - 1) == id) && ((Integer) ((List) c.get(s - 2)).get(e - 1) == id) && ((Integer) ((List) c.get(s - 3)).get(e - 1) == id);
    }

    private boolean checkVertical(String game, int id, int pos, Map<Integer, List<Integer>> c) {
        int e = c.get(pos).size();
        int e2 = e - 2;
        int e3 = e - 3;
        int e4 = e - 4;
        int s = pos;
        return (e >= 4) &&
                ((Integer) ((List) c.get(s)).get(e4) == id) && ((Integer) ((List) c.get(s)).get(e3) == id) && ((Integer) ((List) c.get(s)).get(e2) == id);
    }

    private void submitStatistics(String game, boolean drawn, int pb, String winnerBlock) {
        instance.getSt().load();
        instance.getSt().addPlayedGame();
        if (drawn) {
            instance.getSt().addDrawn();
        } else {
            if (pb < 15) {
                instance.getSt().addWin7_14();
            } else if ((pb > 14) && (pb < 23)) {
                instance.getSt().addWin15_22();
            } else if ((pb > 22) && (pb < 31)) {
                instance.getSt().addWin23_30();
            } else if ((pb > 30) && (pb < 39)) {
                instance.getSt().addWin31_38();
            } else if (pb > 38) {
                instance.getSt().addWin39_42();
            }
            if (winnerBlock.toUpperCase() == "SAND") {
                instance.getSt().addWinSand();
            } else {
                instance.getSt().addWinGravel();
            }
        }
        instance.getSt().save();
        instance.getSt().load();
    }

    private void giveRewards(Player p, String game) {
        if (!instance.list.contains(game.toLowerCase())) {
            return;
        }
        if (instance.getConfig().get("games." + game + ".rewards") != null) {
            String path = "games." + game.toString() + ".rewards";
            List<String> def = null;
            def.add(0, null);
            List<?> rewardlist = instance.getConfig().getList(path, def);
            for (List<String> rewards : (List<List<String>>) rewardlist) {
                //now rewards should be <id:data> <amount>
                // amount defaults to 1
                String item = rewards.get(0);
                Integer amount = 1;
                if (rewards.size() == 2) {
                    String amountstr = rewards.get(1);
                    if (Integer.parseInt(amountstr) != 0)amount=Integer.parseInt(amountstr);
                }
                ItemStack reward = Utilities.getItem(item,amount, 0);
                p.getInventory().addItem(reward);
                }
                Utilities.msg(p, instance.getMessages().getMessage("gotReward"));
            }
            if ((instance.isEcon()) &&
                    (instance.getConfig().get("games." + game + ".ecoReward") != null)) {
                try {
                    int reward = instance.getConfig().getInt("games." + game + ".ecoReward");
                    EconomyResponse r = ConnectFour.econ.depositPlayer(p, reward);
                    if (r.transactionSuccess()) {
                        Utilities.msg(p, instance.getMessages().getMessage("gotEcoReward", String.valueOf(reward)));
                    }
                } catch (Exception localException1) {
                }
            }
            if ((instance.isEcon()) &&
                    (instance.getConfig().get("games." + game + ".betMoney") != null)) {
                try {
                    int money = instance.getConfig().getInt("games." + game + ".betMoney");
                    EconomyResponse r = ConnectFour.econ.depositPlayer(p, money);
                    if (r.transactionSuccess()) {
                        Utilities.msg(p, instance.getMessages().getMessage("gotBetReward", String.valueOf(money)));
                    }
                } catch (Exception localException2) {
                }
            }
        }
    }

