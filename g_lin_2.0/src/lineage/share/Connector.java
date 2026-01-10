package lineage.share;

import java.io.BufferedReader;
import java.io.FileReader;

public final class Connector {

	static public boolean is_load = false;

	static public boolean LINX_decrypt_Packet = false;
	static public String LINX_decrypt_Key = null;

	static public void init() {
		is_load = true;

		TimeLine.start("Connector..");
		String line = null;

		try (BufferedReader lnrr = new BufferedReader(new FileReader("connector.conf"))) {

			while ((line = lnrr.readLine()) != null) {
				if (line.startsWith("#"))
					continue;

				int pos = line.indexOf("=");
				if (pos > 0) {
					String key = line.substring(0, pos).trim();
					String value = line.substring(pos + 1, line.length()).trim();

					if (key.equalsIgnoreCase("LINX_decrypt_Packet"))
						LINX_decrypt_Packet = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("LINX_decrypt_Key"))
						LINX_decrypt_Key = value;
				}
			}
			lnrr.close();

		} catch (Exception e) {
			lineage.share.System.printf("%s : init()\r\n", Connector.class.toString());
			lineage.share.System.println(String.format("에러 라인 -> %s", line == null ? "라인 없음" : line));
			lineage.share.System.println(e);
		} finally {
			is_load = false;
			TimeLine.end();
		}
	}
}