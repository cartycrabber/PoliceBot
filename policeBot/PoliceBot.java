package policeBot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Calendar;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = PoliceBot.modid, name = "Police Bot", version = "1.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class PoliceBot
{
	public static final String modid = "cartycrabber_PoliceBot";
	public static final String chatID = "#policebot";
	public String chat[];
	public String username;
	public String message;
	static String settingsDir;
	String loggerUsername;
	int snitchAlertBetween = 5;
	int snitchAlertRepeat = 3;
	int snitchDetectCooldown = (snitchAlertBetween * snitchAlertRepeat) - snitchAlertBetween;
	int messageStartPlace;
	TimerTask detectionTask;
	String line;
	File watchListFile;
	File chatLogFile;
	File blacklistFile;
	File tempFile;
	File logFile;
	File announcementsFile;
	File permissionsFile;
	static List<String> detectedList = new ArrayList<String>();
	String[] userChat;
	List<String> botPermissions = new ArrayList<String>();
	List<String> botPermBlacklist = new ArrayList<String>();
	List<String> watchList = new ArrayList<String>();
	static List<String[]> alertList = new ArrayList<String[]>();
	String alertName;
	String alertPerp;
	String alertCrime;
	Date date = new Date();
	SimpleDateFormat dayFormat = new SimpleDateFormat("MM-dd-yy");
	SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	String currentDate = dayFormat.format(date);
	
	@EventHandler
	public void load(FMLInitializationEvent event) throws Exception
	{
		MinecraftForge.EVENT_BUS.register(this);
		if (System.getProperty("os.name").startsWith("Windows"))
		{
			settingsDir = (System.getenv("APPDATA") + "\\.minecraft\\mods\\PoliceBot");
			new File(settingsDir).mkdirs();
			new File(settingsDir + "\\Chat Logs").mkdirs();
		}
		watchListFile = new File(settingsDir + "\\WatchList.txt");
		chatLogFile = new File(settingsDir + "\\Chat Logs\\" + currentDate + ".txt");
		blacklistFile = new File(settingsDir + "\\Blacklist.txt");
		logFile = new File(settingsDir + "\\Log.txt");
		announcementsFile = new File(settingsDir + "\\announcements.txt");
		permissionsFile = new File(settingsDir + "\\Permissions.txt");
		System.out.println("[PoliceBot]" + chatLogFile.toString());
		if (!watchListFile.exists())
			watchListFile.createNewFile();
		if (!chatLogFile.exists())
			chatLogFile.createNewFile();
		if (!blacklistFile.exists())
			blacklistFile.createNewFile();
		if (!logFile.exists())
			logFile.createNewFile();
		if (!announcementsFile.exists())
			announcementsFile.createNewFile();
		if (!permissionsFile.exists())
			permissionsFile.createNewFile();
	}
	@ForgeSubscribe
	public void onChatReceived(ClientChatReceivedEvent event) throws Exception
	{
//		System.out.println("[PoliceBot] Received chat");
		chatLogFile = new File(settingsDir + "\\Chat Logs\\" + currentDate + ".txt");
		if (!chatLogFile.exists())
			chatLogFile.createNewFile();
		FileWriter chatLogger = new FileWriter(chatLogFile, true);
		BufferedReader permissionReader = new BufferedReader(new FileReader(permissionsFile));
		botPermissions.clear();
		while ((line = permissionReader.readLine()) != null)
			if (!botPermissions.contains(line))
				botPermissions.add(line);
		permissionReader.close();
		botPermBlacklist.clear();
		BufferedReader BlacklistReader = new BufferedReader(new FileReader(blacklistFile));
		while ((line = BlacklistReader.readLine()) != null)
			if (!botPermBlacklist.contains(line))
				botPermBlacklist.add(line.toLowerCase());
		BlacklistReader.close();
		String rawChat = event.message;
		FileWriter out = new FileWriter(logFile, true);
		out.write("-------------------------------------------------------------------------------------------------------" + "\n");
		out.write("[Policebot] Raw Chat: " + rawChat + "\n");
		String rawChatArr[] = rawChat.split("\"");
//		System.out.println("[PoliceBot] rawChatArr.length = " + rawChatArr.length);
		for (int x = 0; x < rawChatArr.length; x++)
		{
			out.write("rawChatArr: " + rawChatArr[x] + "\n");
//			System.out.println("[PoliceBot] x = " + x);
		}
		if (Minecraft.getMinecraft().isSingleplayer())
		{
			out.write("Is Singleplayer" + "\n");
			System.out.println("[PoliceBot] " + rawChat);
			if (rawChatArr.length > 7)
			{
				username = rawChatArr[7];
				username = ((rawChatArr[7].split(":"))[0].replace("From ", ""));
			}
			else
				username = "null";
			if (rawChatArr.length > 9)
			{
				message = rawChatArr[9];
			}
			else
				message = "null";
			loggerUsername = username;
		}
		else
		{
			out.write("Is Multiplayer" + "\n");
			if (rawChatArr.length > 3)
			{
				chat =  rawChatArr[3].split(" ");
				for (int x = 0; x < chat.length; x++)
					if (chat[x].contains(":"))
					{
						messageStartPlace = x;
						break;
					}
				username = chat[messageStartPlace].replace(":", "");
//				username = (userChat[0].replace("From ", ""));
				username = username.trim();
				username = stripChat(username);
				for (int x = 0; x < chat.length; x++)
					out.write("chat: " + chat[x] + "\n");
				message = "";
				for (int x = messageStartPlace + 1; x < chat.length; x++)
				{
					message = message + " " + chat[x];
				}
				if (chat[0].contains("From"))
					loggerUsername = "From " + username;
				else
					loggerUsername = username;
			}
		}
		message = message.trim();
		message = stripChat(message);
		out.write("username: " + username + "\n");
		chatLogger.write(timeFormat.format(new java.util.Date()) + "[" + loggerUsername + "]" + message + "\n");
		out.write("message: " + message + "\n");
		String command[] = message.split(" ");
		List<String> commandList = new ArrayList<String>();
		for (String x : message.split(" "))
		{
			commandList.add(x);
//			System.out.println("[PoliceBot] commandList: " + commandList.toString());
		}
		//checks to see if the chat message is long enough to fill each command level, and if its not fills it with "null"
		int cmdtop = 8;
		for (int cmdlvl = 0; cmdlvl <= cmdtop; cmdlvl++)
		{
			if (cmdlvl > commandList.size())
			{
				commandList.add("null");
//				System.out.println("[PoliceBot] commandList: " + commandList.toString());
			}
		}
//		System.out.println("[PoliceBot] commandList: " + commandList);
//		System.out.println("[PoliceBot] commandList element 3: " + commandList.get(3));
		if (commandList.get(0).equalsIgnoreCase(chatID))
		{
			if (!botPermBlacklist.contains(username.toLowerCase()) && !watchList.contains(username.toLowerCase()))
			{
				if (commandList.get(1).equalsIgnoreCase("help"))
				{
					policeBot.SendChat.addMessageQueue("[PoliceBot] https://github.com/cartycrabber/PoliceBot/wiki/Controls");
				}
				else if (commandList.get(1).equalsIgnoreCase("alerts"))
				{
					if (botPermissions.contains(username.toLowerCase()))
					{
						if (commandList.get(2).equalsIgnoreCase("run"))
							if (commandList.get(3) != "null" && commandList.get(4) != "null" && commandList.get(5) != "null" && commandList.get(6) != "null")
								policeBot.AlertManager.wantedAlert(commandList.get(3), commandList.get(4), Integer.parseInt(commandList.get(5)), Integer.parseInt(commandList.get(6)));
							else
								policeBot.SendChat.addMessageQueue("[PoliceBot] Error, use command like this: run criminalName crime secondsBetween timesRepeated");				
						else if (commandList.get(2).equalsIgnoreCase("running"))
						{
							System.out.println("[PoliceBot] alertsRunning -" + policeBot.AlertManager.alertsRunning.toString() + "-");
							if (policeBot.AlertManager.alertsRunning.toString().equals("[]"))
								policeBot.SendChat.addMessageQueue("[PoliceBot] No alerts are running");
							else
								 policeBot.SendChat.addMessageQueue("[PoliceBot] Alerts: "+ policeBot.AlertManager.alertsRunning.toString().replace("[", "").replace("]", ""));
						}
						else if (commandList.get(2).equalsIgnoreCase("stop"))
							if (commandList.get(3) != "null")
								if (policeBot.AlertManager.alertsRunning.contains(commandList.get(3)))
								{
									SendChat.addMessageQueue("[PoliceBot] Stopping alert " + commandList.get(3));
									policeBot.AlertManager.alertsRunning.remove(commandList.get(3));
									detectedList.remove((commandList.get(3).split("_"))[1]);
								}
								else
								{
									policeBot.SendChat.addMessageQueue("[PoliceBot] Error, no alerts under that name. Try alerts running");
									System.out.println("[PoliceBot] Alerts Running: " + policeBot.AlertManager.alertsRunning.toString());
									System.out.println("[PoliceBot] Command List 3: " + commandList.get(3).toString());
								}
							else
								policeBot.SendChat.addMessageQueue("[PoliceBot] Error, use command like this: alerts stop alertName");
					}
					else
						policeBot.SendChat.addMessageQueue("[PoliceBot] Im sorry, " + username + ", you do not have permission to do that");
				}
				else if (commandList.get(1).equalsIgnoreCase("announcements"))
				{
					if (botPermissions.contains(username))
					{
						if (commandList.get(2).equalsIgnoreCase("start"))
						{
							if (chat[0].contains("From"))
								SendChat.addMessageQueue("/r Starting announcements");
							else
								SendChat.addMessageQueue("[PoliceBot] Starting announcements");
							policeBot.AlertManager.announcementsAlert(10, announcementsFile);
						}
						else if (commandList.get(2).equalsIgnoreCase("stop"))
						{
							if (chat[0].contains("From"))
								SendChat.addMessageQueue("/r Stopping announcements");
							else
								SendChat.addMessageQueue("[PoliceBot] Stopping announcements");
							AlertManager.runAnnouncements = false;
						}
						else
							if (chat[0].contains("From"))
								SendChat.addMessageQueue("/r Error, use command like this: announcements start/stop");
							else
								SendChat.addMessageQueue("[PoliceBot] Error, use command like this: announcements start/stop");
					}
					else
						SendChat.addMessageQueue("[PoliceBot] Im sorry, " + username + ", you do not have permission to do that");
				}
				else if (commandList.get(1).equalsIgnoreCase("city"))
				{
					policeBot.SendChat.addMessageQueue("Current government officials: Itaqi-President, GTAVisbest-Vice President,");
					policeBot.SendChat.addMessageQueue("Konvexon-Police Chief, KwizzleHazzizle-Public Works Director.");
					policeBot.SendChat.addMessageQueue("Elections are held every 15th. Buy a plot for 4d from any government official. ");
					policeBot.SendChat.addMessageQueue("Make sure to visit /r/civcraft_orion. Thank you for visiting Orion " + username + "!");
				}
				else if (commandList.get(1).equalsIgnoreCase("credits"))
				{
					policeBot.SendChat.addMessageQueue("[PoliceBot] Coded by baconater88 with the help of Murder_is_Tasty for the city of Orion.");
					policeBot.SendChat.addMessageQueue("[PoliceBot] Version 1.0");
				}
				else if (commandList.get(1).equalsIgnoreCase("permissions"))
					policeBot.SendChat.addMessageQueue("[PoliceBot] Permissions: " + botPermissions.toString().replace("[", "").replace("]", ""));
				else if (commandList.get(1).equalsIgnoreCase("blacklist"))
				{
					if (botPermissions.contains(username))
					{
						if (commandList.get(2).equalsIgnoreCase("add"))
						{
							if (commandList.get(3) != "null")
							{
								FileWriter blacklistWriter = new FileWriter(blacklistFile, true);
								blacklistWriter.write(commandList.get(3) + "\n");
								blacklistWriter.close();
								policeBot.SendChat.addMessageQueue("[PoliceBot] Blacklisting " + commandList.get(3));
							}
							else
								policeBot.SendChat.addMessageQueue("[PoliceBot] Error, use command like this: blacklist add playerName");
						}
						else if (commandList.get(2).equalsIgnoreCase("remove"))
						{
							tempFile = new File(settingsDir + "\\TempFile.txt");
							BufferedReader blacklistReader = new BufferedReader(new FileReader(blacklistFile));
							FileWriter tempWriter = new FileWriter(tempFile, true);
							while((line = blacklistReader.readLine()) != null)
							{
								if (line.trim().equals(commandList.get(3))) continue;
								tempWriter.write(line + "\n");
							}
							blacklistReader.close();
							tempWriter.close();
							blacklistFile.delete();
							tempFile.renameTo(blacklistFile);
						}
						else if (commandList.get(2).equalsIgnoreCase("null"))
						{
							policeBot.SendChat.addMessageQueue("[PoliceBot] Blacklist: " + botPermBlacklist.toString().replace("[", "").replace("]", ""));
						}
					}
					else
						policeBot.SendChat.addMessageQueue("[PoliceBot] Im sorry, " + username + ", you do not have permission to do that");
				}
				else if (commandList.get(1).equalsIgnoreCase("debug"))
				{
					if (botPermissions.contains(username))
					{
						if (commandList.get(2).equalsIgnoreCase("on"))
						{
							snitchAlertBetween = 1;
							snitchAlertRepeat = 3;
							snitchDetectCooldown = (snitchAlertBetween * snitchAlertRepeat) - snitchAlertBetween;
							policeBot.SendChat.addMessageQueue("[PoliceBot] Debug mode on");
						}
						else if (commandList.get(2).equalsIgnoreCase("off"))
						{
							snitchAlertBetween = 5;
							snitchAlertRepeat = 3;
							snitchDetectCooldown = (snitchAlertBetween * snitchAlertRepeat) - snitchAlertBetween;
							policeBot.SendChat.addMessageQueue("[PoliceBot] Debug mode off");
						}
						else
							policeBot.SendChat.addMessageQueue("[PoliceBot] Error, use command like this: debug on/off");
					}
					else
						policeBot.SendChat.addMessageQueue("[PoliceBot] Im sorry, " + username + ", you do not have permission to do that");
				}
				else
					policeBot.SendChat.addMessageQueue("[PoliceBot] Im sorry, " + username + ", that command does not exist. Try help");
			}
			else
				policeBot.SendChat.addMessageQueue("/msg " + username + " [PoliceBot] Im sorry, you have been blacklisted");
		}
		else if (commandList.get(0).equals("*") && commandList.get(2).equalsIgnoreCase("entered"))
		{
			System.out.println("[PoliceBot] Is snitch alert");
			if (Integer.parseInt(commandList.get(6).replace("[", "").replace("]", "")) <= -4000 && Integer.parseInt(commandList.get(6).replace("[", "").replace("]", "")) >= -6000 && Integer.parseInt(commandList.get(8).replace("[", "").replace("]", "")) <= -4000 && Integer.parseInt(commandList.get(8).replace("[", "").replace("]", "")) >= -6000)
			{
				BufferedReader AlertsReader = new BufferedReader(new FileReader(watchListFile));
				watchList.clear();
				while ((line = AlertsReader.readLine()) != null)
					watchList.add(line.toLowerCase());
				AlertsReader.close();
				System.out.println("[PoliceBot] Watchlist: " + watchList.toString());
				System.out.println("[PoliceBot] Detected List: " + detectedList.toString());
				if (watchList.contains(commandList.get(1).toLowerCase()))
				{
					if (!detectedList.contains(commandList.get(1)))
					{
							policeBot.AlertManager.nearbyAlert(commandList.get(1), snitchAlertBetween, snitchAlertRepeat);
							detectedList.add(commandList.get(1));
							System.out.println("[PoliceBot] Detected List: " + detectedList.toString());
							final String detectedUser = commandList.get(1);
							System.out.println("[PoliceBot] Added " + detectedUser + " to detected list");
							final Timer detectionTimer = new Timer();
							detectionTimer.schedule(detectionTask = new TimerTask()
							{
								@Override
								public void run()
								{
									if (detectedList.contains(detectedUser))
									{
										detectedList.remove(detectedUser);
										System.out.println("[PoliceBot] Removed " + detectedUser + " from detected list");
									}
								}
							}, snitchDetectCooldown * 60 * 1000);
					}
				}
			}
		}
		out.close();
		chatLogger.close();
	}
	
	public String stripChat(String chat)
	{
		String badCharacters[] = {"§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f"};
		for (int x = 0; x < badCharacters.length; x++)
		{
			chat = chat.replace(badCharacters[x], "");
		}
		return chat;
		
	}
}
