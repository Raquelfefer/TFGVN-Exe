package com.RFF.VN;

import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PantallaLogros implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private Repository repository;
    private Image fondo;
    private Texture texFondoGrande; 
    private Texture texFondoFila;
    
    public PantallaLogros(Main game) {
        this.game = game;
        this.repository = new Repository();
        this.stage = new Stage(new ScreenViewport());
        
        this.skin = game.skin; 
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        game.controlarMusicaMenu("musica_menus.mp3", true);

        fondo = new Image(new Texture(Gdx.files.internal("fondos/fondo_menu_logros.png")));
        fondo.setFillParent(true);
        stage.addActor(fondo);
        
        Color colorFondoScroll = new Color(0.2f, 0.15f, 0.1f, 0.7f); 
        Color colorFilaLogro = new Color(0.3f, 0.2f, 0.15f, 0.8f);
        
        TextureRegionDrawable drawableGrande = crearFondo(colorFondoScroll, true);
        TextureRegionDrawable drawableFila = crearFondo(colorFilaLogro, false);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        Label titulo = new Label("TUS LOGROS", skin, "titulo");
        mainTable.add(titulo).padBottom(30).row();

        Table listaLogros = new Table();
        listaLogros.top();

        List<LogroDetalleDTO> logros = repository.obtenerListaLogros(game.idUsuarioLogueado);

        for (LogroDetalleDTO l : logros) {
            Table fila = new Table();
            fila.setBackground(drawableFila);
            fila.pad(15);

            Label nameLabel = new Label(l.nombre, skin);
            Label descLabel = new Label(l.descripcion, skin);
            descLabel.setWrap(true); 
            descLabel.setFontScale(0.85f); 

            if (l.conseguido) {
                nameLabel.setColor(Color.GOLD);
                String fechaLimpia = l.fechaConseguido != null ? l.fechaConseguido.substring(0, 10) : "";
                Label fechaLabel = new Label("Desbloqueado: " + fechaLimpia, skin);
                fechaLabel.setFontScale(0.75f);
                fechaLabel.setColor(skin.getColor("aguamarina"));

                fila.add(nameLabel).width(550).left().row();
                fila.add(descLabel).width(550).left().padBottom(5).row();
                fila.add(fechaLabel).left();
            } else {
                nameLabel.setColor(Color.valueOf("888888")); 
                descLabel.setColor(Color.valueOf("666666"));
                fila.add(nameLabel).width(550).left().row();
                fila.add(descLabel).width(550).left();
            }

            listaLogros.add(fila).width(580).pad(10).row();
        }

        ScrollPane scroll = new ScrollPane(listaLogros, skin);
        scroll.getStyle().background = drawableGrande;
        scroll.setFadeScrollBars(false); 
        
        mainTable.add(scroll).width(650).height(450).row();

        TextButton btnVolver = new TextButton("Volver al Menu", skin);
        btnVolver.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new PantallaMenu(game));
            }
        });
        mainTable.add(btnVolver).padTop(20).width(250);
    }
    
    private TextureRegionDrawable crearFondo(Color color, boolean esGrande) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        
        if (esGrande) texFondoGrande = tex;
        else texFondoFila = tex;
        
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(tex));
        pixmap.dispose();
        return drawable;
    }
    
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0,0,0,1);
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        game.getViewport().update(width, height, true);
    }
    
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    
    @Override public void dispose() {
        stage.dispose();
        if (texFondoGrande != null) texFondoGrande.dispose();
        if (texFondoFila != null) texFondoFila.dispose();
    }
}