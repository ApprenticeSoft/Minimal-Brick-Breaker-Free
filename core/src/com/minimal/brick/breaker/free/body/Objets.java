package com.minimal.brick.breaker.free.body;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.minimal.brick.breaker.free.GameConstants;

public class Objets extends PolygonShape{
	
	public Body body;
	private BodyDef bodyDef;
	private FixtureDef fixtureDef;
	private final World world;
	private Camera camera;
	private int type, balleRandom;
	private float  width, height;
	private Array<Balle> balles;
	private String stringObjet;
    
	public Objets(World world, Camera camera, float posX, float posY, Barre barre, Array<Balle> balles, Array<Laser> lasers){
		this.world = world;
		this.camera = camera;
		this.balles = balles;
		
		type = MathUtils.random(1,GameConstants.objet);
		while(type == GameConstants.dernierObjet || type == GameConstants.avantDernierObjet){
			type = MathUtils.random(1,GameConstants.objet);	
		}
			if(GameConstants.briquesDetruitesAuLaser > (int)(0.35f*GameConstants.briquesInitiales) && type == 8){
				type = MathUtils.random(1,GameConstants.objet);	
			}
			
		GameConstants.avantDernierObjet = GameConstants.dernierObjet;
		GameConstants.dernierObjet = type;
		
		bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody; 
        
        if(type == 1){
        	width = camera.viewportWidth/24;
        	height = 0.33f*camera.viewportWidth/24;
        	stringObjet = "BarreMax";
        }
        else if(type == 2){
        	width = camera.viewportWidth/24;
        	height = 0.33f*camera.viewportWidth/24;
        	stringObjet = "BarreMin";
        }
        else if(type == 3){
        	width = camera.viewportWidth/24;
        	height = 0.5f*camera.viewportWidth/24;
        	stringObjet = "VitesseMin";
        }
        else if(type == 4){
        	width = camera.viewportWidth/24;
        	height = 0.5f*camera.viewportWidth/24;
        	stringObjet = "VitesseMax";
        }
        else if (type == 5){
        	width = camera.viewportWidth/50;
        	height = camera.viewportWidth/50;
        	stringObjet = "Balle";
        }
        else if (type == 6){
        	width = camera.viewportWidth/24;
        	height = camera.viewportWidth/24;
        	stringObjet = "MultiBalles";
        }
        else if (type == 7){
        	width = 0.85f*camera.viewportWidth/24;
        	height = camera.viewportWidth/24;
        	stringObjet = "Bouclier";
        }
        else if (type == 8){
        	width = camera.viewportWidth/24;
        	height = camera.viewportWidth/24;
        	stringObjet = "BalleLaser";
        }
        else if (type == 9){
        	width = 0.38f*camera.viewportWidth/24;
        	height = camera.viewportWidth/24;
        	stringObjet = "Laser";
        }
		
    	this.setAsBox(width, height);
        
		fixtureDef = new FixtureDef();
		fixtureDef.shape = this;
        fixtureDef.density = 1.0f;  
        fixtureDef.friction = 0.0f;  
        fixtureDef.restitution = 1f; 
        
        bodyDef.position.set(posX, posY);
        body = world.createBody(bodyDef);
        body.setUserData("Objet");
        body.createFixture(fixtureDef);
		body.setLinearVelocity(0, -camera.viewportHeight/8);
        body.setFixedRotation(true);	
	}
	
	public void objetActif(){
		if(type == 1){ 													//Augmentation de la taille de la barre
			if(GameConstants.ecart == GameConstants.barreNormale)
				GameConstants.ecart = GameConstants.barreMax;
			else if(GameConstants.ecart == GameConstants.barreMin)
				GameConstants.ecart = GameConstants.barreNormale;
		}
		else if(type == 2){												//Diminution de la taille de la barre
			if(GameConstants.ecart == GameConstants.barreNormale)
				GameConstants.ecart = GameConstants.barreMin;
			else if(GameConstants.ecart == GameConstants.barreMax)
				GameConstants.ecart = GameConstants.barreNormale;
		}
		else if(type == 3){												//Diminution de la vitesse
			GameConstants.vitesseBalleTime = TimeUtils.millis();
			GameConstants.vitesseBalle = GameConstants.vitesseBalleMin;	
		}
		else if(type == 4){												//Augmentation de la vitesse
			GameConstants.vitesseBalleTime = TimeUtils.millis();
			GameConstants.vitesseBalle = GameConstants.vitesseBalleMax;	
		}
		else if(type == 5){												//Balle suplémentaire
			Balle balle = new Balle(world, camera, this.body.getPosition().x, this.body.getPosition().y);
			balle.balleActive = true;
			balle.startImpulse = true;
			balles.add(balle);
			balle.body.applyLinearImpulse(0f, -1f, balle.body.getPosition().x, balle.body.getPosition().y, true);
		}
		else if(type == 6){												//Balles suplémentaires
			balleRandom = MathUtils.random(0, balles.size-1);
			Balle balle = new Balle(world, camera, balles.get(balleRandom).body.getPosition().x, balles.get(balleRandom).body.getPosition().y);
			balle.balleActive = true;
			balle.startImpulse = true;
			balles.add(balle);
			Vector2 impulseA = balles.get(balleRandom).body.getLinearVelocity().cpy().rotate(120);
			balle.body.applyLinearImpulse(impulseA.x, impulseA.y, balle.body.getPosition().x, balle.body.getPosition().y, true);
			
			Balle balle2 = new Balle(world, camera, balles.get(balleRandom).body.getPosition().x, balles.get(balleRandom).body.getPosition().y);
			balle2.balleActive = true;
			balle2.startImpulse = true;
			balles.add(balle2);
			Vector2 impulseB = balles.get(balleRandom).body.getLinearVelocity().cpy().rotate(-120);
			balle2.body.applyLinearImpulse(impulseB.x, impulseB.y, balle.body.getPosition().x, balle.body.getPosition().y, true);
		}
		else if(type == 7){												//Activation du bouclier
			GameConstants.bouclierActif = true;
		}
		else if(type == 8){												//Activation de la balle laser
			balleRandom = MathUtils.random(0, balles.size-1);
			balles.get(balleRandom).body.setUserData("BalleLaser");
			balles .get(balleRandom). balleLaserContact = false;
		}
		else if(type == 9){												//Activation du laser
			GameConstants.laserActif = true;
			GameConstants.tirs = 0;
			GameConstants.laserTime = TimeUtils.millis();
		}
	}
	
	public float getWidth(){
		return width;
	}
	
	public float getHeight(){
		return height;
	}
	
	public void draw(SpriteBatch batch, TextureAtlas textureAtlas){
		batch.setColor(0,0,0,0.2f);
		batch.draw(textureAtlas.findRegion(stringObjet), 
				GameConstants.BOX_TO_WORLD * (this.body.getPosition().x - this.getWidth()) + Gdx.graphics.getWidth()/80, 
				GameConstants.BOX_TO_WORLD * (this.body.getPosition().y - this.getHeight()) - Gdx.graphics.getWidth()/68, 
				GameConstants.BOX_TO_WORLD * 2 * this.getWidth(), 
				GameConstants.BOX_TO_WORLD * 2 * this.getHeight());
		batch.setColor(1,1,1,1);
		batch.draw(textureAtlas.findRegion(stringObjet), 
				GameConstants.BOX_TO_WORLD * (this.body.getPosition().x - this.getWidth()), 
				GameConstants.BOX_TO_WORLD * (this.body.getPosition().y - this.getHeight()), 
				GameConstants.BOX_TO_WORLD * 2 * this.getWidth(), 
				GameConstants.BOX_TO_WORLD * 2 * this.getHeight());
	}
	
	public static void detruire(Array<Objets> array, World world){
		for(int i = 0; i < array.size; i++){
        	if(!array.get(i).body.isAwake()){
        		array.get(i).objetActif();
        		array.get(i).body.setActive(false);
        		world.destroyBody(array.get(i).body);
        		array.removeIndex(i);
        	}
        	else if(array.get(i).body.getPosition().y < -2*array.get(i).getHeight()){
        		array.get(i).body.setActive(false);
        		world.destroyBody(array.get(i).body);
        		array.removeIndex(i);
        	}
        }
	}
}
