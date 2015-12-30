package coolosity.tad.util;

public class TADLogger
{

	private static String prefix = "";
	
	public static void setPrefix(String prefix)
	{
		TADLogger.prefix = prefix;
	}
	
	public static void log(String txt)
	{
		System.out.println(prefix+"[INFO] "+txt);
	}
	
	public static void dev(String txt)
	{
		System.out.println(prefix+"[DEV] "+txt);
	}
	
	public static void err(String err)
	{
		System.err.println(prefix+"[ERROR] "+err);
	}
	
}
