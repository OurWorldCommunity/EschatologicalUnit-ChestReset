package online.smyhw.EschatologicalUnit.ChestReset;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import jdk.nashorn.internal.runtime.regexp.joni.Config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public class smyhw extends JavaPlugin implements Listener 
{
	public static Plugin smyhw_;
	public static Logger loger;
	public static FileConfiguration configer;
	public static String prefix;
	@Override
    public void onEnable() 
	{
		getLogger().info("EschatologicalUnit.ChestReset加载");
		getLogger().info("正在加载环境...");
		loger=getLogger();
		configer = getConfig();
		smyhw_=this;
		getLogger().info("正在加载配置...");
		saveDefaultConfig();
		prefix = configer.getString("config.prefix");
		getLogger().info("正在注册监听器...");
		Bukkit.getPluginManager().registerEvents(this,this);
		getLogger().info("EschatologicalUnit.ChestReset加载完成");
    }

	@Override
    public void onDisable() 
	{
		getLogger().info("EschatologicalUnit.ChestReset卸载");
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
        if (cmd.getName().equals("euCr"))
        {
                if(!sender.hasPermission("eu.plugin")) 
                {
                	sender.sendMessage(prefix+"非法使用 | 使用者信息已记录，此事将被上报");
                	loger.warning(prefix+"使用者<"+sender.getName()+">试图非法使用指令<"+args+">{权限不足}");
                	return true;
                }
                switch(args[0])
                {
                case "reset":
                {
                	if(args.length > 1) 
                	{
                		reset(args[1]);
                		sender.sendMessage(prefix+"已成功尝试还原方块<"+args[1]+">");
                		return true;
                	}
                	Set<String> temp1 = configer.getConfigurationSection("data").getKeys(false);
                	for(String temp2:temp1)
                	{
                		reset(temp2);
                		sender.sendMessage(prefix+"已成功尝试还原方块<"+temp2+">");
                	}
                	return true;
                }
                case "set":
                {
                	if(args.length<4) {CSBZ(sender);return true;}
                	if(args.length>4)
                	for(int i=4;i<args.length;i++)
                	{
                		switch(args[i])
                		{
                		case "-f":
                		{//覆盖之前的相同坐标箱子
                        	int x = Integer.parseInt(args[1]);
                        	int y = Integer.parseInt(args[2]);
                        	int z = Integer.parseInt(args[3]);
                        	Set<String> temp1 = configer.getConfigurationSection("data").getKeys(false);
                        	for(String temp2:temp1)
                        	{
                            	int cx = configer.getInt("data."+temp2+".x");
                            	int cy = configer.getInt("data."+temp2+".y");
                            	int cz = configer.getInt("data."+temp2+".z");
                            	if(cx==x && cy==y && cz==z)
                            	{
                            		configer.set("data."+temp2, null);
                            		sender.sendMessage(prefix+"<-f>删除原配置<"+temp2+">");	
                            	}
                        	}
                        	continue;
                		}
                		default:
                		{
                			sender.sendMessage(prefix+"未知的选项<"+args[i]+">");
                			continue;
                		}
                		}
                	}
                	int x = Integer.parseInt(args[1]);
                	int y = Integer.parseInt(args[2]);
                	int z = Integer.parseInt(args[3]);
                	Block block = ((Player) sender).getWorld().getBlockAt(x, y, z);
                	if(block.getType()!=org.bukkit.Material.CHEST)
                	{//检查方块类型
                		sender.sendMessage(prefix+"该方块不是箱子");
                		return true;
                	}
                	//查找没有被占用的ID
                	int dataID = 0;
                	for(;configer.get("data."+dataID+".inv")!=null;dataID++);
                	//保存物品
                	ItemStack[] inv = ((Chest) block.getState()).getBlockInventory().getContents();
                	configer.set("data."+dataID+".inv",inv);
                	//保存世界/坐标
                	configer.set("data."+dataID+".x",block.getX());
                	configer.set("data."+dataID+".y",block.getY());
                	configer.set("data."+dataID+".z",block.getZ());
                	configer.set("data."+dataID+".world",block.getWorld().getName());
                	SreloadConfig();
                	sender.sendMessage(prefix+"已成功尝试保存方块<"+dataID+">");
                	return true;
                	
                }
                case "check":
                {
                	sender.sendMessage(prefix+" ======ChestRestCheck======");
                	sender.sendMessage(prefix+"---------有效性检查---------");
                	{
	                	Set<String> temp1 = configer.getConfigurationSection("data").getKeys(false);
	                	for(String temp2:temp1)
	                	{
	                    	int cx = configer.getInt("data."+temp2+".x");
	                    	int cy = configer.getInt("data."+temp2+".y");
	                    	int cz = configer.getInt("data."+temp2+".z");
	                    	World world = Bukkit.getWorld(configer.getString("data."+temp2+".world"));
	                		if(world.getBlockAt(cx, cy, cz).getType()!=Material.CHEST)
	                    	{
	                    		sender.sendMessage(prefix+"[+]坐标方块不是箱子{ID=<"+temp2+">;World=<"+world.getName()+">;X=<"+cx+">;Y=<"+cy+">;Z=<"+cz+">}");	
	                    	}
	                	}
                	}
                	sender.sendMessage(prefix+"---------重复性检查---------");
                	{
	                	//已经被判定为重复的Set
	                	Set<String> haveRe = new HashSet<String>();
	                	Set<String> temp1 = configer.getConfigurationSection("data").getKeys(false);
	                	for(String temp2:temp1)
	                	{
	                		if(haveRe.contains(temp2)) {continue;}
	                    	int cx = configer.getInt("data."+temp2+".x");
	                    	int cy = configer.getInt("data."+temp2+".y");
	                    	int cz = configer.getInt("data."+temp2+".z");
	                    	String world = configer.getString("data."+temp2+".world");
	                    	Set<String> temp3 = configer.getConfigurationSection("data").getKeys(false);
	                    	boolean title = false;
	                    	for(String temp4:temp3)
	                    	{
	                        	int ccx = configer.getInt("data."+temp4+".x");
	                        	int ccy = configer.getInt("data."+temp4+".y");
	                        	int ccz = configer.getInt("data."+temp4+".z");
	                        	String cworld = configer.getString("data."+temp4+".world");
	                        	if(cworld.equals(world) && ccx==cx && ccy==cy && ccz==cz && !(temp4.equals(temp2)))
	                        	{
	                        		if(!title) 
	                        		{
	                        			sender.sendMessage("重复组{World=<"+world+">;X=<"+cx+">;Y=<"+cy+">;Z=<"+cz+">}");
	                        			title = true;
	                        		}
	                        		sender.sendMessage(prefix+"[+]重复{ID=<"+temp2+">;World=<"+world+">;X=<"+cx+">;Y=<"+cy+">;Z=<"+cz+">}");	
	                        		haveRe.add(temp4);
	                        	}
	                    	}
	                	}
                	}
                	//处理参数
                	if(args.length>1)
                	{
                		for(int i=1;i<args.length;i++)
                		{
                			switch(args[i])
                			{
                			case "sid":
                			{//理顺ID
                				sender.sendMessage(prefix+"=========附加任务:理顺ID=========");
                				Set<String> temp1 = configer.getConfigurationSection("data").getKeys(false);
                				//临时箱子Set，所有箱子配置的副本
            					Set<ConfigurationSection> tempConfigS = new HashSet<ConfigurationSection>();
                				for(String temp2:temp1)
                				{
                					ConfigurationSection tempConfig = configer.getConfigurationSection("data."+temp2);
                					sender.sendMessage(prefix+"[+]提取ID=<"+temp2+">");
                					tempConfigS.add(tempConfig);
                				}
                				//清空配置文件
                				configer.set("data", null);
                				//遍历提取出来的副本，一个个放回配置文件
                				int num = 0;
                				for(ConfigurationSection temp2:tempConfigS)
                				{
                					configer.set("data."+num, temp2);
                					sender.sendMessage(prefix+"[+]回置ID=<"+num+">");
                					num++;
                				}
                				SreloadConfig();
                				sender.sendMessage(prefix+"=========附加任务:理顺ID:完成=========");
                				continue;
                			}
                			default:
                			{
                				sender.sendMessage(prefix+"未知的参数<"+args[i]+">");
                				continue;
                			}
                			}
                		}
                	}
                	return true;
                }
                case "find":
                {//寻找指定范围内的箱子
                	if(args.length<2) {CSBZ(sender);}
                	int r = Integer.parseInt(args[1]);
                	int x = ((Player) sender).getLocation().getBlockX();
                	int y = ((Player) sender).getLocation().getBlockY();
                	int z = ((Player) sender).getLocation().getBlockZ();
                	Set<String> temp1 = configer.getConfigurationSection("data").getKeys(false);
                	for(String temp2:temp1)
                	{
                    	int cx = configer.getInt("data."+temp2+".x");
                    	int cy = configer.getInt("data."+temp2+".y");
                    	int cz = configer.getInt("data."+temp2+".z");
                    	if( ( ( (x+r) > cx) && (cx > (x-r) ) ) && ( ( (y+r) > cy) && (cy > (y-r) ) ) && ( ( (z+r) > cz) && (cz > (z-r) ) ) )
                    	{
                    		sender.sendMessage(prefix+"附近的箱子{ID=<"+temp2+">}");
                    	}
                    	
                	}
                	return true;
                }
                default:
                	CSBZ(sender);
                }
                return true;                                                       
        }
       return false;
	}
	
	static synchronized void SreloadConfig()
	{
    	try
    	{//reload NMSL
			configer.save("./plugins/EschatologicalUnit.ChestReset/config.yml");
			configer.load("./plugins/EschatologicalUnit.ChestReset/config.yml");
		} 
    	catch (Exception e) 
    	{
			e.printStackTrace();
		}
	}
	
	static void reset(String ID)
	{
		String world = configer.getString("data."+ID+".world");
		int x = configer.getInt("data."+ID+".x");
		int y = configer.getInt("data."+ID+".y");
		int z = configer.getInt("data."+ID+".z");
		Block  block = Bukkit.getWorld(world).getBlockAt(x, y, z);//获取箱子方块
		Chest chest = (Chest) block.getState();//获取箱子捕获状态
		List<ItemStack> conf_inv = (List<ItemStack>) configer.getList("data."+ID+".inv");//获取物品堆
		chest.getInventory().setContents(conf_inv.toArray(new ItemStack[0]));
	}
	
	static void CSBZ(CommandSender sender)
	{
		sender.sendMessage(prefix+"非法使用 | 使用者信息已记录，此事将被上报");
		loger.warning(prefix+"使用者<"+sender.getName()+">试图非法使用指令{参数不足}");
	}
	
}