package com.nicola.gameoflife.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.nicola.gameoflife.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 650;
		config.resizable = false;
		config.title = "Game of Life";
		new LwjglApplication(new Main(), config);
	}
}
