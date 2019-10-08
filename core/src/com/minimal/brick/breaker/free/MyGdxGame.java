package com.minimal.brick.breaker.free;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.minimal.brick.breaker.free.Donnees;
import com.minimal.brick.breaker.free.screen.LoadingScreen;

public class MyGdxGame extends Game implements ApplicationListener {
	public SpriteBatch batch;
	public AssetManager assets;
	public Langue langue;
	public ActionResolver actionResolver;
	public Music music;
	

	public MyGdxGame(ActionResolver actionResolver){
		this.actionResolver = actionResolver;
	}
	
	@Override
	public void create () {
		
		Donnees.Load();
		if(Donnees.getGroupe() < 1)
			Donnees.setGroupe(1);
		if(Donnees.getNiveau() < 1)
			Donnees.setNiveau(1);
		
		if(Donnees.getGroupe() > 3 && !Donnees.getMicrogravite())
			Donnees.setMicrogravite(true);
		
		//Donnees.setGroupe(4);
		//Donnees.setNiveau(25);
		//Donnees.setGroupe(2);
		//Donnees.setNiveau(17);
		//Donnees.setEpileptique(true);
		//Donnees.setMicrogravite(true);
		
		Donnees.setInterstitial(Donnees.getInterstitial() + 1);

		if(!Donnees.getRate()){
			Donnees.setRateCount(Donnees.getRateCount() - 1);
		}
		
		batch = new SpriteBatch();
		assets = new AssetManager();
		langue = new Langue();
		langue.setLangue(Donnees.getLangue());
		
		music = Gdx.audio.newMusic(Gdx.files.internal("Sons/Minimal Brick Breaker - Menu.ogg"));
		music.setLooping(true);
		
		this.setScreen(new LoadingScreen(this));
		
		System.out.println("Donnees.getRate() = " + Donnees.getRate());
		System.out.println("Donnees.getRateCount() = " + Donnees.getRateCount());
	}

	@Override
	public void render () {
		super.render();

		if(!Donnees.getSon())
			music.setVolume(0);
		else
			music.setVolume(1);
		
		if(!actionResolver.adsListener()){
			actionResolver.LoadInterstital();
			
			if(GameConstants.niveauFini || GameConstants.pause)
				actionResolver.showAdsTop();
			else
				actionResolver.hideAds();
			
			if(GameConstants.INTERSTITIAL_TRIGGER < 1){
				GameConstants.INTERSTITIAL_TRIGGER = MathUtils.random(1,2);
				actionResolver.showOrLoadInterstital();
			}
			
			if(Donnees.getInterstitial() > 5){
				actionResolver.showOrLoadInterstital();
				Donnees.setInterstitial(Donnees.getInterstitial() - 1);
			}
		}
		else if(actionResolver.adsListener()){
			actionResolver.hideAds();
		}
		else  {
			actionResolver.hideAds();
		}
	}
	 
	public void dispose() {
		batch.dispose();
	}	 
}
