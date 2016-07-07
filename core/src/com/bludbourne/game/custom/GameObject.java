package com.bludbourne.game.custom;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

public abstract class GameObject {
	Array<Component> components = new Array<Component>();
	
	public void update(float deltaTime){
		for(Component component : components){
			component.update(deltaTime);
		}
	}
	public void draw(Batch batch){
		for(Component component : components){
			component.draw(batch);
		}
	}
}
