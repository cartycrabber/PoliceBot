package policeBot;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SendChat {
	
	static int alertTimes;
	static int messagesScheduled = 0;
	static int messageWaitSec = 3;
	static String nextMessage;
	static boolean messageTooBig;
	static Timer messageTimer = new Timer();
	
	//Puts messages for chat in a queue separated by messageWaitSec to avoid spamming the server
	static void addMessageQueue(String message)
	{
		if (message.length() > 100)
		{
			messageTooBig = true;
			String[] messageArr = message.split(" ");
			System.out.println("[PoliceBot] messageArr: " + messageArr.toString());
			List<String> newMessageList = new ArrayList<String>();
			List<String> nextMessageList = new ArrayList<String>();
			for (int x = 0; x < messageArr.length; x++)
			{
				if (newMessageList.toString().length() < 100)
				{
					newMessageList.add(messageArr[x]);
				}
				else
					nextMessageList.add(messageArr[x]);
			}
			System.out.println("[PoliceBot] newMessageList: " + newMessageList.toString());
			System.out.println("[PoliceBot] nextMessageList: " + nextMessageList.toString());
			System.out.println("[PoliceBot] newMessage.size(): " + newMessageList.size());
			nextMessageList.add(0, newMessageList.get(newMessageList.size() - 1));
			newMessageList.remove(newMessageList.size() - 1);
			StringBuilder newMessageBuilder = new StringBuilder();
			for (String value : newMessageList)
				newMessageBuilder.append(value + " ");
			message = newMessageBuilder.toString();
			StringBuilder nextMessageBuilder = new StringBuilder();
			for (String value : nextMessageList)
				nextMessageBuilder.append(value + " ");
			System.out.println("[PoliceBot] newMessageList2: " + newMessageList.toString());
			System.out.println("[PoliceBot] nextMessageList2: " + nextMessageList.toString());
			nextMessage = nextMessageBuilder.toString();
			System.out.println("[PoliceBot] nextMessage: " + nextMessage);
		}
		final String chatMessage = message;
		messagesScheduled++;
//		System.out.println("[PoliceBot] Messages scheduled in queue: " + messagesScheduled);
		messageTimer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				Minecraft.getMinecraft().thePlayer.sendChatMessage(chatMessage);
				messagesScheduled--;
//				System.out.println("[PoliceBot] Messages scheduled in queue: " + messagesScheduled)
			}
		}, messagesScheduled * messageWaitSec * 1000);
		if (messageTooBig)
		{
			messageTooBig = false;
			SendChat.addMessageQueue(nextMessage);
		}
	}
}
