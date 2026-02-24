package com.minimal.brick.breaker.free.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.minimal.brick.breaker.free.MyGdxGame;
import com.minimal.brick.breaker.free.GameConstants;
import com.minimal.brick.breaker.free.menus.MenuPrincipale;
import com.minimal.brick.breaker.free.ui.UiActorUtils;

public class MainMenuScreen implements Screen {

	final MyGdxGame game;
	OrthographicCamera camera;
	private Stage stage;
	private Skin skin;
	private TextureAtlas textureAtlas;
	private BitmapFont fontTitre;
	private float titleWidth;
	private MenuPrincipale menuPrincipale;
	private boolean listenersBound;
	
	public MainMenuScreen(final MyGdxGame gam){
		game = gam;
		listenersBound = false;
	
		game.ensureMenuMusic();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		GameConstants.niveauFini = true;
		
		stage = new Stage();
		skin = new Skin();
		textureAtlas = game.assets.get("Images.pack", TextureAtlas.class);
		skin.addRegions(textureAtlas);
		
		fontTitre = game.assets.get("fontTitre.ttf", BitmapFont.class);
		titleWidth = new GlyphLayout(fontTitre, "MINIMAL BRICK BREAKER").width;
		
		menuPrincipale = new MenuPrincipale(gam, skin, stage, new Color(0.27f, 0.695f, 0.613f, 1));
	}
	
	@Override
	public void render(float delta) {
		game.ensureMenuMusic();
		Gdx.gl.glClearColor(0.27f, 0.695f, 0.613f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
			fontTitre.draw(game.batch, "MINIMAL BRICK BREAKER", 
							Gdx.graphics.getWidth()/2 - titleWidth/2, 
							85*Gdx.graphics.getHeight()/100);
		game.batch.end();
		stage.act();
		stage.draw();
		
		if (Gdx.input.isKeyJustPressed(Keys.BACK))	
			Gdx.app.exit();
	}

	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width, height);
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		game.actionResolver.showBanner();
		if (Gdx.app.getType() == com.badlogic.gdx.Application.ApplicationType.WebGL) {
			UiActorUtils.centerTextButtons(stage.getRoot());
		}
		stage.addCaptureListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				game.ensureMenuMusic();
				return false;
			}
		});
		if (listenersBound) {
			return;
		}
		listenersBound = true;

		menuPrincipale.boutonListener();
	}

	@Override
	public void hide() {
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
	public void dispose() {
		skin.dispose();
		stage.dispose();
	}

}
