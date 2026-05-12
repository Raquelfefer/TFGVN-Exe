package com.RFF.VN;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PantallaLogin implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private Repository repository;
    private Image fondo;

    public PantallaLogin(Main game) {
        this.game = game;
        this.repository = new Repository();
        this.stage = new Stage(new ScreenViewport());
        
        this.skin = game.skin; 
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        game.controlarMusicaMenu("musica_menus.mp3", true);

        fondo = new Image(new Texture(Gdx.files.internal("fondos/fondo_login.png")));
        fondo.setFillParent(true);
        stage.addActor(fondo);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label titulo = new Label("El viaje de Laurie", skin, "titulo");
        
        Label labelUsuario = new Label("Nombre de Usuario:", skin);
        final TextField campoUsuario = new TextField("", skin);
        Label labelPassword = new Label("Contraseña:", skin);
        final TextField campoPassword = new TextField("", skin);
        campoPassword.setPasswordMode(true);
        campoPassword.setPasswordCharacter('*');

        final Label mensajeEstado = new Label("", skin);
        mensajeEstado.setColor(Color.SCARLET); 

        TextButton botonLogin = new TextButton("Entrar", skin);
        TextButton botonRegistro = new TextButton("Crear usuario", skin);
        TextButton botonRecuperar = new TextButton("¿Olvidaste tu contraseña?", skin);
        TextButton botonSalir = new TextButton("Salir", skin);

        table.center();
        table.add(titulo).colspan(2).padBottom(40).center().row(); 
        table.add(mensajeEstado).colspan(2).padBottom(10).center().row();
        
        table.add(labelUsuario).colspan(2).padBottom(5).center().row();
        table.add(campoUsuario).width(350).colspan(2).padBottom(15).center().row();

        table.add(labelPassword).colspan(2).padBottom(5).center().row();
        table.add(campoPassword).width(350).colspan(2).padBottom(25).center().row();

        table.add(botonLogin).width(350).colspan(2).padBottom(15).center().row();

        Table subTable = new Table();
        subTable.add(botonRegistro).width(200).padRight(20);
        subTable.add(botonRecuperar).width(380);
        table.add(subTable).colspan(2).padBottom(20).center().row();

        if (Gdx.app.getType() != com.badlogic.gdx.Application.ApplicationType.WebGL) {
            table.add(botonSalir).width(200).colspan(2).padTop(10);
            botonSalir.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.exit();
                }
            });
        }

        botonLogin.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String nombre = campoUsuario.getText().trim();
                String password = campoPassword.getText().trim();

                if (nombre.isEmpty() || password.isEmpty()) {
                    mensajeEstado.setText("Escribe tu usuario y contraseña.");
                    return;
                }

                int id = repository.validarLogin(nombre, password);
                if (id > 0) {
                    game.idUsuarioLogueado = id;
                    game.nombreUsuarioLogueado = campoUsuario.getText();
                    game.setScreen(new PantallaMenu(game));
                } else if (id == -1) {
                    mensajeEstado.setText("Contraseña incorrecta.");
                } else {
                    mensajeEstado.setText("El usuario no existe.");
                }
            }
        });

        botonRegistro.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new PantallaRegistro(game));
            }
        });

        botonRecuperar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new PantallaRecuperarPassword(game));
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