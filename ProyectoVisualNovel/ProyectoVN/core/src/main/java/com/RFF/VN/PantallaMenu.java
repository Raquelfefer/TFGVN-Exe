package com.RFF.VN;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PantallaMenu implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private Repository repository;
    private Image fondo;

    public PantallaMenu(Main game) {
        this.game = game;
        this.repository = new Repository();
        this.stage = new Stage(new ScreenViewport());
        
        this.skin = game.skin; 
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        
        game.controlarMusicaMenu("musica_menus.mp3", true);

        fondo = new Image(new Texture(Gdx.files.internal("fondos/fondo_menu.png")));
        fondo.setFillParent(true);
        stage.addActor(fondo);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        final int ultimoId = repository.obtenerUltimoCapitulo(game.idUsuarioLogueado);

        Label saludo = new Label("¡Hola, " + game.nombreUsuarioLogueado + "!", skin, "titulo");
        saludo.setFontScale(0.9f);

        TextButton btnNuevaPartida = new TextButton("Nueva Partida", skin);
        TextButton btnContinuarPartida = new TextButton("Continuar Partida", skin);
        TextButton btnLogros = new TextButton("Ver Logros", skin);
        TextButton btnCerrarSesion = new TextButton("Cerrar Sesion", skin);
        TextButton btnSalir = new TextButton("Salir", skin);

        if (ultimoId <= 0) {
            btnContinuarPartida.setDisabled(true);
            btnContinuarPartida.getColor().a = 0.5f; 
        }

        table.center();
        table.add(saludo).padBottom(50).row();
        
        table.add(btnNuevaPartida).width(300).pad(10).row();
        table.add(btnContinuarPartida).width(300).pad(10).row();
        table.add(btnLogros).width(300).pad(10).row();
        
        table.add(btnCerrarSesion).width(300).pad(10).padTop(30).row();
        table.add(btnSalir).width(300).pad(10).row();

        btnNuevaPartida.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.controlarMusicaMenu(null, false);
                repository.actualizarProgreso(game.idUsuarioLogueado, 1);
                repository.borrarHistorialUsuario(game.idUsuarioLogueado);
                game.setScreen(new PantallaJuego(game, 1, true));
            }
        });

        btnContinuarPartida.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!btnContinuarPartida.isDisabled()) {
                    game.controlarMusicaMenu(null, false);
                    game.setScreen(new PantallaJuego(game, ultimoId, true));
                }
            }
        });

        btnLogros.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new PantallaLogros(game));
            }
        });

        btnCerrarSesion.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.idUsuarioLogueado = 0;
                game.nombreUsuarioLogueado = "";
                game.setScreen(new PantallaLogin(game));
            }
        });

        btnSalir.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Gdx.app.getType() == com.badlogic.gdx.Application.ApplicationType.WebGL) {
                    game.setScreen(new PantallaLogin(game));
                } else {
                    Gdx.app.exit();
                }
            }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
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

    @Override
    public void dispose() {
        stage.dispose();
    }
}