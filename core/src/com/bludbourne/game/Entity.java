package com.bludbourne.game;

import java.util.UUID;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Entity {
	private static final String TAG = Entity.class.getName();
	private static final String _defaultSpritePath = "sprites/characters/Warrior.png";
	
	private Vector2 _velocity;
	private String _entityID;
	
	private Direction _currentDirection = Direction.LEFT;
	private Direction _previousDirection = Direction.UP;
	
	private Animation _walkLeftAnimation;
	private Animation _walkRightAnimation;
	private Animation _walkUpAnimation;
	private Animation _walkDownAnimation;
	
	private Array<TextureRegion> _walkLeftFrames;
	private Array<TextureRegion> _walkRightFrames;
	private Array<TextureRegion> _walkUpFrames;
	private Array<TextureRegion> _walkDownFrames;
	
	protected Vector2 _nextPlayerPosition;
	protected Vector2 _currentPlayerPosition;
	protected State _state = State.IDLE;
	protected float _frameTime = 0f;
	protected Sprite _frameSprite = null;
	protected TextureRegion _currentFrame = null;
	
	public final int FRAME_WIDTH = 16;
	public final int FRAME_HEIGHT = 16;
	public static Rectangle _boundingBox;
	
	public enum State{
		IDLE, WALKING
	}
	
	public enum Direction{
		UP, DOWN, RIGHT, LEFT
	}
	
	public Entity(){
		initEntity();
	}
	
	public void initEntity(){
		this._entityID = UUID.randomUUID().toString();
		this._nextPlayerPosition = new Vector2();
		this._currentPlayerPosition = new Vector2();
		this._boundingBox = new Rectangle();
		this._velocity = new Vector2(2f, 2f);
		
		Utility.loadTextureAsset(_defaultSpritePath);
		loadDefaultSprite();
		loadAllAnimations();
	}
	
	public void update(float delta){
		_frameTime = (_frameTime + delta) % 5;	//avoid overflow
		
		//we want the hitbix to be at the feet for a better feel
		setBoundingBoxSize(0f, 0.5f);
	}
	
	public void init(float startX, float startY){
		this._currentPlayerPosition.set(startX, startY);
		this._nextPlayerPosition.set(startX, startY);
	}
	
	public void setBoundingBoxSize(float percentageWidthReduced, float percentageHeightReduced){
		//Update the current bounding box
		float width;
		float height;
		
		float widthReductionAmount = 1.0f - percentageWidthReduced;
		float heightReductionAmount = 1.0f - percentageHeightReduced;
		
		if(widthReductionAmount > 0 && widthReductionAmount < 1){
			width = FRAME_WIDTH * widthReductionAmount;
		}
		else{
			width = FRAME_WIDTH;
		}
		
		if(heightReductionAmount > 0 && heightReductionAmount < 1){
			height = FRAME_WIDTH * heightReductionAmount;
		}
		else{
			height = FRAME_WIDTH;
		}
		
		if(width == 0 || height == 0){
			Gdx.app.debug(TAG, "Width or Height are 0!! " + width + " : " + height);
		}
		
		//need to account for the unitscale, since the map coordinates will be in pixels
		float minX;
		float minY;
		
		if(MapManager.UNIT_SCALE > 0){
			minX = _nextPlayerPosition.x / MapManager.UNIT_SCALE;
			minY = _nextPlayerPosition.y / MapManager.UNIT_SCALE;
		}
		else{
			minX = _nextPlayerPosition.x;
			minY = _nextPlayerPosition.y;
		}
		
		_boundingBox.set(minX, minY, width, height);
	}
	
	private void loadDefaultSprite(){
		Texture texture = Utility.getTextureAsset(_defaultSpritePath);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
		_frameSprite = new Sprite(textureFrames[0][0].getTexture(), 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		_currentFrame = textureFrames[0][0];
	}
	
	//chop up the sprite sheet
	private void loadAllAnimations(){
		//walking animation
		Texture texture = Utility.getTextureAsset(_defaultSpritePath);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
		
		//holders for the animations
		_walkDownFrames = new Array<TextureRegion>(4);
		_walkLeftFrames = new Array<TextureRegion>(4);
		_walkRightFrames = new Array<TextureRegion>(4);
		_walkUpFrames = new Array<TextureRegion>(4);
		
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				TextureRegion region = textureFrames[i][j];
				if(region == null){
					Gdx.app.debug(TAG, "Got null animation frame " + i + ", " + j);
				}
				
				switch(i){
					case 0:
						_walkDownFrames.insert(j, region);
						break;
					case 1:
						_walkLeftFrames.insert(j, region);
						break;
					case 2:
						_walkRightFrames.insert(j, region);
						break;
					case 3:
						_walkUpFrames.insert(j, region);
						break;
				}
			}
		}
		
		//Insert the texture regions into an animation
		_walkDownAnimation = new Animation(0.25f, _walkDownFrames, Animation.PlayMode.LOOP);
		_walkLeftAnimation = new Animation(0.25f, _walkLeftFrames, Animation.PlayMode.LOOP);
		_walkRightAnimation = new Animation(0.25f, _walkRightFrames, Animation.PlayMode.LOOP);
		_walkUpAnimation = new Animation(0.25f, _walkUpFrames, Animation.PlayMode.LOOP);
	}
	
	public void dispose(){
		Utility.unloadAsset(_defaultSpritePath);
	}
	
	public void setState(State state){
		this._state = state;
	}
	
	public Sprite getFrameSprite(){
		return _frameSprite;
	}
	
	public TextureRegion getFrame(){
		return _currentFrame;
	}
	
	public Vector2 getCurrentPosition(){
		return _currentPlayerPosition;
	}
	
	public void setCurrentPosition(Vector2 position){
		_frameSprite.setX(position.x);
		_frameSprite.setY(position.y);
		
		this._currentPlayerPosition = position;
	}
	
	// called on a user's input to change direction, updates which animation we will be using
	public void setDirection(Direction direction, float deltaTime){
		this._previousDirection = this._currentDirection;
		this._currentDirection = direction;
		
		//Look into the appropriate variable when changing position
		switch(_currentDirection){
				case DOWN:
					_currentFrame = _walkDownAnimation.getKeyFrame(deltaTime);
					break;
				case LEFT:
					_currentFrame = _walkLeftAnimation.getKeyFrame(deltaTime);
					break;
				case UP:
					_currentFrame = _walkUpAnimation.getKeyFrame(deltaTime);
					break;
				case RIGHT:
					_currentFrame = _walkRightAnimation.getKeyFrame(deltaTime);
					break;
		}
	}
	
	public void setNextPosition(){
		setCurrentPosition(_nextPlayerPosition);
	}	
	
	//Method of collision detection.  Look ahead one frame to see what is there
	public void calculateNextPosition(Direction currentDirection, float deltaTime){
		float testX = _currentPlayerPosition.x;
		float testY  = _currentPlayerPosition.y;
		
		_velocity.scl(deltaTime);
		
		switch(currentDirection){
			case LEFT:
				testX -= _velocity.x;
				break;
			case RIGHT:
				testX += _velocity.x;
				break;
			case UP:
				testY += _velocity.y;
				break;
			case DOWN:
				testY -= _velocity.y;
				break;
			default:
				break;
		}
		
		_nextPlayerPosition.set(testX, testY);
		
		//velocity
		_velocity.scl(1 / deltaTime);
	}

	public void setNextPositionToCurrent() {
		_nextPlayerPosition = _currentPlayerPosition;		
	}
}
// EOF