package coolosity.tad.packet;

import java.lang.reflect.Method;
import java.util.HashMap;

import coolosity.tad.util.TADLogger;

public abstract class Packet
{

	private static HashMap<Integer,Class<?>> packetSet;
	
	public static void init()
	{
		packetSet = new HashMap<Integer,Class<?>>();
		packetSet.put(1, Packet1Ping.class);
	}
	
	public static Packet fromString(String string)
	{
		try
		{
			String[] spl = string.split(" ");
			int id = Integer.parseInt(spl[0]);
			if(!packetSet.containsKey(id))
			{
				TADLogger.err("Received packet with unrecognized id "+id+": "+string);
				return null;
			}
			else
			{
				Class<?> cls = packetSet.get(id);
				Class<?>[] cArg = new Class[]{String.class};
				Method m = cls.getMethod("createFromString", cArg);
				return (Packet)m.invoke(cls.newInstance(), string);
			}
		}
		catch(Exception e)
		{
			TADLogger.err("An error occured while loading packet from string: "+string);
			e.printStackTrace();
			return null;
		}
	}
	
	private int id;
	
	public Packet(int id)
	{
		this.id = id;
	}
	
	public abstract Packet createFromString(String string);
	
	@Override
	public String toString()
	{
		return id+"";
	}
}
