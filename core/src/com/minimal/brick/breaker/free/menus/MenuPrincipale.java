package com.minimal.brick.breaker.free.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.minimal.brick.breaker.free.MyGdxGame;
import com.minimal.brick.breaker.free.screen.NiveauxScreen;
import com.minimal.brick.breaker.free.screen.OptionScreen;

public class MenuPrincipale {

	final MyGdxGame game;
	private TextButtonStyle textButtonStyle;
	private TextButton startBouton, optionBouton;
	private Image transitionImage;
	
	public MenuPrincipale(final MyGdxGame gam, Skin skin, Stage stage, Color couleur){	
		game = gam;
		
		textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("BoutonPatch");
		textButtonStyle.down = skin.getDrawable("BoutonCheckedPatch");	
		textButtonStyle.font = game.assets.get("font1.ttf", BitmapFont.class);
		textButtonStyle.fontColor = Color.WHITE;
		textButtonStyle.downFontColor = new Color(0.27f, 0.695f, 0.613f, 1);
		
		startBouton = new TextButton(gam.langue.commencer, textButtonStyle);
		startBouton.setWidth(Gdx.graphics.getWidth()/3);
		startBouton.setHeight(Gdx.graphics.getHeight()/12);
		startBouton.setX(Gdx.graphics.getWidth()/2 - startBouton.getWidth()/2);
		startBouton.setY(60*Gdx.graphics.getHeight()/100 - startBouton.getHeight()/2);
		
		optionBouton = new TextButton(gam.langue.options, textButtonStyle);
		optionBouton.setWidth(Gdx.graphics.getWidth()/3);
		optionBouton.setHeight(Gdx.graphics.getHeight()/12);
		optionBouton.setX(startBouton.getX());
		optionBouton.setY(startBouton.getY() - 1.1f*optionBouton.getHeight());
		
		transitionImage = new Image(skin.getDrawable("Barre"));
		transitionImage.setWidth(Gdx.graphics.getWidth());
		transitionImage.setHeight(Gdx.graphics.getHeight());
		transitionImage.setColor(couleur);
		transitionImage.setX(-Gdx.graphics.getWidth());
		transitionImage.setY(0);
		transitionImage.addAction(Actions.alpha(0));
		transitionImage.setTouchable(Touchable.disabled);
		
		stage.addActor(startBouton);
		stage.addActor(optionBouton);
		stage.addActor(transitionImage);

		startBouton.addAction(Actions.parallel(Actions.alpha(0), Actions.addAction(Actions.alpha(0), optionBouton)));
		startBouton.addAction(Actions.sequence(Actions.delay(0.1f), Actions.alpha(1, 0.1f), Actions.addAction(Actions.alpha(1, 0.1f), optionBouton)));
	}
	
	public void boutonListener(){

		startBouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				transitionImage.addAction(Actions.moveTo(0, 0));
				transitionImage.addAction(Actions.sequence(Actions.alpha(1, 0.2f),	 
															Actions.run(new Runnable() {
													            @Override
													            public void run() {
																	game.setScreen(new NiveauxScreen(game));
													            }})));
			}
		});
		
		optionBouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				transitionImage.addAction(Actions.moveTo(0, 0));
				transitionImage.addAction(Actions.sequence(Actions.alpha(1, 0.2f),	 
															Actions.run(new Runnable() {
													            @Override
													            public void run() {
																	game.setScreen(new OptionScreen(game));
													            }})));
			}
		});
	}
}
