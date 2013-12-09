package policeBot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AlertManager 
{
	static List<String> alertsRunning = new ArrayList<String>();
	static TimerTask alertTask;
	
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
	
	void stopAlert()
	{
		
	}
}
