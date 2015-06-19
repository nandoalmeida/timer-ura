package br.com.vivo;

import java.util.Timer;

public class Main {
	public static void main(String[] args) {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new Task(), 1000, (5555 * 60 * 1000));
	}
}