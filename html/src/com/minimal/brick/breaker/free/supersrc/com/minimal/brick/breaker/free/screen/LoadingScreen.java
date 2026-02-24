package com.minimal.brick.breaker.free.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.minimal.brick.breaker.free.MyGdxGame;

public class LoadingScreen implements Screen {
    final MyGdxGame game;
    OrthographicCamera camera;
    private Texture textureLogo;
    private Image imageLogo;
    private Stage stage;

    private static class WebFontLoader extends SynchronousAssetLoader<BitmapFont, AssetLoaderParameters<BitmapFont>> {
        private static final float BUTTON_TEXT_SCALE = 0.312f;
        private static final float TITLE_TEXT_SCALE = 0.336f;
        private static final float SMALL_TEXT_SCALE = 0.20f;
        private static final float MEDIUM_TEXT_SCALE = 0.24f;

        public WebFontLoader(FileHandleResolver resolver) {
            super(resolver);
        }

        @Override
        public BitmapFont load(AssetManager assetManager, String fileName, com.badlogic.gdx.files.FileHandle file, AssetLoaderParameters<BitmapFont> parameter) {
            String fontFile;
            float scale = 1.0f;
            if ("fontOption.ttf".equals(fileName) || "fontBoutonNotation.ttf".equals(fileName)) {
                fontFile = "Fonts/web_ui.fnt";
                scale = BUTTON_TEXT_SCALE;
            } else if ("fontTitre.ttf".equals(fileName)) {
                fontFile = "Fonts/web_title.fnt";
                scale = TITLE_TEXT_SCALE;
            } else if ("font1.ttf".equals(fileName)) {
                fontFile = "Fonts/web_ui.fnt";
                scale = BUTTON_TEXT_SCALE;
            } else if ("fontTextTableJeu.ttf".equals(fileName)) {
                fontFile = "Fonts/web_ui.fnt";
                scale = MEDIUM_TEXT_SCALE;
            } else if ("fontTextNotation.ttf".equals(fileName)) {
                fontFile = "Fonts/web_ui.fnt";
                scale = SMALL_TEXT_SCALE;
            } else {
                fontFile = "Fonts/web_ui.fnt";
                scale = BUTTON_TEXT_SCALE;
            }
            BitmapFont font = new BitmapFont(Gdx.files.internal(fontFile), false);
            font.getData().setScale(scale);
            font.setUseIntegerPositions(false);
            for (TextureRegion region : font.getRegions()) {
                region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
            }
            return font;
        }

        @Override
        public Array<AssetDescriptor> getDependencies(String fileName, com.badlogic.gdx.files.FileHandle file,
                                                       AssetLoaderParameters<BitmapFont> parameter) {
            return null;
        }
    }

    public LoadingScreen(final MyGdxGame gam) {
        game = gam;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        textureLogo = new Texture(Gdx.files.internal("Images/Logo.png"), true);
        textureLogo.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        imageLogo = new Image(textureLogo);
        imageLogo.setWidth(9 * Gdx.graphics.getWidth() / 10f);
        imageLogo.setHeight(imageLogo.getWidth() * textureLogo.getHeight() / textureLogo.getWidth());
        imageLogo.setX(Gdx.graphics.getWidth() / 2f - imageLogo.getWidth() / 2f);
        imageLogo.setY(Gdx.graphics.getHeight() / 2f - imageLogo.getHeight() / 2f);
        stage = new Stage();

        // Sound assets
        game.assets.load("Sons/Collision 1.ogg", Sound.class);
        game.assets.load("Sons/Collision 2.ogg", Sound.class);
        game.assets.load("Sons/Collision 3.ogg", Sound.class);
        game.assets.load("Sons/Collision 4.ogg", Sound.class);
        game.assets.load("Sons/Collision 5.ogg", Sound.class);
        game.assets.load("Sons/Collision 6.ogg", Sound.class);
        game.assets.load("Sons/Collision 7.ogg", Sound.class);
        game.assets.load("Sons/Collision 8.ogg", Sound.class);
        game.assets.load("Sons/Collision 9.ogg", Sound.class);
        game.assets.load("Sons/Collision 10.ogg", Sound.class);
        game.assets.load("Sons/Collision 11.ogg", Sound.class);
        game.assets.load("Sons/Collision 12.ogg", Sound.class);
        game.assets.load("Sons/Collision 13.ogg", Sound.class);
        game.assets.load("Sons/Collision 14.ogg", Sound.class);
        game.assets.load("Sons/Collision 15.ogg", Sound.class);
        game.assets.load("Sons/Collision 16.ogg", Sound.class);
        game.assets.load("Sons/Laser.ogg", Sound.class);
        game.assets.load("Sons/NiveauFini.ogg", Sound.class);
        game.assets.load("Sons/Perdu.ogg", Sound.class);

        // Texture atlas
        game.assets.load("Images.pack", TextureAtlas.class);

        // Web fallback fonts mapped to the same asset keys used in core UI code.
        FileHandleResolver resolver = new InternalFileHandleResolver();
        game.assets.setLoader(BitmapFont.class, ".ttf", new WebFontLoader(resolver));
        game.assets.load("font1.ttf", BitmapFont.class);
        game.assets.load("fontOption.ttf", BitmapFont.class);
        game.assets.load("fontTitre.ttf", BitmapFont.class);
        game.assets.load("fontBoutonNotation.ttf", BitmapFont.class);
        game.assets.load("fontTextNotation.ttf", BitmapFont.class);
        game.assets.load("fontTextTableJeu.ttf", BitmapFont.class);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        stage.act();
        stage.draw();

        if (game.assets.update()) {
            dispose();
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        game.actionResolver.hideBanner();
        stage.addActor(imageLogo);
        imageLogo.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.75f), Actions.delay(1.5f)));
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        textureLogo.dispose();
        stage.dispose();
    }
}
