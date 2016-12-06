package ch.dragon252525.connectFour;

public class Statistics
{
    private final ConnectFour c4;
    private boolean useMySql = false;
    private final ConfigAccessor cfg;

    public Statistics(ConnectFour plugin)
    {
        this.c4 = plugin;
        this.cfg = new ConfigAccessor(this.c4, "statistics.dat");
    }

    private int playedGames = 0;
    private int winsSand = 0;
    private int winsGravel = 0;
    private int drawns = 0;
    private int wins7_14 = 0;
    private int wins15_22 = 0;
    private int wins23_30 = 0;
    private int wins31_38 = 0;
    private int wins39_42 = 0;
    private MySQL mysql;

    public int getPlayedGames()
    {
        return this.playedGames;
    }

    public int getWinsSand()
    {
        return this.winsSand;
    }

    public int getWinsGravel()
    {
        return this.winsGravel;
    }

    public int getDrawns()
    {
        return this.drawns;
    }

    public int getWins7_14()
    {
        return this.wins7_14;
    }

    public int getWins15_22()
    {
        return this.wins15_22;
    }

    public int getWins23_30()
    {
        return this.wins23_30;
    }

    public int getWins31_38()
    {
        return this.wins31_38;
    }

    public int getWins39_42()
    {
        return this.wins39_42;
    }

    public void addPlayedGame()
    {
        this.playedGames += 1;
    }

    public int addWinSand()
    {
        return this.winsSand++;
    }

    public int addWinGravel()
    {
        return this.winsGravel++;
    }

    public int addDrawn()
    {
        return this.drawns++;
    }

    public int addWin7_14()
    {
        return this.wins7_14++;
    }

    public int addWin15_22()
    {
        return this.wins15_22++;
    }

    public int addWin23_30()
    {
        return this.wins23_30++;
    }

    public int addWin31_38()
    {
        return this.wins31_38++;
    }

    public int addWin39_42()
    {
        return this.wins39_42++;
    }

    public void load()
    {
        configDefaults();
        this.playedGames = this.cfg.getConfig().getInt("playedGames");
        this.winsSand = this.cfg.getConfig().getInt("winsSand", this.winsSand);
        this.winsGravel = this.cfg.getConfig().getInt("winsGravel", this.winsGravel);
        this.drawns = this.cfg.getConfig().getInt("drawns", this.drawns);
        this.wins7_14 = this.cfg.getConfig().getInt("wins7_14", this.wins7_14);
        this.wins15_22 = this.cfg.getConfig().getInt("wins15_22", this.wins15_22);
        this.wins23_30 = this.cfg.getConfig().getInt("wins23_30", this.wins23_30);
        this.wins31_38 = this.cfg.getConfig().getInt("wins31_38", this.wins31_38);
        this.wins39_42 = this.cfg.getConfig().getInt("wins39_42", this.wins39_42);
    }

    public void loadMySqlData()
    {
        if (this.c4.getConfig().getBoolean("use_MySql"))
        {
            String password = "";
            String username = "";
            String database = "";
            String port = "";
            String host = "";
            try
            {
                host = this.c4.getConfig().getString("mysql_host");
                port = this.c4.getConfig().getString("mysql_port");
                database = this.c4.getConfig().getString("mysql_database");
                username = this.c4.getConfig().getString("mysql_username");
                password = this.c4.getConfig().getString("mysql_password");
            }
            catch (Exception e)
            {
                System.out.println("[ConnectFour] Error while loading MySql data. Please check config.yml");
                return;
            }
            try
            {
                this.mysql = new MySQL(host, port, database, username, password);
                this.mysql.open();
                this.mysql.close();
            }
            catch (Exception e)
            {
                System.out.println("[ConnectFour] Error while connecting to the MySql table. Please check config.yml");
                return;
            }
            this.mysql.open();
            this.mysql.createTable("cfour_playerStats");
            this.mysql.close();
            this.useMySql = true;
        }
        else
        {
            this.useMySql = false;
        }
    }

    public void submitPlayedGame(String gameName, String winner, String loser)
    {
        if (this.useMySql)
        {
            this.mysql.open();
            String[] columns = { "gameName", "winner", "loser" };
            String[] values = { gameName, winner, loser };
            this.mysql.insert("cfour_playerStats", columns, values);
            this.mysql.close();
        }
    }

    public void save()
    {
        this.cfg.getConfig().set("playedGames", this.playedGames);
        this.cfg.getConfig().set("winsSand", this.winsSand);
        this.cfg.getConfig().set("winsGravel", this.winsGravel);
        this.cfg.getConfig().set("drawns", this.drawns);
        this.cfg.getConfig().set("wins7_14", this.wins7_14);
        this.cfg.getConfig().set("wins15_22", this.wins15_22);
        this.cfg.getConfig().set("wins23_30", this.wins23_30);
        this.cfg.getConfig().set("wins31_38", this.wins31_38);
        this.cfg.getConfig().set("wins39_42", this.wins39_42);
        this.cfg.saveConfig();
    }

    private void configDefaults()
    {
        this.cfg.getConfig().options().header("DO NOT EDIT THIS FILE! IT WILL CAUSE A GLOBAL DISCREPANCY!");
        this.cfg.getConfig().addDefault("playedGames", 0);
        this.cfg.getConfig().addDefault("winsSand", 0);
        this.cfg.getConfig().addDefault("winsGravel", 0);
        this.cfg.getConfig().addDefault("drawns", 0);
        this.cfg.getConfig().addDefault("wins7_14", 0);
        this.cfg.getConfig().addDefault("wins15_22", 0);
        this.cfg.getConfig().addDefault("wins23_30", 0);
        this.cfg.getConfig().addDefault("wins31_38", 0);
        this.cfg.getConfig().addDefault("wins39_42", 0);
        this.cfg.getConfig().options().copyDefaults(true);
        this.cfg.saveConfig();
    }
}
