package policeBot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

@Mod(modid = PoliceBot.modid, name = "Police Bot", version = "0.2")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class PoliceBot
{
	public static final String modid = "cartycrabber_PoliceBot";
	public static final String chatID = "#policebot";
	public String chat[];
	public String username;
	public String message;
	static String settingsDir;
	List<String[]> alertList = new ArrayList<String[]>();
	String alertName;
	String alertPerp;
	String alertCrime;
	
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
	}

	@ForgeSubscribe
	public void onChatReceived(ClientChatReceivedEvent event) throws Exception
	{
		String rawChat = event.message.toLowerCase();
		FileWriter out = new FileWriter("C:\\Users\\Will\\Documents\\Development\\PoliceBotOutput.txt", true);
		out.write("-------------------------------------------------------------------------------------------------------" + "\n");
		out.write(rawChat + "\n");
		String rawChatArr[] = rawChat.split("\"");
		System.out.println("[PoliceBot] rawChatArr.length = " + rawChatArr.length);
		for (int x = 0; x < rawChatArr.length; x++)
		{
			out.write("rawChatArr: " + rawChatArr[x] + "\n");
			System.out.println("[PoliceBot] x = " + x);
		}
		if (Minecraft.getMinecraft().isSingleplayer())
		{
			out.write("Is Singleplayer" + "\n");
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
			out.write("Is Multiplayer" + "\n");
			if (rawChatArr.length > 3)
			{
				chat =  rawChatArr[3].split(" ");
				username = chat[0].replace(":", "");
				username = username.trim();
				username = stripChat(username);
				for (int x = 0; x < chat.length; x++)
					out.write("chat: " + chat[x] + "\n");
				
				out.write("username: " + username + "\n");
				message = "";
				for (int x = 1; x < chat.length; x++)
				{
					message = message + " " + chat[x];
				}
			}
		}
		message = message.trim();
		message = stripChat(message);
		out.write("message: " + message + "\n");
		String command[] = message.split(" ");
		List<String> commandList = new ArrayList<String>();
		for (String x : message.split(" "))
			commandList.add(x);
//		System.out.println("[PoliceBot] commandList: " + commandList);
		//checks to see if the chat message is long enough to fill each command level, and if its not fills it with "null"
		int cmdtop = 4;
		for (int cmdlvl = 0; cmdlvl <= cmdtop; cmdlvl++)
		{
			if (cmdlvl > commandList.size())
			{
				commandList.add("null");
//				System.out.println("[PoliceBot] commandList: " + commandList);
			}
		}
		if (commandList.get(0).equalsIgnoreCase(chatID))
		{
			System.out.println("[PoliceBot] called in chat");
			if (commandList.get(1).equalsIgnoreCase("help"))
			{
				policeBot.SendChat.addMessageQueue("[PoliceBot] Use command 'alerts list' to see a list of alerts and 'alert run' to start one");
				policeBot.SendChat.addMessageQueue("[PoliceBot] Use 'credits' to see contributors. Use 'city' to see information about the city");
			}
			else if (commandList.get(1).equalsIgnoreCase("alerts"))
			{
				if (commandList.get(2).equalsIgnoreCase("run"))
					if (commandList.get(3) != "null")
						policeBot.AlertManager.wantedAlert(Integer.parseInt(commandList.get(3)), 5, 3);
					else
						policeBot.SendChat.addMessageQueue("[PoliceBot] No programs match that name");
				else if (commandList.get(2).equalsIgnoreCase("list"))
					policeBot.SendChat.addMessageQueue("[PoliceBot] Current alerts: test alert");
				else if (commandList.get(2).equalsIgnoreCase("create"))
				{
					if (commandList.get(3) != "null" && commandList.get(4) != "null")
					{
						policeBot.AlertManager.createAlert(commandList.get(3), commandList.get(4));
					}
					else
						policeBot.SendChat.addMessageQueue("[PoliceBot] Command proper use: create wantedPlayer crime");
				}
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
				policeBot.SendChat.addMessageQueue("[PoliceBot] Version 0.2");
			}
			else
				policeBot.SendChat.addMessageQueue("[PoliceBot] Im sorry, " + username + ", that command does not exist");
		}
		out.close();
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
