package policeBot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class AlertManager 
{
	static List<String> alertsRunning = new ArrayList<String>();
	static List<String> announcementsList = new ArrayList<String>();
	static List<Integer> usedAnnouncements = new ArrayList<Integer>();
	static TimerTask alertTask;
	static String line;
	static boolean runAnnouncements = false;
	static int randInt;
	
	static void createAlert(String alertPerp, String alertCrime) throws Exception
	{
		BufferedReader AlertsReader = new BufferedReader(new FileReader(policeBot.PoliceBot.settingsDir + "\\alerts\\wantedAlerts.txt"));
		int lines = 0;
		while (AlertsReader.readLine() != null) lines++;
		AlertsReader.close();
		FileWriter AlertsWriter = new FileWriter(policeBot.PoliceBot.settingsDir + "\\alerts\\wantedAlerts.txt", true);
		AlertsWriter.write(lines + "," + alertPerp + "," + alertCrime + "\n");
		AlertsWriter.close();
	}
	
	static void wantedAlert(final String alertPerp, final String alertCrime, int minBetween, final int timesRepeat) throws Exception
	{
		alertsRunning.add("wanted_" + alertPerp);
/*		BufferedReader AlertsReader = new BufferedReader(new FileReader(policeBot.PoliceBot.settingsDir + "\\alerts\\wantedAlerts.txt"));
		List<String> wantedAlertsList = new ArrayList<String>();
		String line = null;
		while ((line = AlertsReader.readLine()) != null)
		{
			wantedAlertsList.add(line);
		}
		AlertsReader.close();
		String alertInfo = wantedAlertsList.get(alertID);
		String alertInfoArr[] = alertInfo.split(",");
		final String alertPerp = alertInfoArr[1];
		final String alertCrime = alertInfoArr[2];
*/		final Timer wantedTimer = new Timer();
		wantedTimer.scheduleAtFixedRate(alertTask = new TimerTask()
		{
			int repeatTimes = timesRepeat;
			@Override
			public void run()
			{
				if (alertsRunning.contains("wanted_" + alertPerp))
				{
					policeBot.SendChat.addMessageQueue("Alert: " + alertPerp + " is wanted for " + alertCrime);
					repeatTimes--;
					if (repeatTimes <= 0)
					{
						for (int i = 0; i < alertsRunning.size(); i++)
						{
							if (alertsRunning.get(i).equals("snitchDetect_" + alertPerp))
							{
								alertsRunning.remove(i);
								break;
							}
							wantedTimer.cancel();
							wantedTimer.purge();
						}
					}
				}
				else
				{
					wantedTimer.cancel();
					wantedTimer.purge();
				}
			}
		}, 0, minBetween * 60 * 1000);
	}
	
	static void nearbyAlert(final String alertPerp, int minBetween, final int timesRepeat)
	{
		alertsRunning.add("snitchDetect_" + alertPerp);
		final Timer nearbyTimer = new Timer();
		nearbyTimer.scheduleAtFixedRate(alertTask = new TimerTask()
		{
			int repeatTimes = timesRepeat;
			@Override
			public void run()
			{
				if (alertsRunning.contains("snitchDetect_" + alertPerp))
				{
					policeBot.SendChat.addMessageQueue("Alert: The criminal " + alertPerp + " is in Orion");
					repeatTimes--;
					if (repeatTimes <= 0)
					{
						for (int i = 0; i < alertsRunning.size(); i++)
						{
							if (alertsRunning.get(i).equals("snitchDetect_" + alertPerp))
							{
								alertsRunning.remove(i);
								break;
							}
							nearbyTimer.cancel();
							nearbyTimer.purge();
						}
					}
				}
				else
				{
					nearbyTimer.cancel();
					nearbyTimer.purge();
				}
			}
		}, 0, minBetween * 60 * 1000);
	}
	
	static void announcementsAlert(int alertSpacing, final File announcementsFile) throws Exception
	{
		runAnnouncements = true;
		final Timer announcementTimer = new Timer();
		announcementTimer.scheduleAtFixedRate(new TimerTask()
		{
			@Override
			public void run()
			{
				if (runAnnouncements)
				{
					BufferedReader announcementReader = null;
					try
					{
						announcementReader = new BufferedReader(new FileReader(announcementsFile));
					} 
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}
					try
					{
						while ((line = announcementReader.readLine()) != null)
							if (!announcementsList.contains(line))
								announcementsList.add(line);
						announcementReader.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					if (usedAnnouncements.size() == announcementsList.size())
						usedAnnouncements.clear();
					Random randomGen = new Random();
					do
						randInt = randomGen.nextInt(announcementsList.size());
					while (usedAnnouncements.contains(randInt));
					usedAnnouncements.add(randInt);
					System.out.println("[PoliceBot] Random Int: " + randInt);
					System.out.println("[PoliceBot] Used Announcements : " + usedAnnouncements.toString());
					policeBot.SendChat.addMessageQueue(announcementsList.get(randInt));
				}
				else
				{
					announcementTimer.cancel();
					announcementTimer.purge();
				}
			}
		}, 0, alertSpacing * 60 * 1000);
	}
}
