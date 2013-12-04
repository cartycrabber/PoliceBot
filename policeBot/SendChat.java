package policeBot;

import net.minecraft.client.Minecraft;

import java.util.Timer;
import java.util.TimerTask;

public class SendChat {
	
	static int alertTimes;
	static int messagesScheduled = -1;
	static int messageWaitSec = 2;
	static Timer messageTimer = new Timer();
	
	static void wantedAlert(final String wantedName, final String crime, int secBetween, final int timesRepeat)
	{
		final Timer alertTimer = new Timer();
		alertTimer.scheduleAtFixedRate(new TimerTask()
		{
			int repeatTimes = timesRepeat;
			@Override
			public void run()
			{
				if (repeatTimes > 0)
				{
					addMessageQueue("Alert: " + wantedName + " is wanted for " + crime);
/*					System.out.println("[PoliceBot] wantedAlert repeat times is " + repeatTimes);
					Minecraft.getMinecraft().thePlayer.sendChatMessage("ALERT: " + wantedName + " is wanted for " + crime);
					repeatTimes--;
*/				}
				else
				{
					System.out.println("[PoliceBot] ending alertTimer");
					alertTimer.cancel();
					alertTimer.purge();
				}
			}
		}, 0, secBetween*1000);
	}
	
	static void addMessageQueue(final String message)
	{
		messagesScheduled++;
		messageTimer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				Minecraft.getMinecraft().thePlayer.sendChatMessage(message);
				messagesScheduled--;
			}
		}, messagesScheduled * messageWaitSec * 1000);
	}
}
