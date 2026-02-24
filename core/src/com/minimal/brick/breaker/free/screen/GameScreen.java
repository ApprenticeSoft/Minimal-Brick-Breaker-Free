package com.minimal.brick.breaker.free.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.minimal.brick.breaker.free.Couleurs;
import com.minimal.brick.breaker.free.Donnees;
import com.minimal.brick.breaker.free.LevelHandler;
import com.minimal.brick.breaker.free.MyGdxGame;
import com.minimal.brick.breaker.free.Sons;
import com.minimal.brick.breaker.free.GameConstants;
import com.minimal.brick.breaker.free.body.Balle;
import com.minimal.brick.breaker.free.body.Barre;
import com.minimal.brick.breaker.free.body.Bouclier;
import com.minimal.brick.breaker.free.body.Brique;
import com.minimal.brick.breaker.free.body.Laser;
import com.minimal.brick.breaker.free.body.Objets;
import com.minimal.brick.breaker.free.ui.UiActorUtils;

public class GameScreen extends InputAdapter implements Screen{

	private static final int OBJECT_DROP_CHANCE_ON_BALL_HIT = 31; // 26% +20% ~= 31%
	private static final int OBJECT_DROP_CHANCE_ON_LASER_BALL_HIT = 20; // 17% +20% ~= 20%
	private static final int OBJECT_DROP_CHANCE_ON_LASER_HIT = 29; // 24% +20% ~= 29%
	private static final float WEB_END_SCREEN_LABEL_BASE_SCALE = 0.5f;
	private static final float MOBILE_END_SCREEN_LABEL_BASE_SCALE = 2f;

	final MyGdxGame game;
	OrthographicCamera camera;
	World world; 
    static int son, spawnObjet;    
    private PolygonShape boxBordure;
    private BodyDef bordureBodyDef;
    private Body bodyHaut, bodyGauche, bodyDroite;
	private Skin skin;
	private TextureAtlas textureAtlas;
	private TextureRegion regionBarre;
	private TextureRegion regionBalle;
	boolean spawn;
	
	Stage stage;
	Table tableFin, tablePause, tablePerdu;
	private Image imagePause;
	TextButtonStyle textButtonStyle, textButtonStyleFini;
	TextButton nextBouton, replayBouton, 
				resumeBouton, restartBouton, restartBouton2, menuBouton, menuBouton2, 
				groupeFini, groupeDebloque, jeuFini,
				quitBouton, pauseBouton,
				micrograviteDebloque, epileptiqueDebloque;
	private Texture pauseButtonUpTexture;
	private Texture pauseButtonDownTexture;
	LabelStyle labelStyle;
	Label labelComplete, labelPerdu;
	
	private Array<Brique> briques;
	private Array<Balle> balles;
	private Array<Objets> objets;
	private Array<Laser> lasers;
	private Balle balle;
	private Barre barre;
	private Bouclier bouclier;
	
	private LevelHandler level;
	private Couleurs couleurs;
	private Sons sons;
	private Sound sonLaser, sonNiveauFini, sonPerdu;

	long lastDropTime;
	private int couleurEdit, couleurMin, couleurMax;
	private int appliedColorGroup;
	float spawnX, spawnY;
	private boolean adBreakSent;
	private boolean bannerVisible;
	private boolean listenersBound;
	private final boolean webBuild;
	private final boolean showPauseButton;
	private final float baseScreenWidth;
	private final float baseScreenHeight;
	private Cell<Label> tableFinLabelCell;
	private Cell<TextButton> tableFinNextCell;
	private Cell<TextButton> tableFinMenuCell;
	private Cell<Label> tablePerduLabelCell;
	private Cell<TextButton> tablePerduReplayCell;
	private Cell<TextButton> tablePerduMenuCell;
	
	Box2DDebugRenderer debugRenderer; 
	
	public GameScreen(final MyGdxGame gam){
		game = gam;
		adBreakSent = false;
		bannerVisible = false;
		listenersBound = false;
		webBuild = Gdx.app.getType() == com.badlogic.gdx.Application.ApplicationType.WebGL;
		showPauseButton = !webBuild;
		baseScreenWidth = Math.max(1f, Gdx.graphics.getWidth());
		baseScreenHeight = Math.max(1f, Gdx.graphics.getHeight());
		couleurEdit = GameConstants.groupeSelectione;
		appliedColorGroup = -1;
		if(GameConstants.epileptique){
			couleurMin = 1;
			couleurMax = 19;
		}
		else{
			couleurMin = GameConstants.groupeSelectione;
			couleurMax = GameConstants.groupeSelectione;
		}
		couleurs = new Couleurs(couleurEdit);
		sons = new Sons(game, GameConstants.groupeSelectione);
		GameConstants.vitesseBalleScale = webBuild ? 1.5f : 1f;
		GameConstants.vitesseBalle = GameConstants.vitesseBalleNormale;
		GameConstants.ecart = GameConstants.barreNormale;
		GameConstants.bouclierActif = false;
		GameConstants.laserActif = false;
		GameConstants.tirs = 0;
		GameConstants.viesPerdues = 0;
		GameConstants.BOX_STEP = 1/60f;
		GameConstants.pause = false;
		GameConstants.niveauFini = false;
		
      	world = new World(new Vector2(0, 0), true); 
		World.setVelocityThreshold(0);
      	
		GameConstants.ecart = GameConstants.barreNormale;
		sonLaser = game.assets.get("Sons/Laser.ogg", Sound.class);
		sonNiveauFini = game.assets.get("Sons/NiveauFini.ogg", Sound.class);
		sonPerdu = game.assets.get("Sons/Perdu.ogg", Sound.class);
		spawn = false;
		
		camera = new OrthographicCamera();
        camera.viewportHeight = Gdx.graphics.getHeight() * GameConstants.WORLD_TO_BOX;  
        camera.viewportWidth = Gdx.graphics.getWidth() * GameConstants.WORLD_TO_BOX;  
        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0f);  
        camera.update();  
	
		skin = new Skin();
		textureAtlas = game.assets.get("Images.pack", TextureAtlas.class);
		skin.addRegions(textureAtlas);
		regionBarre = textureAtlas.findRegion("Barre");
		regionBalle = textureAtlas.findRegion("Balle");
		
        //Bordure de l'écran de jeu  
		bordureBodyDef = new BodyDef();    
        boxBordure = new PolygonShape();  
        boxBordure.setAsBox(camera.viewportWidth/2, camera.viewportHeight/100);     
        bordureBodyDef.position.set(new Vector2(camera.viewportWidth/2, 101*camera.viewportHeight/100)); 
        bodyHaut = world.createBody(bordureBodyDef);
        bodyHaut.setUserData("Haut");
        bodyHaut.createFixture(boxBordure, 0.0f); 
        
        bordureBodyDef.position.set(new Vector2(-camera.viewportWidth/100, camera.viewportHeight/2)); 
        boxBordure.setAsBox(camera.viewportWidth/100, camera.viewportHeight/2);
        bodyGauche = world.createBody(bordureBodyDef);
        bodyGauche.setUserData("Rebord");
        bodyGauche.createFixture(boxBordure, 0.0f);
        
        bordureBodyDef.position.set(new Vector2(101*camera.viewportWidth/100, camera.viewportHeight/2)); 
        bodyDroite = world.createBody(bordureBodyDef);
        bodyDroite.setUserData("Rebord");
        bodyDroite.createFixture(boxBordure, 0.0f);

        //Pad
        barre = new Barre(world, camera);
        //Bouclier
        bouclier = new Bouclier(world, camera);
        
        briques = new Array<Brique>();
        objets = new Array<Objets>();
        lasers = new Array<Laser>();
        
        level = new LevelHandler(world, camera, briques);
        level.loadLevel(GameConstants.groupeSelectione, GameConstants.niveauSelectione);
        
        balles = new Array<Balle>();
		balle = new Balle(world, camera, barre.body.getPosition().x, (barre.body.getPosition().y + camera.viewportWidth/50 + camera.viewportHeight/100));
		balles.add(balle);
		
		//Table fin de niveau
		stage = new Stage();

		textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("FondTable2");
		textButtonStyle.down = skin.getDrawable("BoutonCheckedPatch");
		textButtonStyle.font = game.assets.get("font1.ttf", BitmapFont.class);
			textButtonStyle.fontColor = Color.WHITE;
			textButtonStyle.downFontColor = new Color(0.27f, 0.695f, 0.613f, 1);
			
			nextBouton = new TextButton(gam.langue.suivant, textButtonStyle);	
			replayBouton = new TextButton(gam.langue.menu, textButtonStyle);
			TextButtonStyle pauseButtonStyle = new TextButtonStyle();
			pauseButtonUpTexture = createPauseButtonTexture(0f, 0.75f);
			pauseButtonDownTexture = createPauseButtonTexture(0.2f, 0.95f);
			pauseButtonStyle.up = new TextureRegionDrawable(new TextureRegion(pauseButtonUpTexture));
			pauseButtonStyle.down = new TextureRegionDrawable(new TextureRegion(pauseButtonDownTexture));
			pauseButtonStyle.font = game.assets.get("font1.ttf", BitmapFont.class);
			pauseButtonStyle.fontColor = Color.WHITE;
				pauseButtonStyle.downFontColor = new Color(0.27f, 0.695f, 0.613f, 1);
				
				labelStyle = new LabelStyle(game.assets.get("font1.ttf", BitmapFont.class), Color.WHITE);
				labelComplete = new Label(gam.langue.niveauComplete, labelStyle);
				labelComplete.setAlignment(Align.center);
			
			tableFin = new Table();
			tableFin.row().colspan(2);
			tableFinLabelCell = tableFin.add(labelComplete);
			tableFinLabelCell.colspan(2);
			tableFin.row().height(Gdx.graphics.getHeight()/12);
			tableFinNextCell = tableFin.add(nextBouton);
			tableFinMenuCell = tableFin.add(replayBouton);
			tableFin.setX(-Gdx.graphics.getWidth()/2);
			tableFin.setY(Gdx.graphics.getHeight()/2 + tableFin.getPrefHeight()/4);
		
		//Table pause
			resumeBouton = new TextButton(gam.langue.reprendre, textButtonStyle);
			restartBouton = new TextButton(gam.langue.recommencer, textButtonStyle);	
			menuBouton = new TextButton(gam.langue.menu, textButtonStyle);
			quitBouton = new TextButton(gam.langue.quitter, textButtonStyle);
			if (showPauseButton) {
				pauseBouton = new TextButton("II", pauseButtonStyle);
				pauseBouton.setWidth((Gdx.graphics.getWidth()/16f) * 1.3f);
				pauseBouton.setHeight(pauseBouton.getWidth());
				updatePauseButtonBounds();
			}
		
		tablePause = new Table();
		float restartTextWidth = new GlyphLayout(game.assets.get("font1.ttf", BitmapFont.class), gam.langue.recommencer).width;
		if(Donnees.getLangue() == 2)
			tablePause.defaults().height(Gdx.graphics.getHeight()/12).width(1.2f * restartTextWidth).space(Gdx.graphics.getHeight()/100);
		else
			tablePause.defaults().height(Gdx.graphics.getHeight()/12).width(2f * restartTextWidth).space(Gdx.graphics.getHeight()/100);
		tablePause.add(resumeBouton).row();
		tablePause.add(menuBouton).row();
		tablePause.add(quitBouton).row();
		tablePause.setSize(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		tablePause.setX(2 * Gdx.graphics.getWidth());
		tablePause.setY(Gdx.graphics.getHeight()/2 - tablePause.getHeight()/2);
		//tablePause.setBackground(skin.getDrawable("FondTable5"));

			//Table perdu
			tablePerdu = new Table();
			labelPerdu = new Label(gam.langue.perdu, labelStyle);
			labelPerdu.setAlignment(Align.center);
			restartBouton2 = new TextButton(gam.langue.rejouer, textButtonStyle);	
			menuBouton2 = new TextButton(gam.langue.menu, textButtonStyle);
			
			tablePerdu.row().colspan(2);
			tablePerduLabelCell = tablePerdu.add(labelPerdu);
			tablePerduLabelCell.colspan(2);
			tablePerdu.row().height(Gdx.graphics.getHeight()/12);
			tablePerduReplayCell = tablePerdu.add(restartBouton2);
			tablePerduMenuCell = tablePerdu.add(menuBouton2);
			tablePerdu.setX(-Gdx.graphics.getWidth()/2);
			tablePerdu.setY(Gdx.graphics.getHeight()/2 + tablePerdu.getPrefHeight()/4);
		
		//Bouton goupe debloqué
		textButtonStyleFini = new TextButtonStyle();
		textButtonStyleFini.up = skin.getDrawable("FondTable2");
		textButtonStyleFini.font = game.assets.get("font1.ttf", BitmapFont.class);
		textButtonStyleFini.fontColor = Color.WHITE;
		
		groupeFini = new TextButton(gam.langue.groupe + " " + Donnees.getGroupe() + " " + gam.langue.fini + " !", textButtonStyleFini);
		groupeFini.setWidth(56*Gdx.graphics.getWidth()/100);
		groupeFini.setHeight(groupeFini.getWidth()/3);
		groupeFini.setX(-Gdx.graphics.getWidth());
		groupeFini.setY(0);
		groupeFini.addAction(Actions.alpha(0));
	
		groupeDebloque = new TextButton(gam.langue.groupe + " " + (Donnees.getGroupe()+1) + " " + gam.langue.debloque + " !", textButtonStyleFini);
		groupeDebloque.setWidth(56*Gdx.graphics.getWidth()/100);
		groupeDebloque.setHeight(groupeDebloque.getWidth()/3);
		groupeDebloque.setX(-Gdx.graphics.getWidth());
		groupeDebloque.setY(0);
		groupeDebloque.addAction(Actions.alpha(0));
		
		jeuFini = new TextButton(gam.langue.jeuComplete, textButtonStyleFini);
		jeuFini.setWidth(56*Gdx.graphics.getWidth()/100);
		jeuFini.setHeight(jeuFini.getWidth()/3);
		jeuFini.setX(-Gdx.graphics.getWidth());
		jeuFini.setY(0);
		jeuFini.addAction(Actions.alpha(0));
		
		micrograviteDebloque = new TextButton(gam.langue.micrograviteDebloque, textButtonStyleFini);
		//micrograviteDebloque.setWidth(game.assets.get("font1.ttf", BitmapFont.class).getBounds(gam.langue.micrograviteDebloque).width + Gdx.graphics.getWidth()/50);
		//micrograviteDebloque.setHeight(game.assets.get("font1.ttf", BitmapFont.class).getBounds(gam.langue.micrograviteDebloque).height + Gdx.graphics.getHeight()/100);
		micrograviteDebloque.setX(-Gdx.graphics.getWidth());
		micrograviteDebloque.setY(0);
		micrograviteDebloque.addAction(Actions.alpha(0));
		
		epileptiqueDebloque = new TextButton(gam.langue.epileptiqueDebloque, textButtonStyleFini);
		//epileptiqueDebloque.setWidth(game.assets.get("font1.ttf", BitmapFont.class).getBounds(gam.langue.epileptiqueDebloque).width + Gdx.graphics.getWidth()/50);
		//epileptiqueDebloque.setHeight(game.assets.get("font1.ttf", BitmapFont.class).getBounds(gam.langue.epileptiqueDebloque).height + Gdx.graphics.getHeight()/100);
		epileptiqueDebloque.setX(-Gdx.graphics.getWidth());
		epileptiqueDebloque.setY(0);
		epileptiqueDebloque.addAction(Actions.alpha(0));
		
		imagePause = new Image(skin.getDrawable("Fond"));
		imagePause.setWidth(Gdx.graphics.getWidth());
		imagePause.setHeight(Gdx.graphics.getHeight());
		imagePause.setX(0);
		imagePause.setY(0);
		imagePause.addAction(Actions.alpha(0));
		
		stage.addActor(imagePause);
		stage.addActor(tableFin);
		stage.addActor(tablePause);
		stage.addActor(tablePerdu);
		stage.addActor(groupeDebloque);
		stage.addActor(groupeFini);
		stage.addActor(jeuFini);
		stage.addActor(micrograviteDebloque);
		stage.addActor(epileptiqueDebloque);
			if (pauseBouton != null) {
				stage.addActor(pauseBouton);
			}
			tableFin.setVisible(false);
			updateEndScreenLayout();

        debugRenderer = new Box2DDebugRenderer();
        
        GameConstants.briquesInitiales = briques.size;
        GameConstants.briquesDetruitesAuLaser = 0;
        /*
        for(BriqueBox2D brique: briques){
        	brique.body.setAwake(true);
        }
        */
	}
	
	@Override
	public void render(float delta) {  
		if (appliedColorGroup != couleurEdit) {
			couleurs.setGroupe(couleurEdit);
			for (Brique brique : briques) {
				brique.setCouleur(couleurEdit);
			}
			appliedColorGroup = couleurEdit;
		}
		
		Gdx.gl.glClearColor(couleurs.getCouleur5().r, couleurs.getCouleur5().g, couleurs.getCouleur5().b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		for(Balle balle : balles){
			balle.active();
		}

		// Déplacement de la barre
		barre.deplacement();
		bouclier.actif();
        
		// Fin du niveau
		if(briques.size == 0 && !GameConstants.niveauFini){
			sonNiveauFini.play();
			GameConstants.niveauFini = true;
			GameConstants.BOX_STEP = 0;
			imagePause.addAction(Actions.alpha(0.75f, 0.25f));

			if (!adBreakSent) {
				adBreakSent = true;
				game.actionResolver.onNaturalBreak();
			}
			

			if(GameConstants.niveauSelectione == 25 && GameConstants.groupeSelectione == GameConstants.NombreDeGroupes){
				if(Donnees.getGroupe() == GameConstants.NombreDeGroupes)
					Donnees.setGroupe(Donnees.getGroupe() + 1);
				
				Donnees.setNiveau(1);
				jeuFini.addAction(Actions.moveTo(Gdx.graphics.getWidth()/2 - jeuFini.getWidth()/2, Gdx.graphics.getHeight()/2 - jeuFini.getHeight()/2));
				jeuFini.addAction(Actions.alpha(1, 0.5f));
			}
			else if(GameConstants.niveauSelectione == Donnees.getNiveau() && GameConstants.groupeSelectione == Donnees.getGroupe()){
				if(Donnees.getNiveau() < 25){
					Donnees.setNiveau(Donnees.getNiveau() + 1);
					tableFin.setVisible(true);
					tableFin.addAction(Actions.moveTo(Gdx.graphics.getWidth()/2, tableFin.getY(), 0.2f));
				}
				else if(Donnees.getNiveau() == 25 && GameConstants.groupeSelectione == Donnees.getGroupe() && Donnees.getGroupe() < GameConstants.NombreDeGroupes){
					Donnees.setNiveau(1);
					Donnees.setGroupe(Donnees.getGroupe() + 1);
					groupeFini.addAction(Actions.moveTo(Gdx.graphics.getWidth()/2 - groupeFini.getWidth()/2, Gdx.graphics.getHeight()/2 - groupeFini.getHeight()/2));
					groupeFini.addAction(Actions.alpha(1, 0.5f));
				}
			}
			else{
				tableFin.setVisible(true);
				tableFin.addAction(Actions.moveTo(Gdx.graphics.getWidth()/2, tableFin.getY(), 0.2f));
			}
		}
		
		// Niveau perdu
		if(GameConstants.viesPerdues == 3 && !GameConstants.niveauFini){
			sonPerdu.play();
			GameConstants.niveauFini = true;
			GameConstants.BOX_STEP = 0;
			imagePause.addAction(Actions.alpha(0.75f, 0.25f));
			tablePerdu.addAction(Actions.moveTo(Gdx.graphics.getWidth()/2, tablePerdu.getY(), 0.2f));

			if (!adBreakSent) {
				adBreakSent = true;
				game.actionResolver.onNaturalBreak();
			}
		}

		boolean shouldShowBanner = GameConstants.pause || GameConstants.niveauFini;
		if (shouldShowBanner != bannerVisible) {
			bannerVisible = shouldShowBanner;
			if (bannerVisible) {
				game.actionResolver.showBanner();
			} else {
				game.actionResolver.hideBanner();
			}
		}

		if (pauseBouton != null) {
			pauseBouton.setVisible(canTogglePause());
		}
		
        world.step(GameConstants.BOX_STEP, GameConstants.BOX_VELOCITY_ITERATIONS, GameConstants.BOX_POSITION_ITERATIONS);
        
        // Mettre le jeu en pause
        if (isPauseTogglePressed() && canTogglePause()){
			togglePause();
		}
        
        // Apparition des objets
        if(spawn){
        	//Jamais plus de 4 objets en même temps
        	if(objets.size < 4){													
        	Objets objet = new Objets(world, camera, spawnX, spawnY, barre, balles, lasers);
        	objets.add(objet);
        	}
        	spawn = false;
        }
        
        //Apparition du laser
        if(GameConstants.laserActif){
			if(TimeUtils.millis() - GameConstants.laserTime > 200){
				if(Donnees.getSon())
					sonLaser.play();
				if(GameConstants.tirs%2 == 0){
					Laser laser = new Laser(world, camera, barre.body.getPosition().x - 0.7f*barre.getWidth(), barre.body.getPosition().y + barre.getHeight());
					lasers.add(laser);
				}
				else{
					Laser laser = new Laser(world, camera, barre.body2.getPosition().x + 0.7f*barre.getWidth(), barre.body.getPosition().y + barre.getHeight());
					lasers.add(laser);
				}
				GameConstants.laserTime = TimeUtils.millis();
				GameConstants.tirs++;
			}
			else if(GameConstants.tirs == 14){
				GameConstants.laserActif = false;
				GameConstants.tirs = 0;
			}
		}
        //Vie perdue - Apparition d'une balle & réinitialisation des powerups
        if(balles.size == 0){
    		balle = new Balle(world, camera, barre.body.getPosition().x, (barre.body.getPosition().y + camera.viewportWidth/50 + camera.viewportHeight/100));
    		balles.add(balle);
    		GameConstants.vitesseBalle = GameConstants.vitesseBalleNormale;
    		GameConstants.ecart = GameConstants.barreNormale;
    		GameConstants.laserActif = false;
    		GameConstants.tirs = 0;  	
    		GameConstants.viesPerdues++;
        }
        
        // Destruction des briques, balles, lasers et objets
		Brique.detruire(briques, world);
        Laser.detruire(lasers, world, camera.viewportHeight);
        Balle.detruire(balles, world);
        Objets.detruire(objets, world);
               
        //Gestion de la vitesse de la balle
        if(GameConstants.vitesseBalle == GameConstants.vitesseBalleMin && (TimeUtils.millis() - GameConstants.vitesseBalleTime) > 5100)
        	GameConstants.vitesseBalle = GameConstants.vitesseBalleNormale;
        else if(GameConstants.vitesseBalle == GameConstants.vitesseBalleMax && (TimeUtils.millis() - GameConstants.vitesseBalleTime) > 5000)
        	GameConstants.vitesseBalle = GameConstants.vitesseBalleNormale;
        
          
		// debugRenderer.render(world, camera.combined);
		
		/**********************************Affichage des graphismes************************************/
		game.batch.begin();
		// Affichage des ombres
		for(int i = 0; i < briques.size; i++){
			briques.get(i).drawOmbre(game.batch, regionBarre);
		}
		for(int i = 0; i < lasers.size; i++){
			lasers.get(i).drawOmbre(game.batch, regionBarre);
		}
		for(Balle balle : balles){
			balle.drawOmbre(game.batch, regionBalle);
		}
		
		// Affichage des briques
		for(int i = 0; i < briques.size; i++){
			briques.get(i).draw(game.batch, regionBarre);
		}
		// Affichage de la barre
		barre.draw(game.batch, regionBarre);
		// Affichage des objets
		for(int i = 0; i < objets.size; i++){
			objets.get(i).draw(game.batch, textureAtlas);
		}
		// Affichage des Lasers
		for(int i = 0; i < lasers.size; i++){
			lasers.get(i).draw(game.batch, regionBarre);
		}	
		// Affichage des balles
		game.batch.setColor(1,1,1,1);
		for(Balle balle : balles){
			balle.draw(game.batch, regionBalle);
		}
		bouclier.draw(game.batch, regionBarre);
		game.batch.end();
		
        stage.act(delta);
        stage.draw();
		/**********************************************************************************************/
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportHeight = height * GameConstants.WORLD_TO_BOX;
		camera.viewportWidth = width * GameConstants.WORLD_TO_BOX;
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0f);
		camera.update();
		stage.getViewport().update(width, height, true);
		updatePauseButtonBounds();
		updateEndScreenLayout();
		if (webBuild) {
			UiActorUtils.centerTextButtons(stage.getRoot());
		}
	}

	@Override
	public void show() {	
		// Important pour pouvoir utiliser la touche BACK
		Gdx.input.setCatchKey(Keys.BACK, true);
		if (webBuild) {
			Gdx.input.setCatchKey(Keys.ESCAPE, true);
			Gdx.input.setCatchKey(Keys.SPACE, true);
			Gdx.input.setCatchKey(Keys.P, true);
		}
		// Permet d'interagir avec les Actors du Stage
		Gdx.input.setInputProcessor(stage);
		game.actionResolver.hideBanner();
		if (webBuild) {
			UiActorUtils.centerTextButtons(stage.getRoot());
		}
		if (listenersBound) {
			return;
		}
		listenersBound = true;
		
		world.setContactListener(new ContactListener(){
			@Override
			public void beginContact(Contact contact) {
				Body a = contact.getFixtureA().getBody();
			    Body b = contact.getFixtureB().getBody();
			    if(a.getUserData() != null && b.getUserData() != null){
				    if(a.getUserData().equals("Brique") && b.getUserData().equals("Balle")){
				    	couleurEdit = MathUtils.random(couleurMin,couleurMax);
				    	
				    	for(Balle balle : balles){
				    		if(balle.body == b)
				    			balle.rebond = 0;
				    	}
				    	
				    	for(Brique brique : briques){
				    		if(brique.body == a){
				    			brique.Collision();
				    			if(Donnees.getSon()){
									son = MathUtils.random(0,15);
									sons.getSons().get(son).play();
				    			}

						    	//Apparition des objets
						    	if(rollObjectDrop(OBJECT_DROP_CHANCE_ON_BALL_HIT) && brique.opacite == 0 && GameConstants.briquesDetruites > 2){
						    		GameConstants.briquesDetruites = 0;
						    		spawn = true;
						    		spawnX = a.getPosition().x;
						    		spawnY = a.getPosition().y;
						    	}
				    		}
				    	}  	
				    }
				    else if(a.getUserData().equals("Barre") && (b.getUserData().equals("Balle") || b.getUserData().equals("BalleLaser"))){
				    	if(b.getPosition().x > (barre.getPosition() - barre.getLargeur()) && b.getPosition().x < (barre.getPosition() + barre.getLargeur())){
				    		
				    		for(Balle balle : balles){
					    		if(balle.body == b)
					    			balle.rebond = 0;
					    	}
				    		
				    		b.setLinearVelocity(b.getLinearVelocity().rotate((30*(b.getPosition().x - barre.getPosition())/(barre.getLargeur()))));
				    	}
				    }
				    else if(a.getUserData().equals("Rebord") && (b.getUserData().equals("Balle") || b.getUserData().equals("BalleLaser"))){
				    	for(Balle balle : balles){
				    		if(balle.body == b){
				    			balle.rebond++;
						    	if(balle.rebond > 3 && b.getLinearVelocity().y >= 0 ){
						    		b.applyLinearImpulse(0, 0.1f, b.getPosition().x, b.getPosition().y, true);
						    	}
						    	else if(balle.rebond > 3){
						    		b.applyLinearImpulse(0, -0.1f, b.getPosition().x, b.getPosition().y, true);
						    	}	
				    		}
				    	}
				    }	
				    else if((a.getUserData().equals("Bouclier") && b.getUserData().equals("Balle")) || 
				    		(a.getUserData().equals("Balle") && b.getUserData().equals("Bouclier"))){
				    	GameConstants.bouclierActif = false;
				    }			    
			    }
			}

			@Override
			public void endContact(Contact contact) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				//Ignore les collisions entre la balle et les objets
				Body a = contact.getFixtureA().getBody();
			    Body b = contact.getFixtureB().getBody();
			    if(a.getUserData() != null && b.getUserData() != null){	
			    	if(a.getUserData().equals("Brique") && b.getUserData().equals("BalleLaser")){
				    	couleurEdit = MathUtils.random(couleurMin,couleurMax);			//Choix de la couleur pour le mode Epileptique
				        GameConstants.briquesDetruitesAuLaser++;
				    	contact.setEnabled(false);
				    	
				    	for(Balle balle : balles){
				    		if(balle.body == b){
				    			balle.rebond = 0;
				    		
					    		if(!balle.balleLaserContact){
				    				balle.balleLaserContact = true;
				    				balle.balleLaserTime = TimeUtils.millis();
				    			}
				    		}
				    	}
				    	
				    	for(Brique brique : briques){
				    		if(brique.body == a){
				    			brique.Destruction();
				    			if(Donnees.getSon()){
				    				son = MathUtils.random(0,15);
				    				sons.getSons().get(son).play();
				    			}

						    	//Apparition des objets
						    	if(rollObjectDrop(OBJECT_DROP_CHANCE_ON_LASER_BALL_HIT) && GameConstants.briquesDetruites > 2){
						    		GameConstants.briquesDetruites = 0;
						    		spawn = true;
						    		spawnX = a.getPosition().x;
						    		spawnY = a.getPosition().y;
						    	}
				    		}
				    	}
				    }
				    else if((a.getUserData().equals("Objet") && !b.getUserData().equals("Barre")) ||
				    		(b.getUserData().equals("Objet") && !a.getUserData().equals("Barre"))){
				    	contact.setEnabled(false);
				    }
				    else if((a.getUserData().equals("Laser") && !b.getUserData().equals("Brique")) ||
				    		(b.getUserData().equals("Laser") && !a.getUserData().equals("Brique"))){
				    	contact.setEnabled(false);
				    }
				    else if((a.getUserData().equals("Balle") || a.getUserData().equals("BalleLaser")) &&
				    		(b.getUserData().equals("Balle") || b.getUserData().equals("BalleLaser"))){
				    	contact.setEnabled(false);
				    }
			    }
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				//Permet d'activer la destruction de l'objet touché par la barre, dans le renderer, sans faire planter l'osti de programme
				Body a = contact.getFixtureA().getBody();
			    Body b = contact.getFixtureB().getBody();
			    if(a.getUserData() != null && b.getUserData() != null){		
			    	if(a.getUserData().equals("Objet") && b.getUserData().equals("Barre") || a.getUserData().equals("Barre") && b.getUserData().equals("Objet")){
			    		for(int i = 0; i < objets.size; i++){
				    		if(objets.get(i).body == a || objets.get(i).body == b){
								objets.get(i).body.setAwake(false);
				    		}	    			
			    		}
			    	}
			    	//Destruction du laser et des briques par le laser
			    	else if(a.getUserData().equals("Laser") && b.getUserData().equals("Brique") || a.getUserData().equals("Brique") && b.getUserData().equals("Laser")){
				    	couleurEdit = MathUtils.random(couleurMin,couleurMax);
				    	
			    		for(int i = 0; i < lasers.size; i++){
				    		if(lasers.get(i).body == a || lasers.get(i).body == b){
				    			lasers.get(i).visible = false;
				    		}	    			
			    		}
			    		for(Brique brique : briques){
				    		if(brique.body == a || brique.body == b){
				    			brique.Collision();
				    			if(Donnees.getSon()){
				    				son = MathUtils.random(0,15);
				    				sons.getSons().get(son).play();
				    			}

						    	//Apparition des objets
						    	if(rollObjectDrop(OBJECT_DROP_CHANCE_ON_LASER_HIT) && brique.opacite == 0 && GameConstants.briquesDetruites > 2){
						    		GameConstants.briquesDetruites = 0;
						    		spawn = true;
						    		spawnX = brique.body.getPosition().x;
						    		spawnY = brique.body.getPosition().y;
						    	}
				    		}
				    	} 
			    	}
			    }
				
			}
			
		});
		
		nextBouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				//Effacement du niveau actuel
				for(Brique brique : briques){
					world.destroyBody(brique.body);
				}
				if(briques.size > 0)
					briques.removeRange(0, briques.size-1);
				
				//Sortie de la pause
				GameConstants.BOX_STEP = 1/60f;
				
				//Transfert vers le niveau suivant s'il y en a encore un
				if(GameConstants.niveauSelectione < 25)
					GameConstants.niveauSelectione +=1;
				else if(GameConstants.niveauSelectione == 25 && GameConstants.groupeSelectione < GameConstants.NombreDeGroupes){
					GameConstants.niveauSelectione = 1;
					GameConstants.groupeSelectione += 1;
				}
				try{
	            	dispose();
					game.setScreen(new GameScreen(game));
				}
				catch(Exception e){
					dispose();
					game.setScreen(new MainMenuScreen(game));
				}
			}
		});
		
		replayBouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				dispose();
				game.setScreen(new MainMenuScreen(game));
			}
		});
		
		restartBouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				for(Brique brique : briques){
				world.destroyBody(brique.body);
				}
				briques.removeRange(0, briques.size-1);
				GameConstants.BOX_STEP = 1/60f;
				dispose();
				game.setScreen(new GameScreen(game));
			}
		});
		
		restartBouton2.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				for(Brique brique : briques){
				world.destroyBody(brique.body);
				}
				briques.removeRange(0, briques.size-1);
				GameConstants.BOX_STEP = 1/60f;
				dispose();
				game.setScreen(new GameScreen(game));
			}
		});
		
		menuBouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				dispose();
				game.setScreen(new MainMenuScreen(game));
			}
		});
		
		menuBouton2.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				dispose();
				game.setScreen(new MainMenuScreen(game));
			}
		});
		
		resumeBouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				GameConstants.pause = false;
		       	GameConstants.BOX_STEP = 1/60f;
		       	imagePause.addAction(Actions.alpha(0, 0.25f));
		       	tablePause.addAction(Actions.moveTo(2 * Gdx.graphics.getWidth(),
							tablePause.getY(), 
							0.25f));
			}
		});

		if (pauseBouton != null) {
			pauseBouton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (canTogglePause()) {
						togglePause();
					}
				}
			});
		}
		
		quitBouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				Gdx.app.exit();
			}
		});
		
		groupeFini.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				groupeFini.addAction(Actions.moveTo(-Gdx.graphics.getWidth(), 0));
				
				if(Donnees.getGroupe() == 4 && !Donnees.getMicrogravite()){
					Donnees.setMicrogravite(true);
					micrograviteDebloque.addAction(Actions.moveTo(Gdx.graphics.getWidth()/2 - micrograviteDebloque.getWidth()/2, Gdx.graphics.getHeight()/2 - micrograviteDebloque.getHeight()/2));
					micrograviteDebloque.addAction(Actions.alpha(1, 0.5f));
				}
				else{
					groupeDebloque.addAction(Actions.sequence(Actions.moveTo(Gdx.graphics.getWidth()/2 - groupeDebloque.getWidth()/2, Gdx.graphics.getHeight()/2 - groupeDebloque.getHeight()/2)));
					groupeDebloque.addAction(Actions.sequence(Actions.alpha(1, 0.35f)));
				}
				
			}
		});
		
		groupeDebloque.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				tableFin.setVisible(true);
				groupeDebloque.addAction(Actions.sequence(Actions.alpha(0, 0.25f), Actions.moveTo(-Gdx.graphics.getWidth(), 0), Actions.addAction(Actions.moveTo(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, 0.2f), tableFin)));
			}
		});
		
		jeuFini.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				if(Donnees.getEpileptique()){
					dispose();
					game.setScreen(new MainMenuScreen(game));
				}
				else{
					Donnees.setEpileptique(true);
					jeuFini.addAction(Actions.moveTo(-Gdx.graphics.getWidth(), 0));
					epileptiqueDebloque.addAction(Actions.moveTo(Gdx.graphics.getWidth()/2 - epileptiqueDebloque.getWidth()/2, Gdx.graphics.getHeight()/2 - epileptiqueDebloque.getHeight()/2));
					epileptiqueDebloque.addAction(Actions.alpha(1, 0.5f));
				}
			}
		});
		
		micrograviteDebloque.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				micrograviteDebloque.addAction(Actions.moveTo(-Gdx.graphics.getWidth(), 0));
				groupeDebloque.addAction(Actions.sequence(Actions.moveTo(Gdx.graphics.getWidth()/2 - groupeDebloque.getWidth()/2, Gdx.graphics.getHeight()/2 - groupeDebloque.getHeight()/2)));
				groupeDebloque.addAction(Actions.sequence(Actions.alpha(1, 0.35f)));
			}
		});
		
		epileptiqueDebloque.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				dispose();
				game.setScreen(new MainMenuScreen(game));
			}
		});
	}
	
	public void spawnBalle(float x, float y, Vector2 speed){
		Balle balle = new Balle(world, camera, x, y);
		balle.balleActive = true;
		balle.startImpulse = true;
		balles.add(balle);
		balle.body.applyLinearImpulse(speed.x, speed.y, balle.body.getPosition().x, balle.body.getPosition().y, true);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void hide() {
		game.actionResolver.hideBanner();
	}

	@Override
	public void pause() {
		// no-op
	}

	@Override
	public void resume() {
		// no-op
	}

	@Override
	public void dispose() {
		skin.dispose();
		stage.dispose();
		debugRenderer.dispose();
		world.dispose();
		if(pauseButtonUpTexture != null)
			pauseButtonUpTexture.dispose();
		if(pauseButtonDownTexture != null)
			pauseButtonDownTexture.dispose();
	}

	private Texture createPauseButtonTexture(float fillAlpha, float borderAlpha){
		Pixmap pixmap = new Pixmap(96, 96, Pixmap.Format.RGBA8888);
		pixmap.setColor(1, 1, 1, 0);
		pixmap.fill();
		if(fillAlpha > 0f){
			pixmap.setColor(0, 0, 0, fillAlpha);
			pixmap.fillRectangle(4, 4, 88, 88);
		}
		pixmap.setColor(1, 1, 1, borderAlpha);
		for(int i = 0; i < 4; i++){
			pixmap.drawRectangle(i, i, 96 - 2 * i, 96 - 2 * i);
		}
		Texture texture = new Texture(pixmap);
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		pixmap.dispose();
		return texture;
	}

	private boolean canTogglePause() {
		return GameConstants.viesPerdues < 3 && briques.size != 0 && !GameConstants.niveauFini;
	}

	private void togglePause() {
		if (GameConstants.pause) {
			GameConstants.pause = false;
			GameConstants.BOX_STEP = 1 / 60f;
			imagePause.addAction(Actions.alpha(0, 0.25f));
			tablePause.addAction(Actions.moveTo(2 * Gdx.graphics.getWidth(), tablePause.getY(), 0.25f));
		} else {
			GameConstants.pause = true;
			GameConstants.BOX_STEP = 0;
			imagePause.addAction(Actions.alpha(0.75f, 0.25f));
			tablePause.addAction(Actions.moveTo(Gdx.graphics.getWidth()/2 - tablePause.getWidth()/2, tablePause.getY(), 0.25f));
		}
	}

	private boolean isPauseTogglePressed() {
		if (webBuild) {
			return Gdx.input.isKeyJustPressed(Keys.ESCAPE)
					|| Gdx.input.isKeyJustPressed(Keys.SPACE)
					|| Gdx.input.isKeyJustPressed(Keys.P);
		}
		return Gdx.input.isKeyJustPressed(Keys.BACK);
	}

	private boolean rollObjectDrop(int dropChancePercent) {
		spawnObjet = MathUtils.random(1, 100);
		return spawnObjet <= dropChancePercent;
	}

	private void updateEndScreenLayout() {
		if (tableFin == null || tablePerdu == null || labelComplete == null || labelPerdu == null) {
			return;
		}
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		float widthScale = width / baseScreenWidth;
		float heightScale = height / baseScreenHeight;
		float responsiveScale = Math.min(widthScale, heightScale);
		float baseLabelScale = webBuild ? WEB_END_SCREEN_LABEL_BASE_SCALE : MOBILE_END_SCREEN_LABEL_BASE_SCALE;
		float endScreenLabelScale = baseLabelScale * responsiveScale;
		labelComplete.setFontScale(endScreenLabelScale);
		labelPerdu.setFontScale(endScreenLabelScale);
		labelComplete.setAlignment(Align.center);
		labelPerdu.setAlignment(Align.center);

		float endScreenButtonWidth = webBuild ? width / 3.6f : width / 3f;
		float endScreenButtonGap = webBuild ? width / 400f : 0f;
		float endScreenButtonHeight = height / 12f;
		float endScreenRowWidth = 2f * endScreenButtonWidth + endScreenButtonGap;
		float endScreenLabelSpacing = height / 30f;

		if (tableFinLabelCell != null) {
			tableFinLabelCell.width(endScreenRowWidth).spaceBottom(endScreenLabelSpacing);
		}
		if (tableFinNextCell != null) {
			tableFinNextCell.width(endScreenButtonWidth).height(endScreenButtonHeight).padRight(endScreenButtonGap);
		}
		if (tableFinMenuCell != null) {
			tableFinMenuCell.width(endScreenButtonWidth).height(endScreenButtonHeight);
		}
		if (tablePerduLabelCell != null) {
			tablePerduLabelCell.width(endScreenRowWidth).spaceBottom(endScreenLabelSpacing);
		}
		if (tablePerduReplayCell != null) {
			tablePerduReplayCell.width(endScreenButtonWidth).height(endScreenButtonHeight).padRight(endScreenButtonGap);
		}
		if (tablePerduMenuCell != null) {
			tablePerduMenuCell.width(endScreenButtonWidth).height(endScreenButtonHeight);
		}

		tableFin.invalidateHierarchy();
		tablePerdu.invalidateHierarchy();
		tableFin.setY(height / 2f + tableFin.getPrefHeight() / 4f);
		tablePerdu.setY(height / 2f + tablePerdu.getPrefHeight() / 4f);
		tableFin.setX(tableFin.getX() > 0f ? width / 2f : -width / 2f);
		tablePerdu.setX(tablePerdu.getX() > 0f ? width / 2f : -width / 2f);
	}

	private void updatePauseButtonBounds() {
		if (pauseBouton == null) {
			return;
		}
		float margin = Gdx.graphics.getWidth() / 40f;
		pauseBouton.setX(Gdx.graphics.getWidth() - pauseBouton.getWidth() - margin);
		pauseBouton.setY(Gdx.graphics.getHeight() - pauseBouton.getHeight() - margin);
	}
}
