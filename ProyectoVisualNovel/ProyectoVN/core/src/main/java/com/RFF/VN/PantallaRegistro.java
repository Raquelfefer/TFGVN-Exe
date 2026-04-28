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

public class PantallaRegistro implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private Repository repository;
    private Image fondo;

    public PantallaRegistro(Main game) {
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

        Label titulo = new Label("Crear Nuevo Usuario", skin, "titulo");

        final TextField campoUsuario = new TextField("", skin);
        final TextField campoPassword = new TextField("", skin);
        campoPassword.setPasswordMode(true);
        campoPassword.setPasswordCharacter('*');

        final SelectBox<String> comboPreguntas = new SelectBox<>(skin);
        comboPreguntas.setItems(
            "¿Nombre de tu primera mascota?",
            "¿Ciudad donde nacieron tus padres?",
            "¿Nombre de tu escuela primaria?",
            "¿Marca de tu primer coche?"
        );

        final TextField campoRespuesta = new TextField("", skin);
        
        final Label mensajeEstado = new Label("", skin);
        mensajeEstado.setColor(Color.SCARLET); 

        TextButton btnRegistrar = new TextButton("Registrar", skin);
        TextButton btnVolver = new TextButton("Volver", skin);

        table.center();
        table.add(titulo).colspan(2).padBottom(30).row();
        
        table.add(new Label("Usuario:", skin)).left().padRight(10);
        table.add(campoUsuario).width(350).pad(5).row();
        
        table.add(new Label("Contraseña:", skin)).left().padRight(10);
        table.add(campoPassword).width(350).pad(5).row();
        
        table.add(new Label("Pregunta Seg:", skin)).left().padRight(10);
        table.add(comboPreguntas).width(350).pad(5).row();
        
        table.add(new Label("Respuesta:", skin)).left().padRight(10);
        table.add(campoRespuesta).width(350).pad(5).row();

        table.add(mensajeEstado).colspan(2).pad(15).row();

        Table botonesTable = new Table();
        botonesTable.add(btnRegistrar).width(160).padRight(20);
        botonesTable.add(btnVolver).width(160);
        
        table.add(botonesTable).colspan(2).padTop(10);

        btnVolver.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new PantallaLogin(game));
            }
        });

        btnRegistrar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String nom = campoUsuario.getText().trim();
                String pass = campoPassword.getText().trim();
                String pre = comboPreguntas.getSelected();
                String res = campoRespuesta.getText().trim();

                if(nom.isEmpty() || pass.isEmpty() || res.isEmpty()) {
                    mensajeEstado.setText("Rellena todos los campos.");
                    return;
                }

                int resBD = repository.registrarUsuario(nom, pass, pre, res);
                if(resBD > 0) {
                    game.idUsuarioLogueado = resBD;
                    game.nombreUsuarioLogueado = nom;
                    game.setScreen(new PantallaMenu(game));
                } else if(resBD == -1) {
                    mensajeEstado.setText("El usuario ya existe.");
                } else {
                    mensajeEstado.setText("Error en la base de datos.");
                }
            }
        });
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
    
    @Override 
    public void dispose() { 
        stage.dispose(); 
    }
}