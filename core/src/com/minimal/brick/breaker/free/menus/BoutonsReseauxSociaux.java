package com.minimal.brick.breaker.free.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.minimal.brick.breaker.free.GameConstants;
import com.minimal.brick.breaker.free.MyGdxGame;

public class BoutonsReseauxSociaux {

	final MyGdxGame game;
	private ImageButton facebookBouton, twitterBouton, logoBouton;
	private float appliedBottomInset;
	
	public BoutonsReseauxSociaux(final MyGdxGame gam, Skin skin){
		game = gam;
		appliedBottomInset = -1f;
		
		facebookBouton = new ImageButton(skin.getDrawable("Facebook"), skin.getDrawable("FacebookDown"));
		
		twitterBouton = new ImageButton(skin.getDrawable("Twitter"), skin.getDrawable("TwitterDown"));
		
		logoBouton = new ImageButton(skin.getDrawable("Logo"), skin.getDrawable("LogoDown"));
		applyBottomInset(0f);
	}

	public void applyBottomInset(float bottomInsetPx) {
		if (Math.abs(appliedBottomInset - bottomInsetPx) < 1f) {
			return;
		}
		appliedBottomInset = bottomInsetPx;
		float iconSize = 0.071f * Gdx.graphics.getHeight();
		float margin = Gdx.graphics.getWidth() / 30f;
		float y = margin + Math.max(0f, bottomInsetPx);

		facebookBouton.setWidth(iconSize);
		facebookBouton.setHeight(iconSize);
		facebookBouton.setX(margin);
		facebookBouton.setY(y);

		twitterBouton.setWidth(iconSize);
		twitterBouton.setHeight(iconSize);
		twitterBouton.setX(facebookBouton.getX() + 1.4f * iconSize);
		twitterBouton.setY(y);

		logoBouton.setWidth(iconSize);
		logoBouton.setHeight(iconSize);
		logoBouton.setX(twitterBouton.getX() + 1.4f * iconSize);
		logoBouton.setY(y);
	}
	
	public void draw(Stage stage){
		stage.addActor(facebookBouton);
		stage.addActor(twitterBouton);
		stage.addActor(logoBouton);
	}
	
	public void action(){	
		facebookBouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				//try{
				//	Gdx.net.openURI("fb://page/157533514581396");
				//}catch(Exception e){
					Gdx.net.openURI("https://m.facebook.com/profile.php?id=157533514581396");
				//}
			}
		});
		
		twitterBouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
		       	Gdx.net.openURI("https://twitter.com/ApprenticeSoft");
			}
		});
		
		logoBouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
		       	Gdx.net.openURI(GameConstants.GOOGLE_PLAY_STORE_URL);
			}
		});
	}
}
