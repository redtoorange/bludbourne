package com.bludbourne.game.custom;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FloatTextureData;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.bludbourne.game.Utility;

public class Player extends GameObject {
	private Vector2 position;
	private Vector2 nextPosition;
	private Vector2 velocity;
	private float maxVelocity = 5f;
	private Sprite playerSprite;
	private float speed = 5f;
	
	public Player(){
		position = new Vector2(0, 0);
		nextPosition = new Vector2(0, 0);
		velocity = new Vector2(0, 0);
		
		Texture texture  = Utility.getTextureAsset("sprites/characters/Warrior.png");
		TextureRegion region = new TextureRegion(texture, 0, 0, 16, 16);
		playerSprite = new Sprite(region);
		playerSprite.setBounds(position.x, position.y, 16, 16);
	}
	
	public void update(float deltaTime){
		super.update(deltaTime);
		
		updatePosition(deltaTime);
		updateSpritePosition();
	}
	
	public void draw(Batch batch){
		super.draw(batch);
		
		playerSprite.draw(batch);
	}
	
	private void updatePosition(float deltaTime){
		nextPosition = position;
		
		if(!velocity.isZero()){
			nextPosition.x = velocity.x * speed * deltaTime;
			nextPosition.y = velocity.y * speed * deltaTime;
		}
		else{
			return;
		}
	}
	
	private void updateSpritePosition(){
		if(position.epsilonEquals(nextPosition, 0)) return;
		
		playerSprite.setPosition(nextPosition.x, nextPosition.y);
		position = nextPosition;
	}

	public void changeVelocity(Vector2 velocity){
		this.velocity = velocity;
		this.velocity.x = MathUtils.clamp(this.velocity.x, -maxVelocity, maxVelocity);
		this.velocity.y = MathUtils.clamp(this.velocity.y, -maxVelocity, maxVelocity);
	}
}
