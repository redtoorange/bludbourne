package com.bludbourne.game;

import com.badlogic.gdx.Game;

public class BludBourne extends Game {

	public static final MainGameScreen _mainGameScreen = new MainGameScreen();
	
	@Override
	public void create() {
		setScreen(_mainGameScreen);
	}
	
	@Override
	public void dispose(){
		_mainGameScreen.dispose();
	}
}
