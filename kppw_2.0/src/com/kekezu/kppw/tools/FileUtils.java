package com.kekezu.kppw.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.kekezu.kppw.activity.SplashActivity;

import android.content.Context;
import android.os.Environment;

public class FileUtils
{
	private String SDPATH;
	private String objString;

	/**
	 * 
	 */
	public FileUtils()
	{
		// TODO Auto-generated constructor stub
		// ��õ�ǰ�ⲿ�洢�豸��Ŀ¼
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}

	/**
	 * ��SD���ϴ����ļ�
	 * 
	 * @param fileName
	 * @return
	 */
	public File createSdFile(String fileName)
	{
		File file = new File(SDPATH + fileName);
		try
		{
			file.createNewFile();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return file;
	}

	/**
	 * ����SD��Ŀ¼
	 * 
	 * @param dirName
	 * @return
	 */
	public File createSDDir(String dirName)
	{
		File file = new File(SDPATH + dirName);
		file.mkdir();

		return file;
	}

	public boolean isFileExist(String fileName)
	{
		File file = new File(SDPATH + fileName);
		return file.exists();

	}

	public File writeToSDFromInput(Context context, String path,
			String fileName, InputStream input)
	{
		File file = null;
		OutputStream output = null;
		objString = fileName;
		try
		{
			createSDDir(path);
			file = createSdFile(path + fileName);
			output = new FileOutputStream(file);

			byte[] buffer = new byte[4 * 1024];
			int total = 0;
			int len = 0;
			while ((len = (input.read(buffer))) != -1)
			{
				total = total + len;
				output.write(buffer, 0, len);
				if (objString.equals("kppw3_android.apk"))
				{
					((SplashActivity) context).sendMsg(1, total);
				}

			}
			output.flush();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				output.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// �������
		if (objString.equals("kppw3_android.apk"))
		{
			((SplashActivity) context).sendMsg(2, 0);
		}
		return file;
	}

}