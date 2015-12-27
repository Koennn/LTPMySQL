package me.koenn.ltpms;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static net.md_5.bungee.api.ChatColor.translateAlternateColorCodes;

public class Main extends Plugin implements Listener {

    static String url = "jdbc:mysql://127.0.0.1:3306/losttimepark";
    static String user = "Proxy";
    static String password = "WVT2ZjVUjiauoB4Xd";

    String bcFormat = translateAlternateColorCodes('&', "&cGlobal &8&l>> &7");

    public void log(String msg){
        ProxyServer.getInstance().getLogger().info("[LTPMySQL] " + msg);
    }

    public static void sqlLog(String msg){
        ProxyServer.getInstance().getLogger().info("[LTPMySQL] [SQL] " + msg);
    }

    @Override
    public void onEnable(){
        log("All credits for this plugin go to Koenn");
        Connection con = openConnection();
        log("Database initialized!");
        closeConnection(con);
        ProxyServer.getInstance().getPluginManager().registerListener(this, this);
    }

    @Override
    public void onDisable(){
        log("All credits for this plugin go to Koenn");
    }

    public void globalBroadcast(String message){
        ProxyServer ps = ProxyServer.getInstance();
        for(ProxiedPlayer p : ps.getPlayers()){
            p.sendMessage(new TextComponent(bcFormat + message));
        }
    }

    public Integer getTokens(String uuid){
        Connection con = openConnection();
        Integer tokens = getTokens(uuid, con);
        closeConnection(con);
        return tokens;
    }

    public void addTokens(String uuid, Integer amount){
        Connection con = openConnection();
        addTokens(uuid, amount, con);
    }

    public synchronized static Connection openConnection(){
        Connection con;
        try{
            con = DriverManager.getConnection(url, user, password);
            sqlLog("Connected to database.");
            return con;
        }catch (Exception ex){
            sqlLog("Error while connecting to database:");
            ProxyServer.getInstance().getLogger().severe(ex.getMessage());
            return null;
        }
    }

    public synchronized static void closeConnection(Connection con){
        try{
            if(con != null){
                con.close();
                sqlLog("Connection closed.");
            }
            sqlLog("Connection was already closed.");
        }catch (Exception ex){
            sqlLog("Error while closing connection:");
            ProxyServer.getInstance().getLogger().severe(ex.getMessage());
        }
    }

    public synchronized static Integer getTokens(String uuid, Connection con){
        try{
            sqlLog("Getting information from database...");
            PreparedStatement ps = con.prepareStatement("SELECT tokens FROM tokens WHERE uuid=?;");
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            sqlLog("Complete.");
            rs.next();
            return rs.getInt("tokens");
        }catch (Exception ex) {
            sqlLog("Error while getting information from database:");
            ProxyServer.getInstance().getLogger().severe(ex.getMessage());
            return null;
        }
    }

    public synchronized void addTokens(String uuid, Integer amount, Connection con){
        try{
            sqlLog("Getting information from database...");
            PreparedStatement ps = con.prepareStatement("SELECT tokens FROM tokens WHERE uuid=?;");
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            sqlLog("Complete.");
            if(!rs.next()){
                sqlLog("Inserting information into database...");
                ps = con.prepareStatement("INSERT INTO tokens (uuid, tokens) VALUES (?, ?);");
                ps.setString(1, uuid);
                ps.setInt(2, amount);
                ps.execute();
                sqlLog("Complete.");
            } else {
                Integer res = rs.getInt("tokens");
                sqlLog("Updating information in database...");
                ps = con.prepareStatement("UPDATE Tokens SET tokens=? WHERE Tokens.uuid = ?;");
                ps.setInt(1, res + amount);
                ps.setString(2, uuid);
                ps.execute();
                sqlLog("Complete.");
            }
        }catch (Exception ex) {
            sqlLog("Error while writing to database:");
            ProxyServer.getInstance().getLogger().severe(ex.getMessage());
        }
    }

}
