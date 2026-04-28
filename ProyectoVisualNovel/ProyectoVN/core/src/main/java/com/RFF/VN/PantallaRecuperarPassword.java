package com.RFF.VN;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PantallaRecuperarPassword implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private Repository repository;
    private Image fondo;

    public PantallaRecuperarPassword(Main game) {
        this.game = game;
        this.repository = new Repository();
        this.stage = new Stage(new ScreenViewport());
        
        this.skin = game.skin; 
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        
        fondo = new Image(new Texture(Gdx.files.internal("fondos/fondo_login.png")));
        fondo.setFillParent(true);
        stage.addActor(fondo);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label titulo = new Label("Recuperar Contraseña", skin, "titulo");

        final TextField campoUsuario = new TextField("", skin);
        
        final Label etiquetaPregunta = new Label("Introduce tu usuario para ver la pregunta", skin);
        etiquetaPregunta.setWrap(true);
        etiquetaPregunta.setAlignment(com.badlogic.gdx.utils.Align.center);
        etiquetaPregunta.setColor(Color.valueOf("E8E4D8")); 

        final TextField campoRespuesta = new TextField("", skin);
        final TextField campoNuevaPass = new TextField("", skin);
        campoNuevaPass.setPasswordMode(true);
        campoNuevaPass.setPasswordCharacter('*');

        final Label mensajeEstado = new Label("", skin);
        
        TextButton btnBuscar = new TextButton("Buscar Usuario", skin);
        TextButton btnRestablecer = new TextButton("Cambiar Contraseña", skin);
        TextButton btnVolver = new TextButton("Cancelar", skin);

        campoRespuesta.setVisible(false);
        campoNuevaPass.setVisible(false);
        btnRestablecer.setVisible(false);

        table.center();
        table.add(titulo).colspan(2).padBottom(30).row();
        
        table.add(new Label("Usuario:", skin)).left().padRight(10);
        table.add(campoUsuario).width(350).pad(5).row();
        
        table.add(btnBuscar).colspan(2).width(250).pad(15).row();
        
        table.add(etiquetaPregunta).colspan(2).width(500).pad(15).row();
        
        Label lblRes = new Label("Tu Respuesta:", skin);
        table.add(lblRes).left().padRight(10);
        table.add(campoRespuesta).width(350).pad(5).row();
        
        Label lblNew = new Label("Nueva Pass:", skin);
        table.add(lblNew).left().padRight(10);
        table.add(campoNuevaPass).width(350).pad(5).row();

        table.add(mensajeEstado).colspan(2).pad(15).row();

        Table botonesTable = new Table();
        botonesTable.add(btnRestablecer).width(350).padRight(15);
        botonesTable.add(btnVolver).width(160);
        table.add(botonesTable).colspan(2).padTop(10);

        btnBuscar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String user = campoUsuario.getText().trim();
                String pregunta = repository.obtenerPreguntaUsuario(user);
                
                if (pregunta != null) {
                    etiquetaPregunta.setText("PREGUNTA:\n" + pregunta);
                    etiquetaPregunta.setColor(skin.getColor("aguamarina")); 
                    campoRespuesta.setVisible(true);
                    campoNuevaPass.setVisible(true);
                    btnRestablecer.setVisible(true);
                    mensajeEstado.setText("");
                } else {
                    mensajeEstado.setText("Usuario no encontrado.");
                    mensajeEstado.setColor(Color.SCARLET);
                }
            }
        });

        btnRestablecer.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String user = campoUsuario.getText().trim();
                String res = campoRespuesta.getText().trim();
                String pass = campoNuevaPass.getText().trim();

                if (res.isEmpty() || pass.isEmpty()) {
                    mensajeEstado.setText("Rellena la respuesta y la nueva clave.");
                    mensajeEstado.setColor(Color.SCARLET);
                    return;
                }

                if (repository.actualizarPassword(user, res, pass)) {
                    mensajeEstado.setText("¡Contraseña actualizada con éxito!");
                    mensajeEstado.setColor(skin.getColor("verde_menta"));
                } else {
                    mensajeEstado.setText("Respuesta incorrecta.");
                    mensajeEstado.setColor(Color.SCARLET);
                }
            }
        });

        btnVolver.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new PantallaLogin(game));
            }
        });
    }

    @Override public void render(float delta) { 
        ScreenUtils.clear(0,0,0,1); 
        stage.act(delta); 
        stage.draw(); 
    }
    
    @Override public void resize(int width, int height) { 
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