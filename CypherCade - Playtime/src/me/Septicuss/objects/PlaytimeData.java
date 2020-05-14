package me.Septicuss.objects;

import me.Septicuss.Files;
import me.Septicuss.Files.FileType;

public class PlaytimeData {

	private String player;
	private long seconds;

	public PlaytimeData(String player, long seconds) {
		this.seconds = seconds;
		this.player = player;
	}

	public PlaytimeData(long seconds) {
		this.seconds = seconds;
	}

	/**
	 * @return data owner
	 */
	public String getPlayer() {
		return this.player;
	}

	/**
	 * @return played time in seconds
	 */
	public long getSeconds() {
		return this.seconds;
	}

	public void addSeconds(long amount) {
		this.seconds += amount;
	}

	public void setSeconds(long seconds) {
		this.seconds = seconds;
	}

	/**
	 * @return formatted played time string
	 */
	public String getFormattedTime() {

		String format = new Files().getFile(FileType.CONFIG).getString("setting.time_format");

		long s = seconds;
		long m = 0;
		while (s > 60) {
			m++;
			s -= 60;
		}

		long h = 0;
		while (m > 60) {
			h++;
			m -= 60;
		}

		long d = 0;
		while (h > 24) {
			d++;
			h -= 24;
		}

		format = format.replaceAll(" ", "");

		if (s != 0) {
			format = format.replaceAll("%s%", s + " seconds ");
		}

		format = format.replace("%s%", "");

		if (m != 0) {
			format = format.replaceAll("%m%", m + " minutes ");
		}

		format = format.replace("%m%", "");

		if (h != 0) {
			format = format.replaceAll("%h%", h + " hours ");
		}

		format = format.replace("%h%", "");

		if (d != 0) {
			format = format.replaceAll("%d%", d + " days ");
		}

		format = format.replace("%d%", "");

		return format;

	}

}
