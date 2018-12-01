package com.kekezu.kppw.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.kekezu.kppw.activity.SplashActivity;

import android.content.Context;

public class HttpDownloader
{
	private URL url = null;
	private String objString;

	/**
	 * ���URL�����ļ���ǰ�����ļ����е�����Ϊ�ı�������ֵ�����ļ����е�����
	 * 
	 * @param urlStr
	 * @return
	 */
	public String download(String urlStr)
	{

		StringBuffer buffer = new StringBuffer();
		String line = null;
		BufferedReader reader = null;

		try
		{
			url = new URL(urlStr);
			try
			{
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				reader = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				while ((line = reader.readLine()) != null)
				{
					buffer.append(line);
				}
			}
			catch (IOException e)
			{

				e.printStackTrace();
			}

		}
		catch (MalformedURLException e)
		{

			e.printStackTrace();
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return buffer.toString();

	}

	/**
	 * �ú�������Σ� -1������س��?0������سɹ���1��������ļ��Ѵ���
	 * 
	 * @param urlStr
	 * @param path
	 * @param fileName
	 * @return
	 */
	public int download(Context context, String path, String fileName)
	{
		InputStream input = null;
		FileUtils fileUtils = new FileUtils();
		objString = fileName;
		if (fileUtils.isFileExist(path + fileName)
				&& objString.equals("kppw3_android.apk"))
		{
			((SplashActivity) context).sendMsg(2, 0);
			return 1;
		}
		else
		{
			try
			{
				input = getInputStreamFromUrl(context,
						"http://www.kppw.cn/download/KPPW.apk");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			File resultFile = fileUtils.writeToSDFromInput(context, path,
					fileName, input);
			if (resultFile == null)
			{
				return -1;
			}
		}
		return 0;
	}

	public InputStream getInputStreamFromUrl(Context context, String urlStr)
			throws IOException
	{
		url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		InputStream s = conn.getInputStream();
		if (objString.equals("kppw3_android.apk"))
		{
			((SplashActivity) context).sendMsg(0, conn.getContentLength());
		}
		return s;
	}
}