package cn.senninha.sserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cn.senninha.game.map.GridStatus;

public class MapGenerateUtil {
	public static String writeToMap(List<MyButton> list) {
		File file = new File("/tmp/map" + System.currentTimeMillis() + ".resource");
		FileOutputStream fis = null;
		try {
			fis = new FileOutputStream(file);
			StringBuilder sb = new StringBuilder();
			for(int i = 0 ; i < list.size() ; i++) {
				if(list.get(i).isBlock()) {
					sb.append(GridStatus.CAN_NOT_SHOT.getStatus());
				}else {
					sb.append(GridStatus.CAN_RUN.getStatus());
				}
			}
			byte[] b = sb.toString().getBytes("utf-8");
			fis.write(b, 0, b.length);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return file.getName();
	}

}
