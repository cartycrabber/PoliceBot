package policeBot;

import net.minecraft.client.Minecraft;

import java.util.Timer;
import java.util.TimerTask;

public class SendChat {
	
	static int alertTimes;
	static int messagesScheduled = 0;
	static int messageWaitSec = 2;
	static Timer messageTimer = new Timer();
	
	//Puts messages for chat in a queue separated by messageWaitSec to avoid spamming the server
	static void addMessageQueue(final String message)
	{
		messagesScheduled++;
		System.out.println("[PoliceBot] Messages scheduled in queue: " + messagesScheduled);
		messageTimer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				Minecraft.getMinecraft().thePlayer.sendChatMessage(message);
				messagesScheduled--;
				System.out.println("[PoliceBot] Messages scheduled in queue: " + messagesScheduled);
			}
		}, messagesScheduled * messageWaitSec * 1000);
	}
}
