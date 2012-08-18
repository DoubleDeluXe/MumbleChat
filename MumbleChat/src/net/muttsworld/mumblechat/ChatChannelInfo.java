package net.muttsworld.mumblechat;

import java.util.List;
import java.util.regex.Pattern;

import net.muttsworld.mumblechat.sql.SqlCommands;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class ChatChannelInfo {

    MumbleChat plugin;
    // String[] Filters;
    List<String> filters;
    List<String> filterexceptions;
    //List<chatChannel> cc;
    ChatChannel[] cc;
    public String mutepermissions;
    public Boolean saveplayerdata;
    public Boolean usePexPrefix;
    public Boolean usePexSuffix;
    
    //////////////////////////////
    // For SQL Connection
    public Boolean useSQL;
    public String hostname;
    public String port;
    public String username;
    public String password;
    public SqlCommands sqlcom;
    ////////////////////////////////
        
    public String defaultChannel; //There can be only one :)

    @SuppressWarnings("unchecked")
    ChatChannelInfo(MumbleChat _plugin) {

        plugin = _plugin;
        filters = (List<String>) plugin.getConfig().getList("filters");
        filterexceptions = (List<String>) plugin.getConfig().getList("filterexceptions");

        String _color = "";
        String _name = "";
        String _permission = "";
        Boolean _muteable = false;
        Boolean _filter = false;
        Boolean _defaultchannel = false;
        String _alias = "";
        Double _distance = (double) 0;
        ConfigurationSection cs = plugin.getConfig().getConfigurationSection("channels");


        mutepermissions = plugin.getConfig().getString("mute.permissions","");
        saveplayerdata = plugin.getConfig().getBoolean("saveplayerdata", true);
        usePexPrefix = plugin.getConfig().getBoolean("usePexPrefix", false);
        usePexSuffix = plugin.getConfig().getBoolean("usePexPrefix", false);
        
        ///////////////////////////////
        // SQL Configuration
        ///////////////////////////////
        useSQL = plugin.getConfig().getBoolean("useSQL", false);
        if(useSQL)
        {
        	  hostname = plugin.getConfig().getString("SqlConfig.hostname","");
        	  port =  plugin.getConfig().getString("SqlConfig.port","");
        	  username =  plugin.getConfig().getString("SqlConfig.username","");
        	  password =  plugin.getConfig().getString("SqlConfig.password","");
        	  
        	
  	        sqlcom = new SqlCommands(plugin,this);
  	        if ( sqlcom.DatabaseSetup() )
  	        {
  	        	plugin.getLogger().info("MumbleChat Database Setup Complete.");
  	        	
  	        }
        }

        int len = (cs.getKeys(false)).size();
        cc = new ChatChannel[len];

        int x = 0;
        //Colors for the Channels
        for (String key : cs.getKeys(false)) {
            //	plugin.getServer().getLogger().info(key + ":" + (String)cs.getString(key+".color"));

            _color = (String) cs.getString(key + ".color", "white");
            //plugin.getServer().getLogger().info("Got Color:" + _color);

            //Check for valid color... Default it to white if wrong.
            Boolean bFound = false;
            for (ChatColor bkColors : ChatColor.values()) {
                //plugin.getServer().getLogger().info(_color+" : "+bkColors.name());
                if (_color.equalsIgnoreCase(bkColors.name())) {
                    bFound = true;
                }
            }
            if (bFound == false) {
                plugin.getServer().getLogger().info("[" + plugin.getName() + "] " + _color + "is not valid. Changing to white.");
                _color = "white";

            }

            _name = key;
            //plugin.getServer().getLogger().info("Got name:" +key);

            _permission = (String) cs.getString(key + ".permissions", "None");
            //plugin.getServer().getLogger().info("Got permission:" + _permission);

            _muteable = (Boolean) cs.getBoolean(key + ".muteable", false);
            //plugin.getServer().getLogger().info("Got muteable:" + _muteable);

            _filter = (Boolean) cs.getBoolean(key + ".filter", true);

            _defaultchannel = (Boolean) cs.getBoolean(key + ".default", false);
            if (_defaultchannel == true)
            	defaultChannel = _name;
            //plugin.getServer().getLogger().info("Got defaultchannel:" + _defaultchannel);

            _alias = (String) cs.getString(key + ".alias", "None");

            _distance = (Double) cs.getDouble(key + ".distance", (double) 0);

            ChatChannel c =
                    new ChatChannel(_name, _color, _permission, _muteable, _filter, _defaultchannel, _alias, _distance);

            //	plugin.getServer().getLogger().info("new channel:" + c.getName());

            cc[x++] = c;
            //cc.add((chatChannel)c);

            //plugin.getServer().getLogger().info("saved channel:" + cc.length);
        }

        //plugin.getServer().getLogger().info("print list");
        //logChannelList();
    }

    int getChannelCount() {
        return cc.length;
    }

    void logChannelList() {
        for (ChatChannel p : cc) {
            plugin.getServer().getLogger().info("[" + plugin.getName() + "]" + p.getName() + ":" + p.getColor() + ":" + p.getPermission() + ":" + p.isMuteable() + ":" + p.isFiltered() + ":" + p.isDefaultchannel());
        }
    }

    public ChatChannel[] getChannelsInfo() {
        return cc;
    }

    public ChatChannel getChannelInfo(String ChannelName) {
        for (ChatChannel c : cc) {
            if (c.getName().equalsIgnoreCase(ChannelName)) {
                return c;
            }
        }

        return null;
    }
    
    //This will fix the color on a player's name with PEX format.
    // it has to be put into the SetFormat method on the chat event.
    protected static Pattern chatColorPattern = Pattern.compile("(?i)&([0-9A-F])");
    
    public String FormatPlayerName(String playerPrefix,String playerDisplayName,String playerSuffix)
    {
    	 if (usePexPrefix) {

            playerPrefix = chatColorPattern.matcher(playerPrefix).replaceAll("\u00A7$1");
         }
    	 if (usePexSuffix) {

             playerSuffix = chatColorPattern.matcher(playerSuffix).replaceAll("\u00A7$1");
         }
    	 return playerPrefix+playerDisplayName.trim()+playerSuffix;

    }

    List<String> getFilters() {
        return filters;
    }
}
