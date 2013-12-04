package policeBot;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ModLoader;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = PoliceBot.modid, name = "Police Bot", version = "0.2")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class PoliceBot
{
	public static final String modid = "cartycrabber_PoliceBot";
	public static final String chatID = "#policebot";
	
	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@ForgeSubscribe
	public void onChatReceived(ClientChatReceivedEvent event)
	{
		System.out.println("[PoliceBot] onChatReceived event fired!");
		String rawchat = event.message.toLowerCase();
		String rawChatArr[] = rawchat.split("\"");
		String username = rawChatArr[7];
		String chat = rawChatArr[9];
		String command[] = chat.split(" ");
		String cmdlvl0 = "null";
		String cmdlvl1 = "null";
		String cmdlvl2 = "null";
		String cmdlvl3 = "null";
		if (command.length > 0)
		{
			cmdlvl0 = command[0];
			if (command.length > 1)
			{
				cmdlvl1 = command[1];
				if (command.length > 2)
				{
					cmdlvl2 = command[2];
					if (command.length > 3)
					{
						cmdlvl3 = command[3];
					}
				}
			}
		}
		if (cmdlvl0.equalsIgnoreCase(chatID))
		{
			System.out.println("[PoliceBot] called in chat");
			if (cmdlvl1.equalsIgnoreCase("help"))
			{
				policeBot.SendChat.addMessageQueue("[PoliceBot] Use command 'alerts list' to see a list of alerts and 'alert run' to start one");
				policeBot.SendChat.addMessageQueue("[PoliceBot] Use 'credits' to see contributors. Use 'city' to see information about the city");
			}
			else if (cmdlvl1.equalsIgnoreCase("alerts"))
			{
				if (cmdlvl2.equalsIgnoreCase("run"))
					if (cmdlvl3.equalsIgnoreCase("testalert"))
						policeBot.SendChat.wantedAlert("testPlayer", "test crime", 5, 3);
					else
						policeBot.SendChat.addMessageQueue("[PoliceBot] No programs match that name");
				else if (cmdlvl2.equalsIgnoreCase("list"))
					policeBot.SendChat.addMessageQueue("[PoliceBot] Current alerts: test alert");
			}
			else if (cmdlvl1.equalsIgnoreCase("city"))
			{
				policeBot.SendChat.addMessageQueue("Current government officials: Itaqi-President, ObsidianOverlord-Vice President, King_Devely-Judge");
				policeBot.SendChat.addMessageQueue("Whohead63-Secretary of Treasury, Konvexon-Police Chief, KwizzleHazzizle-Public Works Director.");
				policeBot.SendChat.addMessageQueue("Elections are held every 15th. Buy a plot for 4d from any government official. ");
				policeBot.SendChat.addMessageQueue("Make sure to visit /r/civcraft_orion. Thank you for visiting Orion " + username);
			}
			else if (cmdlvl1.equalsIgnoreCase("credits"))
			{
				policeBot.SendChat.addMessageQueue("[PoliceBot] Coded by baconater88 with the help of Murder_is_Tasty for the city of Orion.");
				policeBot.SendChat.addMessageQueue("[PoliceBot] Version 0.2");
			}
			else
				policeBot.SendChat.addMessageQueue("[PoliceBot] Im sorry, " + username + ", that command does not exist");
		}
	}
}
