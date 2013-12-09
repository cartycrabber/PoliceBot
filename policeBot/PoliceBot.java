package policeBot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Calendar;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = PoliceBot.modid, name = "Police Bot", version = "0.4")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class PoliceBot
{
	public static final String modid = "cartycrabber_PoliceBot";
	public static final String chatID = "#policebot";
	public String chat[];
	public String username;
	public String message;
	static String settingsDir;
	int snitchAlertBetween = 5;
	int snitchAlertRepeat = 3;
	int snitchDetectCooldown = (snitchAlertBetween * snitchAlertRepeat) - snitchAlertBetween;
	TimerTask detectionTask;
	String line;
	File watchListFile;
	File chatLogFile;
	File blacklistFile;
	File tempFile;
	static List<String> detectedList = new ArrayList<String>();
	List<String> botPermissions = Arrays.asList("cartycrabber", "baconater88", "konvexon", "itaqi");
	List<String> botPermBlacklist = new ArrayList<String>();
	List<String> watchList = new ArrayList<String>();
	static List<String[]> alertList = new ArrayList<String[]>();
	String alertName;
	String alertPerp;
	String alertCrime;
	SimpleDateFormat sdf = new SimpleDateFormat("[MM/dd/yy][HH:mm:ss]");
	
	@EventHandler
	public void load(FMLInitializationEvent event) throws Exception
	{
		MinecraftForge.EVENT_BUS.register(this);
		if (System.getProperty("os.name").startsWith("Windows"))
		{
			settingsDir = (System.getenv("APPDATA") + "\\.minecraft\\mods\\PoliceBot");
			new File(settingsDir).mkdirs();
			new File(settingsDir + "\\alerts").mkdirs();
		}
		watchListFile = new File(settingsDir + "\\WatchList.txt");
		chatLogFile = new File(settingsDir + "\\ChatLog.txt");
		blacklistFile = new File(settingsDir + "\\Blacklist.txt");
		if (!watchListFile.exists())
			watchListFile.createNewFile();
		if (!chatLogFile.exists())
			chatLogFile.createNewFile();
		if (!blacklistFile.exists())
			blacklistFile.createNewFile();
	}

	@ForgeSubscribe
	public void onChatReceived(ClientChatReceivedEvent event) throws Exception
	{
//		System.out.println("[PoliceBot] Received chat");
		FileWriter chatLogger = new FileWriter(settingsDir + "\\ChatLog.txt", true);
		botPermBlacklist.clear();
		BufferedReader BlacklistReader = new BufferedReader(new FileReader(policeBot.PoliceBot.settingsDir + "\\Blacklist.txt"));
		while ((line = BlacklistReader.readLine()) != null)
			if (!botPermBlacklist.contains(line))
				botPermBlacklist.add(line);
		BlacklistReader.close();
		String rawChat = event.message;
//		FileWriter out = new FileWriter("C:\\Users\\Will\\Documents\\Development\\PoliceBotOutput.txt", true);
//		out.write("-------------------------------------------------------------------------------------------------------" + "\n");
//		out.write(rawChat + "\n");
		String rawChatArr[] = rawChat.split("\"");
//		System.out.println("[PoliceBot] rawChatArr.length = " + rawChatArr.length);
		for (int x = 0; x < rawChatArr.length; x++)
		{
//			out.write("rawChatArr: " + rawChatArr[x] + "\n");
//			System.out.println("[PoliceBot] x = " + x);
		}
		if (Minecraft.getMinecraft().isSingleplayer())
		{
//			out.write("Is Singleplayer" + "\n");
			if (rawChatArr.length > 6)
			{
				username = rawChatArr[7];
			}
			else
				username = "null";
			if (rawChatArr.length > 8)
			{
				message = rawChatArr[9];
			}
			else
				message = "null";
		}
		else
		{
//			out.write("Is Multiplayer" + "\n");
			if (rawChatArr.length > 3)
			{
				chat =  rawChatArr[3].split(" ");
				username = chat[0].replace(":", "");
				username = username.trim();
				username = stripChat(username);
				for (int x = 0; x < chat.length; x++)
//					out.write("chat: " + chat[x] + "\n");
				
//				out.write("username: " + username + "\n");
				message = "";
				for (int x = 1; x < chat.length; x++)
				{
					message = message + " " + chat[x];
				}
			}
		}
		message = message.trim();
		message = stripChat(message);
		
		chatLogger.write(sdf.format(new java.util.Date()) + "[" + username + "]" + message + "\n");
//		out.write("message: " + message + "\n");
		String command[] = message.split(" ");
		List<String> commandList = new ArrayList<String>();
		for (String x : message.split(" "))
			commandList.add(x);
		//checks to see if the chat message is long enough to fill each command level, and if its not fills it with "null"
		int cmdtop = 8;
		for (int cmdlvl = 0; cmdlvl <= cmdtop; cmdlvl++)
		{
			if (cmdlvl > commandList.size())
			{
				commandList.add("null");
//				System.out.println("[PoliceBot] commandList: " + commandList);
			}
		}
//		System.out.println("[PoliceBot] commandList: " + commandList);
//		System.out.println("[PoliceBot] commandList element 3: " + commandList.get(3));
		if (commandList.get(0).equalsIgnoreCase(chatID))
		{
			if (!botPermBlacklist.contains(username))
			{
				if (commandList.get(1).equalsIgnoreCase("help"))
				{
					policeBot.SendChat.addMessageQueue("[PoliceBot] https://github.com/cartycrabber/PoliceBot/wiki/Controls");
				}
				else if (commandList.get(1).equalsIgnoreCase("alerts"))
				{
					if (botPermissions.contains(username))
					{
						if (commandList.get(2).equalsIgnoreCase("run"))
							if (commandList.get(3) != "null" && commandList.get(4) != "null" && commandList.get(5) != "null" && commandList.get(6) != "null")
								policeBot.AlertManager.wantedAlert(commandList.get(3), commandList.get(4), Integer.parseInt(commandList.get(5)), Integer.parseInt(commandList.get(6)));
							else
								policeBot.SendChat.addMessageQueue("[PoliceBot] Error, use command like this: run criminalName crime secondsBetween timesRepeated");
		/*				else if (commandList.get(2).equalsIgnoreCase("list"))
						{
							BufferedReader AlertsReader = new BufferedReader(new FileReader(policeBot.PoliceBot.settingsDir + "\\alerts\\wantedAlerts.txt"));
							List<String> wantedAlertsList = new ArrayList<String>();
							String line = null;
							while ((line = AlertsReader.readLine()) != null)
							{
								wantedAlertsList.add(line);
							}
							AlertsReader.close();
							System.out.println("[PoliceBot] Alerts List: " + wantedAlertsList);
							List<String> wantedAlertsIDList = new ArrayList<String>();
							for (int i = 0; i < wantedAlertsList.size(); i++)
							{
								String temp[] = wantedAlertsList.get(i).split(",");
								wantedAlertsIDList.add(temp[0]);
							}
							System.out.println("[PoliceBot] Alerts ID List: " + wantedAlertsIDList);
							policeBot.SendChat.addMessageQueue("[PoliceBot] Current alerts: " + wantedAlertsIDList);
						}
						else if (commandList.get(2).equalsIgnoreCase("create"))
						{
							if (commandList.get(3) != "null" && commandList.get(4) != "null")
							{
								policeBot.AlertManager.createAlert(commandList.get(3), commandList.get(4));
							}
							else
								policeBot.SendChat.addMessageQueue("[PoliceBot] Command proper use: create wantedPlayer crime");
						}
		*/				
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
				else if (commandList.get(1).equalsIgnoreCase("city"))
				{
					policeBot.SendChat.addMessageQueue("Current government officials: Itaqi-President, ObsidianOverlord-Vice President, King_Devely-Judge");
					policeBot.SendChat.addMessageQueue("Whohead63-Secretary of Treasury, Konvexon-Police Chief, KwizzleHazzizle-Public Works Director.");
					policeBot.SendChat.addMessageQueue("Elections are held every 15th. Buy a plot for 4d from any government official. ");
					policeBot.SendChat.addMessageQueue("Make sure to visit /r/civcraft_orion. Thank you for visiting Orion " + username + "!");
				}
				else if (commandList.get(1).equalsIgnoreCase("credits"))
				{
					policeBot.SendChat.addMessageQueue("[PoliceBot] Coded by baconater88 with the help of Murder_is_Tasty for the city of Orion.");
					policeBot.SendChat.addMessageQueue("[PoliceBot] Version 0.4");
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
				policeBot.SendChat.addMessageQueue("[PoliceBot] Im sorry, " + username + ", you have been blacklisted");
		}
		else if (commandList.get(0).equals("*"))
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
				if (watchList.contains(commandList.get(1)))
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
//		out.close();
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
