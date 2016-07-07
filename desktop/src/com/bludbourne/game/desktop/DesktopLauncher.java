package com.bludbourne.game.desktop;



import com.badlogic.gdx.Application;	// responsible for setting up the window, handling resize events, rendering to surfaces and managing the application during its lifetime
										// provides modules for dealing with graphics, audio, input and file I/O, logging, memory footprint info and hooks for library extension

import com.badlogic.gdx.Gdx;			// environment class that holds static instances of Application, Graphics, Audio, Input, Files and Net modules for access

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;	// implementation of Application for the desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;	// provies a single point of reference for all properties assocviate with the desktop game

import com.bludbourne.game.BludBourne;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "Blood Bourne";
		config.useGL30 = false;
		config.width = 800;
		config.height = 600;
		
		
		
		Application app = new LwjglApplication(new BludBourne(), config);
		Gdx.app = app;
		
		//All the available log levels
		//Gdx.app.setLogLevel(Application.LOG_INFO);
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		//Gdx.app.setLogLevel(Application.LOG_ERROR);
		//Gdx.app.setLogLevel(Application.LOG_NONE);
	}
}
