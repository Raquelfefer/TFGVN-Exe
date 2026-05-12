package com.RFF.VN;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class Main extends Game {
    private SpriteBatch batch;
    private ExtendViewport viewport;
    private OrthographicCamera camera;

  
    public Skin skin;
    private BitmapFont fuenteCozy;  
    private BitmapFont fuenteTitulo; 
    
    public int idUsuarioLogueado;
    public String nombreUsuarioLogueado;
    
    public Music musicaMenu;
    private String nombreMusicaMenu = "";
    
    private Music musicaSaliente;
    private Music musicaEntrante;
    private float fadeTimer = 0;
    private final float FADE_DURATION = 2.0f; 
    private boolean isFading = false;
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(1280, 720, camera);

        generarFuentes();

        com.badlogic.gdx.graphics.g2d.TextureAtlas atlas = new com.badlogic.gdx.graphics.g2d.TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        skin = new Skin(atlas); 

        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        pixmap.fill();
        skin.add("white", new com.badlogic.gdx.graphics.Texture(pixmap)); 
        pixmap.dispose();

        skin.add("default-font", fuenteCozy, BitmapFont.class);
        skin.add("fuente-titulo", fuenteTitulo, BitmapFont.class);

        skin.load(Gdx.files.internal("uiskin.json"));

        skin.get(Label.LabelStyle.class).font = fuenteCozy;
        skin.get(TextButton.TextButtonStyle.class).font = fuenteCozy;
        skin.get(TextField.TextFieldStyle.class).font = fuenteCozy;

        ConexionBD.conectar();
        this.setScreen(new PantallaLogin(this));
    }
    
    private void generarFuentes() {
        FreeTypeFontGenerator genCozy = new FreeTypeFontGenerator(Gdx.files.internal("ComingSoon-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter paramCozy = new FreeTypeFontGenerator.FreeTypeFontParameter();

        paramCozy.size = 28; 
        paramCozy.color = Color.valueOf("FDF9E5");
        paramCozy.borderWidth = 2.5f; 
        paramCozy.borderColor = Color.valueOf("422E26"); 
        paramCozy.shadowOffsetX = 2;
        paramCozy.shadowOffsetY = 2;
        paramCozy.shadowColor = new Color(0, 0, 0, 0.4f);
        paramCozy.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "áéíóúÁÉÍÓÚñÑ¿?¡!-—";
        
        fuenteCozy = genCozy.generateFont(paramCozy);
        genCozy.dispose();

        FreeTypeFontGenerator genZen = new FreeTypeFontGenerator(Gdx.files.internal("ZenLoop-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter paramZen = new FreeTypeFontGenerator.FreeTypeFontParameter();

        paramZen.size = 65;
        paramZen.color = Color.valueOf("FFD700"); 

        paramZen.borderWidth = 4.5f; 
        paramZen.borderColor = Color.valueOf("422E26");
        paramZen.borderStraight = false; 

        paramZen.shadowOffsetX = 3;
        paramZen.shadowOffsetY = 3;
        paramZen.shadowColor = new Color(0, 0, 0, 0.5f);

        paramZen.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "áéíóúÁÉÍÓÚñÑ¿?¡!-—";

        fuenteTitulo = genZen.generateFont(paramZen);
        genZen.dispose();
    }
    
    public void controlarMusicaMenu(String nombreArchivo, boolean reproducir) {
        if (!reproducir) {
            if (musicaMenu != null) {
                musicaSaliente = musicaMenu;
                isFading = true;
                fadeTimer = 0;
                nombreMusicaMenu = "";
            }
            return;
        }

        if (nombreMusicaMenu.equals(nombreArchivo)) return;

        musicaSaliente = musicaMenu;
        
        musicaMenu = Gdx.audio.newMusic(Gdx.files.internal("musica/" + nombreArchivo));
        musicaMenu.setLooping(true);
        musicaMenu.setVolume(0);
        musicaMenu.play();
        
        musicaEntrante = musicaMenu;
        nombreMusicaMenu = nombreArchivo;
        
        isFading = true;
        fadeTimer = 0;
    }
    
    @Override
    public void render() {
        super.render(); 

        if (isFading) {
            fadeTimer += Gdx.graphics.getDeltaTime();
            float progreso = Math.min(fadeTimer / FADE_DURATION, 1f);

            if (musicaSaliente != null) {
                musicaSaliente.setVolume(0.3f * (1f - progreso));
                if (progreso >= 1f) {
                    musicaSaliente.stop();
                    musicaSaliente.dispose();
                    musicaSaliente = null;
                }
            }

            if (musicaEntrante != null) {
                musicaEntrante.setVolume(0.3f * progreso);
            }

            if (progreso >= 1f) {
                isFading = false;
                musicaEntrante = null;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        if (fuenteCozy != null) fuenteCozy.dispose();
        if (fuenteTitulo != null) fuenteTitulo.dispose();
        if (skin != null) skin.dispose();
        if (musicaMenu != null) musicaMenu.dispose();
    }
    
    public SpriteBatch getBatch() { return batch; }
    public BitmapFont getFuente() { return fuenteCozy; } 
    public BitmapFont getFuenteTitulo() { return fuenteTitulo; }
    public ExtendViewport getViewport() { return viewport; }
    public OrthographicCamera getCamera() { return camera; }
}