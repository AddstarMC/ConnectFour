package ch.dragon252525.connectFour;

import java.sql.*;

class MySQL extends Database {
    private String hostname = "";
    private String portnmbr = "";
    private String username = "";
    private String password = "";
    private String database = "";

    public MySQL(String hostname, String portnmbr, String database, String username, String password) {
        this.hostname = hostname;
        this.portnmbr = portnmbr;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public Connection open() {
        String url;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            url = "jdbc:mysql://" + this.hostname + ":" + this.portnmbr + "/" + this.database;
            this.connection = DriverManager.getConnection(url, this.username, this.password);
            return this.connection;
        } catch (SQLException e) {
            System.out.print("Could not connect to MySQL server!");
        } catch (ClassNotFoundException e) {
            System.out.print("JDBC Driver not found!");
        }
        return null;
    }

    public void close() {
        try {
            if (this.connection != null) {
                this.connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public boolean checkConnection() {
        return this.connection != null;
    }

    public ResultSet query(String query)
            throws SQLException {
        Statement statement = null;
        ResultSet result = null;
        try {
            statement = this.connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            if (e.getMessage().equals("Can not issue data manipulation statements with executeQuery().")) {
                try {
                    statement.executeUpdate(query);
                } catch (SQLException ex) {
                    if (e.getMessage().startsWith("You have an error in your SQL syntax;")) {
                        String temp = e.getMessage().split(";")[0].substring(0, 36) + e.getMessage().split(";")[1].substring(91);
                        temp = temp.substring(0, temp.lastIndexOf("'"));
                        throw new SQLException(temp);
                    }
                    ex.printStackTrace();
                }
            } else {
                if (e.getMessage().startsWith("You have an error in your SQL syntax;")) {
                    String temp = e.getMessage().split(";")[0].substring(0, 36) + e.getMessage().split(";")[1].substring(91);
                    temp = temp.substring(0, temp.lastIndexOf("'"));
                    throw new SQLException(temp);
                }
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean clearTable(String table) {
        Statement statement = null;
        String query;
        try {
            statement = this.connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM " + table);
            if (result == null) {
                return false;
            }
            query = "DELETE FROM " + table;
            statement.executeUpdate(query);
            return true;
        } catch (SQLException e) {
        }
        return false;
    }

    public boolean createTable(String table) {
        try {
            PreparedStatement ps = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS cfour_playerStats(id int(11) NOT NULL AUTO_INCREMENT,gameName varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,winner varchar(30) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,loser varchar(30) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,PRIMARY KEY (id)) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean removeRow(String table, int id) {
        try {
            PreparedStatement ps = this.connection.prepareStatement("DELETE FROM " + table + " WHERE id=" + id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insert(String table, String[] column, String[] value) {
        Statement statement;
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        String[] arrayOfString;
        int j = (arrayOfString = column).length;
        for (int i = 0; i < j; i++) {
            String s = arrayOfString[i];
            sb1.append(s + ",");
        }
        j = (arrayOfString = value).length;
        int i;
        for (i = 0; i < j; i++) {
            String s = arrayOfString[i];
            sb2.append("'" + s + "',");
        }
        String columns = sb1.toString().substring(0, sb1.toString().length() - 1);
        String values = sb2.toString().substring(0, sb2.toString().length() - 1);
        try {
            statement = this.connection.createStatement();
            statement.execute("INSERT INTO " + table + "(" + columns + ") VALUES (" + values + ")");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteTable(String table) {
        Statement statement = null;
        try {
            if ((table.equals("")) || (table == null)) {
                return true;
            }
            statement = this.connection.createStatement();
            statement.executeUpdate("DROP TABLE " + table);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
