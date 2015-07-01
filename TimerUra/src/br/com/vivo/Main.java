package br.com.vivo;

import java.util.Timer;

public class Main {
	/*OK*/
	public static void main(String[] args) {
		Timer timer = new Timer();
		int delayInMiliseconds = 5 * 60 * 1000;
		int intervalInMiliseconds = 15 * 60 * 1000;
		timer.scheduleAtFixedRate(new Task(), delayInMiliseconds, intervalInMiliseconds);
	}
}