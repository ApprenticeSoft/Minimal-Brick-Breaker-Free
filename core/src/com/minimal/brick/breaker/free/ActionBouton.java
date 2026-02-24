package com.minimal.brick.breaker.free;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.minimal.brick.breaker.free.screen.MainMenuScreen;
import com.minimal.brick.breaker.free.screen.GameScreen;

public class ActionBouton {

	public ActionBouton(){
	}
	
	public void groupeListener(TextButton bouton, final int groupe){
		bouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				GameConstants.groupeSelectione = groupe;
				GameConstants.choixNiveau = true;
			}
		});
	}
	
	public void niveauListener(final MyGdxGame game, TextButton bouton, final int niveau){
		bouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				GameConstants.niveauSelectione = niveau;
				try{
					game.music.stop();
					game.setScreen(new GameScreen(game));
				}
				catch(Exception e){
					game.setScreen(new MainMenuScreen(game));
				}
			}
		});
	}
	
	public void retourListener(final MyGdxGame game, TextButton bouton){
		bouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				if(GameConstants.choixNiveau) 
					GameConstants.choixNiveau = false;
				else {
					game.setScreen(new MainMenuScreen(game));
				}
			}
		});
	}
}
