package br.com.vivo;

import java.util.Timer;

public class Main {
	public static void main(String[] args) {
		Timer timer = new Timer();
		int delayInMiliseconds = 1000;
		int intervalInMiliseconds = 25 * 60 * 1000;
		timer.scheduleAtFixedRate(new Task(), delayInMiliseconds, intervalInMiliseconds);
	}
}