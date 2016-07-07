package com.bludbourne.game.custom;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface Component {
	public void update(float deltaTime);
	public void draw(Batch batch);
}
