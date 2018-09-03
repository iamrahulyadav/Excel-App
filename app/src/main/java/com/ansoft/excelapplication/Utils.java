package com.ansoft.excelapplication;

import java.io.File;
import android.os.Environment;
import android.util.Log;

public class Utils 
{
	private static final String SAVE_DIR = "/DemoSave/";
	public static final String SOURCE_PATH = getSDPath() + "/DemoSource/";
	public static final String SAVE_PATH = getSaveDir();


	/**
	 * getSaveDir
	 * @return
	 */
	public static String getSaveDir()
	{
		String workDir = null;
		String SDPath = getSDPath();
		if(null != SDPath)
		{
			String path = SDPath + SAVE_DIR;
			File file = new File(path);
			
			//不存在则创建
			if(!file.exists())
			{
				if(file.mkdirs())
				{
					workDir = path;
				}
			}
			else 
			{
				workDir = path;
			}
		}
		return workDir;
	}
	
	/**
	 * get SD Path
	 * @return sd path
	 */
	public static String getSDPath()
	{ 
		File sdDir = null; 
		
		try
		{
			boolean sdCardExist = Environment.getExternalStorageState() 
					.equals(Environment.MEDIA_MOUNTED); 
			if (sdCardExist) 
			{ 
				sdDir = Environment.getExternalStorageDirectory();
				return sdDir.getCanonicalPath();
			}
			Log.w("Utils","Utils|getSDPath: SD card is not exsit!");
			return null;
			
		} catch (Exception e)
		{
			Log.w("Utils", "Utils|getSDPath", e.getCause());
			return null;
		}
	}
}

