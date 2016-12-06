package ch.dragon252525.connectFour;

import ch.dragon252525.connectFour.listeners.BlockEvents;
import ch.dragon252525.connectFour.listeners.PlayerEvents;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ConnectFour
        extends JavaPlugin {
    public static final String prefix = ChatColor.GOLD + "[Connect" + ChatColor.RED + "Four] " + ChatColor.YELLOW;
    private String selectedGame = null;
    public final Map<String, String> select = new HashMap();
    public final Map<String, Location> locs = new HashMap();
    private final Map<String, Boolean> isSet = new HashMap();
    public final Map<String, Map<String, Integer>> gameLocs = new HashMap();
    public final Map<String, String> gameWorlds = new HashMap();
    public final Set<String> list = new HashSet();
    private Set<String> list2 = new HashSet();
    private final Map<String, Location> beforeJoinLocs = new HashMap();
    public boolean enableSpam = true;
    private boolean enableTeleport = true;
    private boolean enableAutoClear = false;
    public boolean enableJoinSigns = true;
    private boolean enableSameWorld = false;
    private boolean enableMaxDistance = false;
    private int maxDistance = 20;
    private static BlockEvents blockEvents;
    private final Map<String, String> sand = new HashMap();
    private final Map<String, String> gravel = new HashMap();

    public static Economy econ = null;

    public boolean isEcon() {
        return useEcon;
    }

    private boolean useEcon = true;
    private Statistics st;

    public Messages getMessages() {
        return messages;
    }

    private Messages messages = null;

    public void onDisable() {
        if (this.list != null) {
            for (String game : this.list) {
                if (this.gameStarted.containsKey(game)) {
                    stopGame(game);
                }
            }
        }
        this.st.save();
        messages = null;
        System.out.println("[ConnectFour] disabled.");
    }

    public void onEnable() {
        loadConfig();
        loadListeners();
        loadLanguage();
        loadList();
        if (!setupEconomy()) {
            this.useEcon = false;
        }
        this.st = new Statistics(this);
        this.st.load();
        this.st.loadMySqlData();
        System.out.println("[ConnectFour] enabled.");
    }

    private void loadListeners() {
        PlayerEvents playerEvents = new PlayerEvents(this);
        blockEvents = new BlockEvents(this);
        getServer().getPluginManager().registerEvents(playerEvents, this);
        getServer().getPluginManager().registerEvents(blockEvents, this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ((sender instanceof Player)) {
            Player p = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("cfour")) {
                if (args.length == 0) {
                    PluginDescriptionFile info = getDescription();
                    p.sendMessage(ChatColor.GOLD + "Connect" + ChatColor.RED + "Four " + ChatColor.AQUA + info.getVersion() + ChatColor.YELLOW + " by " + ChatColor.AQUA + "Dragon252525");
                    p.sendMessage(ChatColor.YELLOW + "/cfour help");
                    return true;
                }
                String game;
                switch (args[0].toLowerCase()) {

                    case "info":
                        if (!p.hasPermission("cfour.user.play")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        p.sendMessage("");
                        p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Connect " + ChatColor.RED + "" + ChatColor.BOLD + "Four " + ChatColor.DARK_PURPLE + "Infos");
                        p.sendMessage(ChatColor.YELLOW + messages.getMessage("info"));
                        p.sendMessage("");
                        return true;
                    case "reload":
                        if (!p.hasPermission("cfour.admin.reload")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        reloadConfig();
                        loadConfig();
                        loadLanguage();
                        this.st.save();
                        this.st.load();
                        loadList();
                        this.st.loadMySqlData();
                        p.sendMessage(prefix + "config.yml reloaded.");
                        p.sendMessage(prefix + "language.yml reloaded.");
                        return true;
                    case "lang":
                        if (!p.hasPermission("cfour.admin.messages")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        if (args.length >= 2){
                            getConfig().set("language", args[1].toLowerCase());
                            saveConfig();
                            messages = null;
                            loadLanguage();
                            Utilities.msg(p, messages.getMessage("langChanged"));
                            return true;
                        }
                        Utilities.msg(p, "/cfour lang <Locale>");
                        return true;
                    case "enable":
                        if (!p.hasPermission("cfour.admin.disable")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        if (args.length < 2) {
                            Utilities.msg(p, messages.getMessage("game_which_enable"));
                            return true;
                        }
                        game = args[1].toLowerCase();
                        if (!this.list2.contains(game)) {
                            Utilities.msg(p, messages.getMessage("game_notExists"));
                            return true;
                        }
                        getConfig().set("games." + game + ".enabled", Boolean.TRUE);
                        saveConfig();
                        Utilities.msg(p, messages.getMessage("game_enabled", game));
                        loadList();
                        return true;
                    case "tp":
                        if (!p.hasPermission("cfour.user.tp")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        if (args.length < 2) {
                            Utilities.msg(p, messages.getMessage("game_which_tp"));
                            return true;
                        }
                        game = args[1].toLowerCase();
                        if (!this.list.contains(game)) {
                            Utilities.msg(p, messages.getMessage("game_notExists"));
                            return true;
                        }
                        p.teleport(loc(game));
                        Utilities.msg(p, messages.getMessage("game_youAreAt", game));
                        return true;
                    case "statistics":
                    case "stats":
                        if (!p.hasPermission("cfour.user.stats")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        this.st.load();
                        int pg = this.st.getPlayedGames();
                        p.sendMessage("");
                        p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Connect " + ChatColor.RED + "" + ChatColor.BOLD + "Four " + ChatColor.DARK_PURPLE + "" + messages.getMessage("stats"));
                        p.sendMessage(ChatColor.YELLOW + "-------------------------------------");
                        p.sendMessage(ChatColor.YELLOW + messages.getMessage("statsPlayedGames") + ChatColor.AQUA + pg);
                        p.sendMessage(ChatColor.YELLOW + "-------------------------------------");
                        p.sendMessage(ChatColor.GOLD + messages.getMessage("statsPlacedBlocksToWinn"));
                        p.sendMessage(ChatColor.YELLOW + " 7 - 14: " + ChatColor.AQUA + getPercent(pg, this.st.getWins7_14()) + "%");
                        p.sendMessage(ChatColor.YELLOW + "15 - 22: " + ChatColor.AQUA + getPercent(pg, this.st.getWins15_22()) + "%");
                        p.sendMessage(ChatColor.YELLOW + "23 - 30: " + ChatColor.AQUA + getPercent(pg, this.st.getWins23_30()) + "%");
                        p.sendMessage(ChatColor.YELLOW + "31 - 38: " + ChatColor.AQUA + getPercent(pg, this.st.getWins31_38()) + "%");
                        p.sendMessage(ChatColor.YELLOW + "39 - 42: " + ChatColor.AQUA + getPercent(pg, this.st.getWins39_42()) + "%");
                        p.sendMessage(ChatColor.YELLOW + messages.getMessage("statsDrwan") + ChatColor.AQUA + getPercent(pg, this.st.getDrawns()) + "%");
                        p.sendMessage(ChatColor.YELLOW + "-------------------------------------");
                        p.sendMessage(ChatColor.GOLD + messages.getMessage("statsWonBy"));
                        p.sendMessage(ChatColor.YELLOW + "Sand: " + ChatColor.AQUA + getPercent(pg - this.st.getDrawns(), this.st.getWinsSand()) + "%");
                        p.sendMessage(ChatColor.YELLOW + "Gravel: " + ChatColor.AQUA + getPercent(pg - this.st.getDrawns(), this.st.getWinsGravel()) + "%");
                        p.sendMessage(ChatColor.YELLOW + "-------------------------------------");
                        p.sendMessage(ChatColor.GOLD + messages.getMessage("statsGraphs"));
                        p.sendMessage(ChatColor.AQUA + "https://mcstats.org/plugin/connectfour");
                        p.sendMessage("");
                        return true;
                    case "join":
                        if (!p.hasPermission("cfour.user.play")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        if (args.length < 2) {
                            Utilities.msg(p, messages.getMessage("game_which_join"));
                            return true;
                        }
                        game = args[1].toLowerCase();
                        tryToJoinGame(p, game);
                        return true;
                    case "clean":
                    case "clear":
                        if (!p.hasPermission("cfour.admin.clean")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        if (args.length < 2) {
                            Utilities.msg(p, messages.getMessage("game_which_display"));
                            return true;
                        }
                        game = args[1].toLowerCase();
                        if (!this.list2.contains(game)) {
                            Utilities.msg(p, messages.getMessage("game_notExists"));
                            return true;
                        }
                        cleanDisplay(game);
                        return true;
                    case "setblocks":
                        if (!p.hasPermission("cfour.admin.create")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        if (args.length < 2) {
                            Utilities.msg(p, messages.getMessage("game_which_setblocks"));
                            return true;
                        }
                        if (args.length < 4) {
                            Utilities.msg(p, "/cfour setblocks <game> <ID1> <ID2>");
                            return true;
                        }
                        game = args[1].toLowerCase();
                        if (!this.list2.contains(game)) {
                            Utilities.msg(p, messages.getMessage("game_notExists"));
                            return true;
                        }
                        int id1;
                        try {
                            id1 = Integer.parseInt(args[2]);
                        } catch (Exception e) {

                            Utilities.msg(p, messages.getMessage("notANumber", args[2]));
                            return true;
                        }
                        int id2;
                        try {
                            id2 = Integer.parseInt(args[3]);
                        } catch (Exception e) {
                            Utilities.msg(p, messages.getMessage("notANumber", args[3]));
                            return true;
                        }
                        getConfig().set("games." + game + ".sand", id1);
                        getConfig().set("games." + game + ".gravel", id2);
                        saveConfig();
                        loadList();
                        Utilities.msg(p, messages.getMessage("game_setblocks", game));
                        return true;
                    case "setrewards":
                        if (!p.hasPermission("cfour.admin.create")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        if (args.length < 3) {
                            Utilities.msg(p, "/cfour setrewards <game> <ID:amount[:damage]> [<ID:amount[:damage]>...]");
                            return true;
                        }
                        game = args[1].toLowerCase();
                        if (!this.list.contains(game)) {
                            Utilities.msg(p, messages.getMessage("game_notExists"));
                            return true;
                        }
                        List<String[]> rewardlist = new ArrayList();
                        for (int i = 2; i < args.length; i++) {
                            String[] reward = args[i].split(":");
                            if (reward.length == 2) {
                                int iu = 0;
                                try {
                                    Integer.parseInt(reward[0]);
                                    iu = 1;
                                    Integer.parseInt(reward[1]);
                                } catch (Exception e) {
                                    if (iu == 0) {
                                        Utilities.msg(p, messages.getMessage("notANumber", reward[iu]));
                                        return true;
                                    }
                                    Utilities.msg(p, messages.getMessage("notANumber", reward[iu]));
                                    return true;
                                }
                                rewardlist.add(reward);
                            } else if (reward.length == 3) {
                                int iu = 0;
                                try {
                                    Integer.parseInt(reward[0]);
                                    iu = 1;
                                    Integer.parseInt(reward[1]);
                                    iu = 2;
                                    Integer.parseInt(reward[2]);
                                } catch (Exception e) {
                                    if (iu == 0) {
                                        Utilities.msg(p, messages.getMessage("notANumber", reward[iu]));
                                        return true;
                                    }
                                    if (iu == 1) {
                                        Utilities.msg(p, messages.getMessage("notANumber", reward[iu]));
                                        return true;
                                    }
                                    Utilities.msg(p, messages.getMessage("notANumber", reward[iu]));
                                    return true;
                                }
                                rewardlist.add(reward);
                            } else {
                                p.sendMessage("17:32 = 32 Wood");
                                p.sendMessage("17:1:32 = 32 Birchwood");
                                return true;
                            }
                        }
                        Utilities.msg(p, messages.getMessage("setRewards", game));
                        getConfig().set("games." + game + ".rewards", rewardlist);
                        saveConfig();
                        reloadConfig();
                        return true;
                    case "setbetmoney":
                        if (!p.hasPermission("cfour.admin.create")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        if (!this.useEcon) {
                            Utilities.msg(p, messages.getMessage("noVault"));
                            return true;
                        }
                        if (args.length < 3) {
                            Utilities.msg(p, "/cfour setbetmoney <game> <money>]");
                            return true;
                        }
                        game = args[1].toLowerCase();
                        if (!this.list.contains(game)) {
                            Utilities.msg(p, messages.getMessage("game_notExists"));
                            return true;
                        }
                        int money = 0;
                        try {
                            money = Integer.parseInt(args[2]);
                        } catch (Exception e) {
                            Utilities.msg(p, messages.getMessage("notANumber", args[2]));
                            return true;
                        }
                        Utilities.msg(p, messages.getMessage("setBetMoney", game));
                        getConfig().set("games." + game + ".betMoney", money);
                        saveConfig();
                        reloadConfig();
                        return true;
                    case "removebetmoney":

                        if (!p.hasPermission("cfour.admin.create")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        if (args.length < 1) {
                            Utilities.msg(p, "/cfour removebetmoney <game>");
                            return true;
                        }
                        game = args[1].toLowerCase();
                        if (!this.list.contains(game)) {
                            Utilities.msg(p, messages.getMessage("game_notExists"));
                            return true;
                        }
                        getConfig().set("games." + game + ".betMoney", null);
                        saveConfig();
                        Utilities.msg(p, messages.getMessage("removedBetMoney", game));
                        return true;
                    case "setecoreward":

                        if (!p.hasPermission("cfour.admin.create")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        if (!this.useEcon) {
                            Utilities.msg(p, messages.getMessage("noVault"));
                            return true;
                        }
                        if (args.length < 3) {
                            Utilities.msg(p, "/cfour setecoreward <game> <reward>]");
                            return true;
                        }
                        game = args[1].toLowerCase();
                        if (!this.list.contains(game)) {
                            Utilities.msg(p, messages.getMessage("game_notExists"));
                            return true;
                        }
                        int reward = 0;
                        try {
                            reward = Integer.parseInt(args[2]);
                        } catch (Exception e) {
                            Utilities.msg(p, messages.getMessage("notANumber", args[2]));
                            return true;
                        }
                        Utilities.msg(p, messages.getMessage("setEcoRewards", game));
                        getConfig().set("games." + game + ".ecoReward", reward);
                        saveConfig();
                        reloadConfig();
                        return true;
                    case "removerewards":

                        if (!p.hasPermission("cfour.admin.create")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        if (args.length < 1) {
                            Utilities.msg(p, "/cfour removerewards <game>");
                            return true;
                        }
                        game = args[1].toLowerCase();
                        if (!this.list.contains(game)) {
                            Utilities.msg(p, messages.getMessage("game_notExists"));
                            return true;
                        }
                        getConfig().set("games." + game + ".rewards", null);
                        saveConfig();
                        Utilities.msg(p, messages.getMessage("removedRewards", game));
                        return true;
                    case "removeecoreward":

                        if (!p.hasPermission("cfour.admin.create")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        if (args.length < 1) {
                            Utilities.msg(p, "/cfour removeecoreward <game>");
                            return true;
                        }
                        game = args[1].toLowerCase();
                        if (!this.list.contains(game)) {
                            Utilities.msg(p, messages.getMessage("game_notExists"));
                            return true;
                        }
                        getConfig().set("games." + game + ".ecoReward", null);
                        saveConfig();
                        Utilities.msg(p, messages.getMessage("removedEcoRewards", game));
                        return true;
                    case "setspawn":

                        if (!p.hasPermission("cfour.admin.create")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        if (this.selectedGame != null) {
                            getConfig().set("games." + this.selectedGame + ".spawn.x", p.getLocation().getX());
                            getConfig().set("games." + this.selectedGame + ".spawn.y", p.getLocation().getY());
                            getConfig().set("games." + this.selectedGame + ".spawn.z", p.getLocation().getZ());
                            getConfig().set("games." + this.selectedGame + ".spawn.yaw", p.getLocation().getYaw());
                            getConfig().set("games." + this.selectedGame + ".spawn.pitch", p.getLocation().getPitch());
                            getConfig().set("games." + this.selectedGame + ".spawn.world", p.getLocation().getWorld().getName());
                            saveConfig();
                            loadList();
                            Utilities.msg(p, messages.getMessage("game_spawnSaved", this.selectedGame));
                            return true;
                        }
                        if (args.length > 1) {
                            game = args[1].toLowerCase();
                            if (!this.list.contains(game)) {
                                Utilities.msg(p, messages.getMessage("game_notExists"));
                                return true;
                            }
                            getConfig().set("games." + game.toLowerCase() + ".spawn.x", p.getLocation().getX());
                            getConfig().set("games." + game.toLowerCase() + ".spawn.y", p.getLocation().getY());
                            getConfig().set("games." + game.toLowerCase() + ".spawn.z", p.getLocation().getZ());
                            getConfig().set("games." + game.toLowerCase() + ".spawn.yaw", p.getLocation().getYaw());
                            getConfig().set("games." + game.toLowerCase() + ".spawn.pitch", p.getLocation().getPitch());
                            getConfig().set("games." + game.toLowerCase() + ".spawn.world", p.getLocation().getWorld().getName());
                            saveConfig();
                            loadList();
                            Utilities.msg(p, messages.getMessage("game_spawnSaved", game));
                            return true;
                        }
                        Utilities.msg(p, "/cfour setspawn <game>");
                        return true;
                    case "stop":

                        if (!p.hasPermission("cfour.admin.stop")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        if (args.length == 1) {
                            Utilities.msg(p, messages.getMessage("game_which_stop"));
                            return true;
                        }
                        if (!this.list.contains(args[1].toLowerCase())) {
                            Utilities.msg(p, messages.getMessage("game_notExists"));
                            return true;
                        }
                        stopGame(args[1].toString().toLowerCase());
                        return true;
                    case "list":

                        if (!p.hasPermission("cfour.user.list")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        if (this.list.size() == 0) {
                            Utilities.msg(p, messages.getMessage("game_list") + " ");
                            return true;
                        }
                        Utilities.msg(p, messages.getMessage("game_list") + " " + ChatColor.AQUA + this.list.toString().replace("[", "").replace("]", "").replace(",", new StringBuilder().append(ChatColor.YELLOW).append(",").append(ChatColor.AQUA).toString()));
                        return true;
                    case "leave":

                        if (!p.hasPermission("cfour.user.play")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        if (this.playerIsInGame.containsKey(p.getName().toLowerCase())) {
                            game = this.playerIsInGame.get(p.getName().toLowerCase());
                            if (this.gameStarted.containsKey(game)) {
                                Utilities.msg(p, messages.getMessage("left_self"));
                                if (((String) ((Map) this.playersInGame.get(game)).get(1)).equalsIgnoreCase(p.getName())) {
                                    sendMsg(game, "p2", prefix + messages.getMessage("left_other", "{p1}"));
                                    sendMsg(game, "beide", prefix + messages.getMessage("game_stoped"));
                                }
                                if (((String) ((Map) this.playersInGame.get(game)).get(2)).equalsIgnoreCase(p.getName())) {
                                    sendMsg(game, "p1", prefix + messages.getMessage("left_other", "{p2}"));
                                    sendMsg(game, "beide", prefix + messages.getMessage("game_stoped"));
                                }
                                stopGame(game);
                                return true;
                            }
                            Utilities.msg(p, messages.getMessage("left_self"));
                            this.playerIsInGame.remove(p.getName().toLowerCase());
                            this.playersInGame.remove(game);
                            if (this.enableTeleport) {
                                p.teleport(this.beforeJoinLocs.get(p.getName().toLowerCase()));
                            }
                            this.beforeJoinLocs.remove(p.getName().toLowerCase());

                            return true;
                        }
                    case "remove":

                        if (!p.hasPermission("cfour.admin.create")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        if (args.length == 1) {
                            Utilities.msg(p, messages.getMessage("game_which_delete"));
                            return true;
                        }
                        if (!this.list2.contains(args[1].toLowerCase())) {
                            Utilities.msg(p, messages.getMessage("game_notExists"));
                            return true;
                        }
                        getConfig().set("games." + args[1].toString().toLowerCase(), null);
                        saveConfig();
                        loadList();
                        Utilities.msg(p, messages.getMessage("game_deleted", args[1]));
                        return true;
                    case "save":
                        if (!p.hasPermission("cfour.admin.create")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        loadList();
                        if ((this.locs.containsKey("a2")) && (this.locs.containsKey("a1")) && (this.locs.containsKey("b2")) && (this.locs.containsKey("b1"))) {
                            Map<Integer, Integer> mX = whichIsBigger(this.locs.get("a1").getBlockX(), this.locs.get("a2").getBlockX());
                            Map<Integer, Integer> mZ = whichIsBigger(this.locs.get("a1").getBlockZ(), this.locs.get("a2").getBlockZ());
                            getConfig().set("games." + this.selectedGame + ".throwIn.y", this.locs.get("a1").getBlockY());
                            int mX1 = mX.get(1);
                            int mX2 = mX.get(2);
                            int mZ1 = mZ.get(1);
                            int mZ2 = mZ.get(2);
                            if (mX1 == mX2) {
                                getConfig().set("games." + this.selectedGame + ".throwIn.kX", mX.get(1));
                                getConfig().set("games." + this.selectedGame + ".throwIn.gX", mX.get(2));
                                getConfig().set("games." + this.selectedGame + ".throwIn.kZ", mZ.get(1) + 1);
                                getConfig().set("games." + this.selectedGame + ".throwIn.gZ", mZ.get(2) - 1);
                            }
                            if (mZ1 == mZ2) {
                                getConfig().set("games." + this.selectedGame + ".throwIn.kX", mX.get(1) + 1);
                                getConfig().set("games." + this.selectedGame + ".throwIn.gX", mX.get(2) - 1);
                                getConfig().set("games." + this.selectedGame + ".throwIn.kZ", mZ.get(1));
                                getConfig().set("games." + this.selectedGame + ".throwIn.gZ", mZ.get(2));
                            }
                            saveConfig();
                            this.isSet.put("throwIn", Boolean.TRUE);
                            this.locs.remove("a1");
                            this.locs.remove("a2");
                            Utilities.msg(p, messages.getMessage("saved_throwin"));
                            this.select.remove("throwIn");

                            getConfig().set("games." + this.selectedGame + ".display.y", this.locs.get("b1").getBlockY());
                            Map<Integer, Integer> mXb = whichIsBigger(this.locs.get("b1").getBlockX(), this.locs.get("b2").getBlockX());
                            Map<Integer, Integer> mZb = whichIsBigger(this.locs.get("b1").getBlockZ(), this.locs.get("b2").getBlockZ());
                            int mX1b = mXb.get(1);
                            int mX2b = mXb.get(2);
                            int mZ1b = mZb.get(1);
                            int mZ2b = mZb.get(2);
                            if (mX1b == mX2b) {
                                getConfig().set("games." + this.selectedGame + ".display.kX", mXb.get(1));
                                getConfig().set("games." + this.selectedGame + ".display.gX", mXb.get(2));
                                getConfig().set("games." + this.selectedGame + ".display.kZ", mZb.get(1) + 1);
                                getConfig().set("games." + this.selectedGame + ".display.gZ", mZb.get(2) - 1);
                            }
                            if (mZ1b == mZ2b) {
                                getConfig().set("games." + this.selectedGame + ".display.kX", mXb.get(1) + 1);
                                getConfig().set("games." + this.selectedGame + ".display.gX", mXb.get(2) - 1);
                                getConfig().set("games." + this.selectedGame + ".display.kZ", mZb.get(1));
                                getConfig().set("games." + this.selectedGame + ".display.gZ", mZb.get(2));
                            }
                            saveConfig();
                            getConfig().set("games." + this.selectedGame + ".sand", 12);
                            getConfig().set("games." + this.selectedGame + ".gravel", 13);
                            saveConfig();
                            this.isSet.put("display", Boolean.TRUE);
                            this.locs.remove("b1");
                            this.locs.remove("b2");
                            Utilities.msg(p, messages.getMessage("saved_display"));
                            this.select.remove("display");
                            if ((this.isSet.containsKey("throwIn")) && (this.isSet.containsKey("display"))) {
                                nextStep(p);
                            }
                            return true;
                        }
                        Utilities.msg(p, messages.getMessage("select_both"));
                        return true;
                    case "create":
                        if (!p.hasPermission("cfour.admin.create")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        if (args.length < 2) {
                            Utilities.msg(p, "/cfour create <name>");
                            return true;
                        }
                        String name = args[1].toString().toLowerCase();
                        this.selectedGame = name;
                        Utilities.msg(p, messages.getMessage("create_select"));
                        this.select.put("throwIn", p.getName().toLowerCase());
                        this.select.put("display", p.getName().toLowerCase());
                        return true;
                    case "help":

                        if (!p.hasPermission("cfour.user.help")) {
                            Utilities.msg(p, messages.getMessage("noPermission"));
                            return true;
                        }
                        showHelp(p);
                        return true;
                    default:
                        PluginDescriptionFile info = getDescription();
                        p.sendMessage(ChatColor.GOLD + "Connect" + ChatColor.RED + "Four " + ChatColor.AQUA + info.getVersion() + ChatColor.YELLOW + " by " + ChatColor.AQUA + "Dragon252525");
                        p.sendMessage(ChatColor.YELLOW + "/cfour help");
                        return true;
                }
            }
        }
        return false;
    }

    public void tryToJoinGame(Player p, String game)
    {
        if (!this.list.contains(game))
        {
            Utilities.msg(p, messages.getMessage("game_notExists"));
            return;
        }
        if (this.enableMaxDistance)
        {
            if (loc(game).getWorld() != p.getWorld())
            {
                Utilities.msg(p, messages.getMessage("tooFarAwayWrongWorld", String.valueOf(this.maxDistance)));
                return;
            }
            if (loc(game).distance(p.getLocation()) > this.maxDistance)
            {
                Utilities.msg(p, messages.getMessage("tooFarAway", String.valueOf(this.maxDistance)));
                return;
            }
        }
        if ((this.enableSameWorld) &&
                (loc(game).getWorld() != p.getWorld()))
        {
            Utilities.msg(p, messages.getMessage("wrongWorld"));
            return;
        }
        if ((this.useEcon) &&
                (getConfig().get("games." + game + ".betMoney") != null)) {
            try
            {
                int money = getConfig().getInt("games." + game + ".betMoney");
                if (econ.has(p, money))
                {
                    EconomyResponse r = econ.withdrawPlayer(p, money);
                    if (r.transactionSuccess())
                    {
                        String curName = econ.currencyNamePlural();
                        if (money == 1) {
                            curName = econ.currencyNameSingular();
                        }
                        String[]args = null;
                        args[0]= String.valueOf(money) + curName;
                        args[1]= 2 * money + econ.currencyNamePlural();
                        Utilities.msg(p, messages.getMessage("betted",args));
                    }
                }
                else
                {
                    Utilities.msg(p, messages.getMessage("notEnoughMoney"));
                    return;
                }
            }
            catch (Exception localException) {}
        }
        joinGame(p, game);
    }

    private void showHelp(Player p)
    {
        p.sendMessage("");
        p.sendMessage(ChatColor.GOLD +""+ ChatColor.BOLD + "Connect " + ChatColor.RED +""+ ChatColor.BOLD + "Four " + ChatColor.DARK_PURPLE + messages.getMessage("help_user_1"));
        if (p.hasPermission("cfour.user.play"))
        {
            p.sendMessage(ChatColor.YELLOW + "/cfour info " + ChatColor.DARK_AQUA + messages.getMessage("help_user_2"));
            p.sendMessage(ChatColor.YELLOW + "/cfour join <game> " + ChatColor.DARK_AQUA + messages.getMessage("help_user_4"));
            p.sendMessage(ChatColor.YELLOW + "/cfour leave " + ChatColor.DARK_AQUA + messages.getMessage("help_user_5"));
        }
        if (p.hasPermission("cfour.user.tp")) {
            p.sendMessage(ChatColor.YELLOW + "/cfour tp <game> " + ChatColor.DARK_AQUA + messages.getMessage("help_user_6"));
        }
        if (p.hasPermission("cfour.user.list")) {
            p.sendMessage(ChatColor.YELLOW + "/cfour list " + ChatColor.DARK_AQUA + messages.getMessage("help_user_3"));
        }
        if (p.hasPermission("cfour.user.stats")) {
            p.sendMessage(ChatColor.YELLOW + "/cfour stats " + ChatColor.DARK_AQUA + messages.getMessage("help_user_7"));
        }
        if ((p.hasPermission("cfour.admin.reload")) || (p.hasPermission("cfour.admin.messages")) || (p.hasPermission("cfour.admin.clean")) || (p.hasPermission("cfour.admin.create")) || (p.hasPermission("cfour.admin.stop")) || (p.hasPermission("cfour.admin.disable")))
        {
            p.sendMessage("");
            p.sendMessage(ChatColor.GOLD +""+ ChatColor.BOLD + "Connect " + ChatColor.RED +""+ ChatColor.BOLD + "Four " + ChatColor.DARK_PURPLE + messages.getMessage("help_admin_1"));
            if (p.hasPermission("cfour.admin.create"))
            {
                p.sendMessage(ChatColor.YELLOW + "/cfour create <name> " + ChatColor.DARK_AQUA + messages.getMessage("help_admin_2"));
                p.sendMessage(ChatColor.YELLOW + "/cfour remove <name> " + ChatColor.DARK_AQUA + messages.getMessage("help_admin_3"));
                p.sendMessage(ChatColor.YELLOW + "/cfour setspawn <game> " + ChatColor.DARK_AQUA + messages.getMessage("help_admin_5"));
                p.sendMessage(ChatColor.YELLOW + "/cfour setrewards <game> " + ChatColor.DARK_AQUA + messages.getMessage("help_admin_9"));
                p.sendMessage(ChatColor.YELLOW + "/cfour setEcoReward <game> " + ChatColor.DARK_AQUA + messages.getMessage("help_admin_12"));
                p.sendMessage(ChatColor.YELLOW + "/cfour setBetMoney <game> " + ChatColor.DARK_AQUA + messages.getMessage("help_admin_16"));
                p.sendMessage(ChatColor.YELLOW + "/cfour removerewards <game> " + ChatColor.DARK_AQUA + messages.getMessage("help_admin_10"));
                p.sendMessage(ChatColor.YELLOW + "/cfour removeEcoReward <game> " + ChatColor.DARK_AQUA + messages.getMessage("help_admin_13"));
                p.sendMessage(ChatColor.YELLOW + "/cfour removeBetMoney <game> " + ChatColor.DARK_AQUA + messages.getMessage("help_admin_17"));
                p.sendMessage(ChatColor.YELLOW + "/cfour setblocks <game> " + ChatColor.DARK_AQUA + messages.getMessage("help_admin_15"));
            }
            if (p.hasPermission("cfour.admin.stop")) {
                p.sendMessage(ChatColor.YELLOW + "/cfour stop <game> " + ChatColor.DARK_AQUA + messages.getMessage("help_admin_6"));
            }
            p.hasPermission("cfour.admin.clean");
            if (p.hasPermission("cfour.admin.reload")) {
                p.sendMessage(ChatColor.YELLOW + "/cfour reload " + ChatColor.DARK_AQUA + messages.getMessage("help_admin_4"));
            }
            if (p.hasPermission("cfour.admin.messages")) {
                p.sendMessage(ChatColor.YELLOW + "/cfour messages <DE|EN|custom> " + ChatColor.DARK_AQUA + messages.getMessage("help_admin_11"));
            }
            if (p.hasPermission("cfour.admin.disable"))
            {
                p.sendMessage(ChatColor.YELLOW + "/cfour enable <game> " + ChatColor.DARK_AQUA + messages.getMessage("help_admin_7"));
                p.sendMessage(ChatColor.YELLOW + "/cfour disable <game> " + ChatColor.DARK_AQUA + messages.getMessage("help_admin_8"));
            }
        }
        p.sendMessage("");
    }

    private Location loc(String game)
    {
        double x = getConfig().getDouble("games." + game + ".spawn.x");
        double y = getConfig().getDouble("games." + game + ".spawn.y");
        double z = getConfig().getDouble("games." + game + ".spawn.z");
        float yaw = getConfig().getInt("games." + game + ".spawn.yaw");
        float pitch = getConfig().getInt("games." + game + ".spawn.pitch");
        String world = getConfig().getString("games." + game + ".spawn.world");
        Location loc = new Location(getServer().getWorld(world), x, y, z, yaw, pitch);
        return loc;
    }

    private void nextStep(Player p)
    {
        Utilities.msg(p, messages.getMessage("create_ok"));
    }





    private void loadList()
    {
        if (getConfig().get("games") == null)
        {
            this.list.clear();
            return;
        }
        this.list2 = getConfig().getConfigurationSection("games").getKeys(false);
        this.gameLocs.clear();
        this.gameWorlds.clear();
        this.list.clear();
        for (String game : this.list2) {
            if (getConfig().get("games." + game + ".enabled") == null)
            {
                getConfig().addDefault("games." + game + ".enabled", Boolean.TRUE);
                saveConfig();
            }
        }
        for (String game : this.list2) {
            if ((getConfig().getBoolean("games." + game + ".enabled")) && (getConfig().get("games." + game + ".display") != null) && (getConfig().get("games." + game + ".throwIn") != null) && (getConfig().get("games." + game + ".spawn") != null)) {
                this.list.add(game);
            }
        }
        for (String game : this.list)
        {
            HashMap<String, Integer> map = new HashMap();
            map.put("Y", getConfig().getInt("games." + game + ".display.y"));
            map.put("kX", getConfig().getInt("games." + game + ".display.kX"));
            map.put("gX", getConfig().getInt("games." + game + ".display.gX"));
            map.put("kZ", getConfig().getInt("games." + game + ".display.kZ"));
            map.put("gZ", getConfig().getInt("games." + game + ".display.gZ"));
            map.put("tY", getConfig().getInt("games." + game + ".throwIn.y"));
            map.put("tkX", getConfig().getInt("games." + game + ".throwIn.kX"));
            map.put("tgX", getConfig().getInt("games." + game + ".throwIn.gX"));
            map.put("tkZ", getConfig().getInt("games." + game + ".throwIn.kZ"));
            map.put("tgZ", getConfig().getInt("games." + game + ".throwIn.gZ"));
            this.gameLocs.put(game, map);
            this.gameWorlds.put(game, getConfig().getString("games." + game + ".spawn.world"));
            if (getConfig().get("games." + game + ".sand") == null)
            {
                getConfig().set("games." + game + ".sand", "SAND");
                getConfig().set("games." + game + ".gravel", "GRAVEL");
                saveConfig();
            }
            this.sand.put(game, getConfig().getString("games." + game + ".sand"));
            this.gravel.put(game, getConfig().getString("games." + game + ".gravel"));
        }
    }

    public final Map<String, Map<Integer, List<Integer>>> columns = new HashMap();
    public final Map<String, String> playerIsInGame = new HashMap();
    public final Map<String, Map<Integer, String>> playersInGame = new HashMap();
    public final Map<String, String> whoseTurnN = new HashMap();
    public final Map<String, Boolean> gameStarted = new HashMap();
    private final Map<String, ItemStack[]> invSave = new HashMap();
    private final Map<String, GameMode> gameModeSave = new HashMap();
    public final Map<String, String> playersMaterial = new HashMap();
    public final Map<String, Integer> playersBlocks = new HashMap();

    private void joinGame(Player p, String game)
    {
        if (!this.playersInGame.containsKey(game)) {
            this.playersInGame.put(game, new HashMap());
        }
        Map<Integer, String> playersInTheGame = this.playersInGame.get(game);
        for (String games : this.list) {
            if ((this.playersInGame.containsKey(games)) &&
                    (this.playersInGame.get(games).containsValue(p.getName().toLowerCase())))
            {
                Utilities.msg(p, messages.getMessage("join1"));
                return;
            }
        }
        if ((playersInTheGame.containsKey(1)) && (playersInTheGame.containsKey(2)))
        {
            Utilities.msg(p, messages.getMessage("join2", ChatColor.ITALIC + "/cfour tp <game>" + ChatColor.RESET + ChatColor.YELLOW));
            return;
        }
        this.beforeJoinLocs.put(p.getName().toLowerCase(), p.getLocation());
        if (playersInTheGame.containsKey(1))
        {
            p.teleport(loc(game));
            playersInTheGame.put(2, p.getName().toLowerCase());
            this.playerIsInGame.put(p.getName().toLowerCase(), game);
            startGame(game, getServer().getPlayer(playersInTheGame.get(1)), getServer().getPlayer(playersInTheGame.get(2)));
            return;
        }
        if (playersInTheGame.containsKey(2))
        {
            p.teleport(loc(game));
            playersInTheGame.put(1, p.getName().toLowerCase());
            this.playerIsInGame.put(p.getName().toLowerCase(), game);
            startGame(game, getServer().getPlayer(playersInTheGame.get(1)), getServer().getPlayer(playersInTheGame.get(2)));
            return;
        }
        p.teleport(loc(game));
        playersInTheGame.put(1, p.getName().toLowerCase());
        this.playerIsInGame.put(p.getName().toLowerCase(), game);
        Utilities.msg(p, messages.getMessage("join3"));
    }

    private void startGame(String game, Player p1, Player p2)
    {
        int starter = getRandom(2);
        int color = getRandom(2);
        this.gameModeSave.put(p1.getName().toLowerCase(), p1.getGameMode());
        this.gameModeSave.put(p2.getName().toLowerCase(), p2.getGameMode());
        p1.setGameMode(GameMode.SURVIVAL);
        p2.setGameMode(GameMode.SURVIVAL);
        changeInventory(p1, 1, color, game);
        changeInventory(p2, 2, color, game);
        this.playersBlocks.put(p1.getName().toLowerCase(), 21);
        this.playersBlocks.put(p2.getName().toLowerCase(), 21);
        blockEvents.isPlacingBlocked.put(game, Boolean.FALSE);
        cleanDisplay(game);
        this.gameStarted.put(game, Boolean.TRUE);
        Utilities.msg(p1, messages.getMessage("start1", p2.getName()));
        Utilities.msg(p2, messages.getMessage("start1", p1.getName()));
        if (starter == 0)
        {
            this.whoseTurnN.put(game, p1.getName().toLowerCase());
            sendMsg(game, "beide", prefix + messages.getMessage("start2", p1.getName()));
        }
        else if (starter == 1)
        {
            this.whoseTurnN.put(game, p2.getName().toLowerCase());
            sendMsg(game, "beide", prefix + messages.getMessage("start2", p2.getName()));
        }
    }

    private void changeInventory(Player p, int i, int r, String game)
    {
        if (i == 1) {
            if (r == 0)
            {
                this.playersMaterial.put(p.getName().toLowerCase(), this.sand.get(game));
                saveInv(p, this.sand.get(game));
            }
            else if (r == 1)
            {
                this.playersMaterial.put(p.getName().toLowerCase(), this.gravel.get(game));
                saveInv(p, this.gravel.get(game));
            }
        }
        if (i == 2) {
            if (r == 0)
            {
                this.playersMaterial.put(p.getName().toLowerCase(), this.gravel.get(game));
                saveInv(p, this.gravel.get(game));
            }
            else if (r == 1)
            {
                this.playersMaterial.put(p.getName().toLowerCase(), this.sand.get(game));
                saveInv(p, this.sand.get(game));
            }
        }
    }

    private void returnInv(Player p)
    {
        ItemStack[] inv = this.invSave.get(p.getName().toLowerCase());
        p.getInventory().setContents(inv);
        p.updateInventory();
    }

    public void sendMsg(String game, String wem, String text)
    {
        Player p1 = getServer().getPlayer((String)((Map)this.playersInGame.get(game)).get(1));
        Player p2 = getServer().getPlayer((String)((Map)this.playersInGame.get(game)).get(2));
        String msg1 = text.replace("{GEGNER}", p2.getName()).replace("{p1}", p1.getName()).replace("{p2}", p2.getName());
        String msg2 = text.replace("{GEGNER}", p1.getName()).replace("{p1}", p1.getName()).replace("{p2}", p2.getName());
        if (wem.equalsIgnoreCase("beide"))
        {
            p1.sendMessage(msg1);
            p2.sendMessage(msg2);
            return;
        }
        if (wem.equalsIgnoreCase("p1"))
        {
            p1.sendMessage(msg1);
            return;
        }
        if (wem.equalsIgnoreCase("p2"))
        {
            p2.sendMessage(msg2);
            return;
        }
    }

    private void saveInv(Player p, String material)
    {
        this.invSave.put(p.getName().toLowerCase(), p.getInventory().getContents());
        p.getInventory().clear();
        ItemStack itemStack = Utilities.getItem(material,21,0);
        p.getInventory().setItem(0, itemStack);
        p.updateInventory();
    }

    private int getRandom(int i)
    {
        int r = new Random().nextInt(i);
        return r;
    }


    private Map<Integer, Integer> whichIsBigger(int i1, int i2)
    {
        Map<Integer, Integer> resultat = new HashMap();
        if (i1 < i2)
        {
            resultat.put(1, i1);
            resultat.put(2, i2);
        }
        else if (i2 < i1)
        {
            resultat.put(1, i2);
            resultat.put(2, i1);
        }
        else if (i1 == i2)
        {
            resultat.put(1, i1);
            resultat.put(2, i1);
        }
        return resultat;
    }

    public void stopGame(String game)
    {
        Player p1 = getServer().getPlayer((String)((Map)this.playersInGame.get(game)).get(1));
        Player p2 = getServer().getPlayer((String)((Map)this.playersInGame.get(game)).get(2));
        returnInv(p1);
        returnInv(p2);
        p1.setGameMode(this.gameModeSave.get(p1.getName().toLowerCase()));
        p2.setGameMode(this.gameModeSave.get(p2.getName().toLowerCase()));
        this.playersMaterial.remove(p1.getName().toLowerCase());
        this.playersMaterial.remove(p2.getName().toLowerCase());
        this.columns.remove(game);
        this.whoseTurnN.remove(game);
        this.gameStarted.remove(game);
        this.playerIsInGame.remove(p1.getName().toLowerCase());
        this.playerIsInGame.remove(p2.getName().toLowerCase());
        this.playersInGame.remove(game);
        if (this.enableTeleport)
        {
            p1.teleport(this.beforeJoinLocs.get(p1.getName().toLowerCase()));
            p2.teleport(this.beforeJoinLocs.get(p2.getName().toLowerCase()));
        }
        this.beforeJoinLocs.remove(p1.getName().toLowerCase());
        this.beforeJoinLocs.remove(p2.getName().toLowerCase());
        if (this.enableAutoClear) {
            cleanDisplay(game);
        }
    }
    private boolean cleanDisplay(String game)
    {
        Map<String, Integer> map = this.gameLocs.get(game);
        String world = this.gameWorlds.get(game);
        int y = map.get("Y");
        int kX = map.get("kX");
        int gX = map.get("gX");
        int kZ = map.get("kZ");
        int gZ = map.get("gZ");
        for (int Xo = kX; Xo <= gX; Xo++) {
            for (int Yo = y; Yo >= y - 6; Yo--) {
                for (int Zo = kZ; Zo <= gZ; Zo++)
                {
                    Block blockToChange = getServer().getWorld(world).getBlockAt(Xo, Yo, Zo);
                    blockToChange.setTypeId(0);
                }
            }
        }
        return false;
    }


    private void loadLanguage()
    {
        if (getConfig().get("language") != null){
            String loc =  getConfig().getString("language");
            Locale locale = new Locale(loc);
            messages = new Messages(this,locale);
        }else{
            messages = new Messages(this, Locale.getDefault());
        }
    }

    private void loadConfig()
    {
        getConfig().addDefault("language", "EN");
        getConfig().addDefault("enable-*itsYourTurn*-spam", Boolean.TRUE);
        getConfig().addDefault("teleportOnGameEnd", Boolean.TRUE);
        getConfig().addDefault("clearTheDisplayOnGameEnd", Boolean.FALSE);
        getConfig().addDefault("enableJoinSigns", Boolean.TRUE);
        getConfig().addDefault("sameWorldToJoin", Boolean.FALSE);
        getConfig().addDefault("checkMaxDistaceToGameSpawnOnJoin", Boolean.FALSE);
        getConfig().addDefault("maxDistanceToGameSpawn", 20);
        getConfig().addDefault("use_MySql", Boolean.FALSE);
        getConfig().addDefault("mysql_host", "localhost");
        getConfig().addDefault("mysql_port", "3306");
        getConfig().addDefault("mysql_database", "database");
        getConfig().addDefault("mysql_username", "username");
        getConfig().addDefault("mysql_password", "12345");
        getConfig().options().copyDefaults(true);
        saveConfig();
        if (getConfig().get("sandId") != null)
        {
            getConfig().set("sandId", null);
            getConfig().set("gravelId", null);
            saveConfig();
        }
        this.enableSpam = getConfig().getBoolean("enable-*itsYourTurn*-spam");
        this.enableTeleport = getConfig().getBoolean("teleportOnGameEnd");
        this.enableAutoClear = getConfig().getBoolean("clearTheDisplayOnGameEnd");
        this.enableJoinSigns = getConfig().getBoolean("enableJoinSigns");
        this.enableSameWorld = getConfig().getBoolean("sameWorldToJoin");
        this.enableMaxDistance = getConfig().getBoolean("checkMaxDistaceToGameSpawnOnJoin");
        this.maxDistance = getConfig().getInt("maxDistanceToGameSpawn");
    }



    private String getPercent(double hundred, double what)
    {
        double hundredth = hundred / 100.0D;
        double percent = what / hundredth;
        String s = String.format("%.1f", percent);
        return s;
    }

    private boolean setupEconomy()
    {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Statistics getSt(){
        return st;
    }
}
