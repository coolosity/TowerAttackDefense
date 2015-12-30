package coolosity.tad.client;

public class DisplaySettings
{
	
	public static final int RES_852_480 = 1;
	public static final int RES_1280_720 = 2;
	public static final int RES_1366_768 = 3;
	public static final int RES_1600_900 = 4;
	
	private int resMode;
	private int fpsMax;
	
	public DisplaySettings(int resMode, int fpsMax)
	{
		this.resMode = resMode;
		this.fpsMax = fpsMax;
	}
	
	public int getResMode()
	{
		return resMode;
	}
	
	public int getFPSMax()
	{
		return fpsMax;
	}
}
