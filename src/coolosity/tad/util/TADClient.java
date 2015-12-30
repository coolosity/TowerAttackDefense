package coolosity.tad.util;

import coolosity.tad.client.Display;
import coolosity.tad.client.DisplaySettings;
import coolosity.tad.client.ServerHandler;
import coolosity.tad.client.display.MainMenu;
import coolosity.tad.packet.Packet;
import coolosity.tad.packet.Packet1Ping;

public class TADClient
{

	public static void main(String[] args)
	{
		TADLogger.setPrefix("[CLIENT] ");
		Packet.init();
		new TADClient();
	}
	
	private Display display;
	private ServerHandler server;
	
	public TADClient()
	{
		display = new Display("Tower Attack Defense",new DisplaySettings(DisplaySettings.RES_1280_720,200));
		display.addOverlay(new MainMenu());
	}
	
	public void onConnected(int status)
	{
		if(status==0)
		{
			TADLogger.log("Connected to "+server+" successfully");
		}
		else
		{
			TADLogger.err("Could not connect to "+server);
		}
	}
	
	public void onDisconnected()
	{
		TADLogger.log("Disconnected from server "+server);
	}
	
	public void onPacketReceived(Packet packet)
	{
		TADLogger.dev("Packet received: "+packet.getClass().getName());
		if(packet instanceof Packet1Ping)
		{
			server.sendPacket(new Packet1Ping());
		}
	}
}
