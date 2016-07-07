package com.bludbourne.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

//A screen represents a game state
public class MainGameScreen implements Screen{
	
	private static final String TAG = MainGameScreen.class.getName();
	
	private static class VIEWPORT{
			static float viewportWidth;
			static float viewportHeight;
			static float virtualWidth;
			static float virtualHeight;
			static float physicalWidth;
			static float physicalHeight;
			static float aspectRatio;
	}
	
	private PlayerController _controller;	// handle input for the player
	private TextureRegion _currentPlayerFrame;	//	the current frame of the player's sprite (animation)
	private Sprite _currentPlayerSprite;	// the sprite that represents the player on screen
	
	private OrthogonalTiledMapRenderer _mapRenderer = null;	//	place that handles the map and draw it
	private OrthographicCamera _camera = null;	// camera
	private static MapManager _mapMgr;		// manages the map
	
	public MainGameScreen() {
		_mapMgr = new MapManager();
	}
	
	private static Entity _player;

	@Override
	public void show() {
		// Camera Setup
		setupViewport(10, 10);
		
		//get the current size
		_camera = new OrthographicCamera();
		_camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);
		
		_mapRenderer = new OrthogonalTiledMapRenderer(_mapMgr.getCurrentMap(), MapManager.UNIT_SCALE);	//Takes in a tiledMap and a scale
		_mapRenderer.setView(_camera);
		
		Gdx.app.debug(TAG,	"UnitScale calue is : " + _mapRenderer.getUnitScale());
		
		_player = new Entity();
		_player.init(_mapMgr.getPlayerStartUnitScaled().x, _mapMgr.getPlayerStartUnitScaled().y);
		
		_currentPlayerSprite = _player.getFrameSprite();
		
		_controller = new PlayerController(_player);
		Gdx.input.setInputProcessor(_controller);		
	}

	@Override
	public void render(float delta) {	
		// called each frame of rendering, equivalent to Unit's Update();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//preferable to lock and center the _camera to the player's position
		_camera.position.set(_currentPlayerSprite.getX(), _currentPlayerSprite.getY(), 0);
		_camera.update();	//update the camera
		
		_player.update(delta);	//update the player
		
		_currentPlayerFrame = _player.getFrame();
		
		updatePortalLayerActivation(_player._boundingBox);
		if(!isCollisionWithMapLayer(_player._boundingBox)){
			_player.setNextPositionToCurrent();
		}
		
		_controller.update(delta);	// update the controller
		
		_mapRenderer.setView(_camera);
		_mapRenderer.update(delta);	// update the map renderer
		
		//Begin bathcing and draw
		_mapRenderer.getBatch().begin();
		_mapRenderer.getBatch().draw(_currentPlayerFrame, _currentPlayerSprite.getX(), _currentPlayerSprite.getY(), 1, 1);	// draw the player at his X and Y
		_mapRenderer.getBatch().end();		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {	//	clean up the memory
		_player.dispose();
		_controller.dispose();
		Gdx.input.setInputProcessor(null);	//	reset the input processor away from the player controller
		_mapRenderer.dispose();
	}
	
	private void setupViewport(int width, int height){
		// make the viewport a percentage of the total display area
		VIEWPORT.virtualWidth = width;
		VIEWPORT.virtualHeight = height;
		
		//current viewport dimensions
		VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
		VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
		
		//pixel dimensions of the display
		VIEWPORT.physicalWidth = Gdx.graphics.getWidth();
		VIEWPORT.physicalHeight = Gdx.graphics.getHeight();
		
		//aspect ratio for current viewport
		VIEWPORT.aspectRatio = (VIEWPORT.virtualWidth / VIEWPORT.viewportHeight);
		
		//update viewport of there could be skewing
		if(VIEWPORT.physicalWidth / VIEWPORT.physicalHeight >= VIEWPORT.aspectRatio){
			//Letterbox left and right
			VIEWPORT.viewportWidth = VIEWPORT.viewportHeight * (VIEWPORT.physicalWidth / VIEWPORT.physicalHeight);
			VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
		}
		else{
			//letterbox and above
			VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
			VIEWPORT.viewportHeight = VIEWPORT.viewportWidth * (VIEWPORT.physicalWidth / VIEWPORT.physicalHeight);
		}
		
		Gdx.app.debug(TAG, "World renderer: virtual: (" + VIEWPORT.virtualWidth + ", " + VIEWPORT.virtualHeight + ")");
		Gdx.app.debug(TAG, "World renderer: viewport: (" + VIEWPORT.viewportWidth + ", " + VIEWPORT.viewportHeight + ")");
		Gdx.app.debug(TAG, "World renderer: physical: (" + VIEWPORT.physicalWidth + ", " + VIEWPORT.physicalHeight + ")");
	}

	//map collision layer
	private boolean isCollisionWithMapLayer(Rectangle boundingBox){
		MapLayer mapCollisionLayer = _mapMgr.getCollisionLayer();
		
		if(mapCollisionLayer == null){
			return false;
		}
		
		Rectangle rectangle = null;
		
		for(MapObject object : mapCollisionLayer.getObjects()){
			if(object instanceof RectangleMapObject){
				rectangle = ((RectangleMapObject) object).getRectangle();
				
				//if the player's bounding box overlaps the map's object's rectangle
				if(boundingBox.overlaps(rectangle)){
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean updatePortalLayerActivation(Rectangle boundingBox){
		MapLayer mapPortalLayer = _mapMgr.getPortalLayer();
		
		if(mapPortalLayer == null){
			return false;
		}
		
		Rectangle rectangle = null;
		
		for(MapObject object : mapPortalLayer.getObjects()){
			if(object instanceof RectangleMapObject){
				rectangle = ((RectangleMapObject) object).getRectangle();
				if(boundingBox.overlaps(rectangle)){
					String mapName = object.getName();
					if(mapName== null){
						return false;
					}
					
					_mapMgr.setClosestStartPositionFromScaledUnit(_player.getCurrentPosition());
					_mapMgr.loadMap(mapName);
					
					_player.init(_mapMgr.getPlayerStartUnitScaled().x, _mapMgr.getPlayerStartUnitScaled().y);
					_mapRenderer.setMap(_mapMgr.getCurrentMap());
					Gdx.app.debug(TAG, "Portal Activated");
					return true;
				}
			}
		}
		return false;
	}
}
