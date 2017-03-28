package cn.simafei.test.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件通用类
 */
public class FileUtil {

	public static boolean writeFile(InputStream is, String filePath) {
		File file = new File(filePath);
		if (!file.getParentFile().exists()) {
			//noinspection ResultOfMethodCallIgnored
			file.getParentFile().mkdirs();
		}
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		byte[] buffer = new byte[10 * 1024];
		int ch;
		try {
			while ((ch = is.read(buffer)) != -1) {
				fos.write(buffer, 0, ch);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				is.close();
				fos.flush();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
