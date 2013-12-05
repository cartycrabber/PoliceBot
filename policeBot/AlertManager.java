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
	
	static void wantedAlert(int alertID, int secBetween, final int timesRepeat) throws Exception
	{
		BufferedReader AlertsReader = new BufferedReader(new FileReader(policeBot.PoliceBot.settingsDir + "\\alerts\\wantedAlerts.txt"));
		List<String> wantedAlertsList = new ArrayList<String>();
		String line = null;
		while ((line = AlertsReader.readLine()) != null)
		{
			wantedAlertsList.add(line);
		}
		AlertsReader.close();
		String alertInfo = wantedAlertsList.get(alertID);
		String alertInfoArr[] = alertInfo.split(",");
		final String alertPerp = alertInfoArr[2];
		final String alertCrime = alertInfoArr[3];
		final Timer alertTimer = new Timer();
		alertTimer.scheduleAtFixedRate(new TimerTask()
		{
			int repeatTimes = timesRepeat;
			@Override
			public void run()
			{
				if (repeatTimes > 0)
				{
					policeBot.SendChat.addMessageQueue("ALERT: " + alertPerp + " is wanted for " + alertCrime);
					repeatTimes--;
				}
				else
				{
					System.out.println("[PoliceBot] ending alertTimer");
					alertTimer.cancel();
					alertTimer.purge();
				}
			}
		}, 0, secBetween*1000);
	}
}
