package com.RFF.VN;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PantallaJuego implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private Label etiquetaTexto;
    private Repository repository;
    private Container<Table> contenedorPrincipal;

    private int idNarracionActual;
    private Integer idSiguienteNarracion;
    private boolean mostrandoOpciones = false;

    private Image imgFondo;
    private Image imgIzq, imgDer;
    private Image imgFlecha;
    private Music musicaActual;
    private String nombreMusicaActual = "";
    private Sound sonidoLogro; 

    private float anchoCaja;
    private float altoCaja;
    private float margenAbajo = 20f;
    
    private String textoCompleto = "";    
    private float tiempoAcumulado = 0f;
    private float velocidadEscritura = 0.03f; 
    private int indiceActual = 0;
    private boolean escribiendo = false;

    public PantallaJuego(Main game, int idNarracion) {
        this.game = game;
        this.repository = new Repository();
        this.stage = new Stage(new ScreenViewport());
        this.skin = game.skin;
        this.idNarracionActual = idNarracion;
        
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        
        sonidoLogro = Gdx.audio.newSound(Gdx.files.internal("sonidos/logro.mp3"));

        imgFondo = new Image();
        imgFondo.setFillParent(true);
        stage.addActor(imgFondo);

        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        
        this.altoCaja = sh * 0.25f; 
        this.anchoCaja = sw * 0.90f; 
        float puntoApoyo = altoCaja + margenAbajo + 10f;

        Table tablaPersonajes = new Table();
        tablaPersonajes.setFillParent(true);
        stage.addActor(tablaPersonajes);

        imgIzq = new Image();
        imgDer = new Image();
        imgIzq.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        imgIzq.setAlign(Align.bottom);
        imgDer.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        imgDer.setAlign(Align.bottom);

        float hPersonaje = sh * 0.75f; 
        float wPersonaje = sw * 0.45f;

        tablaPersonajes.add(imgIzq).size(wPersonaje, hPersonaje).bottom().left().padLeft(60).padBottom(puntoApoyo);
        tablaPersonajes.add(imgDer).size(wPersonaje, hPersonaje).bottom().right().padRight(60).padBottom(puntoApoyo);

        Table tablePrincipal = new Table();
        tablePrincipal.setFillParent(true);
        tablePrincipal.bottom();
        stage.addActor(tablePrincipal);

        etiquetaTexto = new Label("", skin);
        etiquetaTexto.setWrap(true);
        etiquetaTexto.setAlignment(Align.topLeft);

        contenedorPrincipal = new Container<>(new Table());
        contenedorPrincipal.background(skin.getDrawable("textfield"));
        contenedorPrincipal.getColor().a = 0.92f;
        tablePrincipal.add(contenedorPrincipal).width(anchoCaja).height(altoCaja).padBottom(margenAbajo);

        imgFlecha = new Image(new Texture(Gdx.files.internal("flecha_continuar.png"))); 

        float tamFlecha = 50f; 
        imgFlecha.setSize(tamFlecha, tamFlecha);
        imgFlecha.setOrigin(Align.center);

        imgFlecha.addAction(Actions.forever(Actions.sequence(
            Actions.moveBy(0, 8, 0.6f, Interpolation.sine),
            Actions.moveBy(0, -8, 0.6f, Interpolation.sine)
        )));

        Table tablaFlecha = new Table();
        tablaFlecha.setFillParent(true); 
        tablaFlecha.bottom().right();    

        tablaFlecha.add(imgFlecha)
                   .size(tamFlecha, tamFlecha) 
                   .padRight((Gdx.graphics.getWidth() - anchoCaja) / 2 + 40) 
                   .padBottom(margenAbajo + 20);

        stage.addActor(tablaFlecha);

        Table tablaSuperior = new Table();
        tablaSuperior.setFillParent(true);
        tablaSuperior.top().right();
        stage.addActor(tablaSuperior);

        TextButton btnPausa = new TextButton("MENU", skin);
        btnPausa.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) { mostrarDialogoSalida(); }
        });
        tablaSuperior.add(btnPausa).pad(20);

        cargarEscena(idNarracionActual);
    }

    private void cargarEscena(int id) {
        if (id == 500) { comprobarFinal(); return; }
        
        NarracionDTO datos = repository.obtenerNarracion(id);
        if (datos != null) {
            imgFlecha.setVisible(false); 
            
            if (datos.fondo != null) {
                imgFondo.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("fondos/" + datos.fondo)))));
            }
            actualizarPersonaje(datos.personajeIzq, true);
            actualizarPersonaje(datos.personajeDer, false);
            gestionarMusica(datos.musica);
            
            if (datos.sonidoEfecto != null) {
                Gdx.audio.newSound(Gdx.files.internal("sonidos/" + datos.sonidoEfecto)).play();
            }

            mostrandoOpciones = false;
            Table tablaContenido = new Table();
            tablaContenido.add(etiquetaTexto).width(anchoCaja * 0.90f).expandY().top().padTop(35);
            contenedorPrincipal.setActor(tablaContenido);

            this.textoCompleto = datos.descripcion.replace("\\n", "\n"); 
            this.indiceActual = 0;                 
            this.tiempoAcumulado = 0f;             
            this.escribiendo = true;             
            etiquetaTexto.setText("");            

            idSiguienteNarracion = datos.idSiguiente;
            
            this.idNarracionActual = id;
            repository.actualizarProgreso(game.idUsuarioLogueado, idNarracionActual);
        }
    }
    
    private void actualizarMaquinaEscribir(float delta) {
        if (escribiendo) {
            tiempoAcumulado += delta;
            if (tiempoAcumulado >= velocidadEscritura) {
                tiempoAcumulado = 0;
                indiceActual++;
                if (indiceActual <= textoCompleto.length()) {
                    etiquetaTexto.setText(textoCompleto.substring(0, indiceActual));
                } else {
                    escribiendo = false;
                    imgFlecha.setVisible(true); 
                }
            }
        }
    }

    private void actualizarPersonaje(String ruta, boolean esIzq) {
        Image img = esIzq ? imgIzq : imgDer;
        img.clearActions();
        if (ruta == null || ruta.isEmpty()) {
            img.setDrawable(null);
        } else {
            img.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("personajes/" + ruta)))));
        }
    }

    private void mostrarOpciones() {
        List<OpcionDTO> opciones = repository.obtenerOpciones(idNarracionActual);
        if (opciones.isEmpty()) return;
        mostrandoOpciones = true;
        imgFlecha.setVisible(false);
        Table tOpc = new Table();
        tOpc.top().padTop(25);
        for (int i = 0; i < opciones.size(); i++) {
            final OpcionDTO o = opciones.get(i);
            final Label l = new Label((i + 1) + ". " + o.texto, skin);
            l.setWrap(true);
            l.addListener(new ClickListener() {
                @Override public void clicked(InputEvent e, float x, float y) { seleccionarOpcion(o); }
                @Override public void enter(InputEvent e, float x, float y, int p, Actor f) { l.setColor(skin.getColor("aguamarina")); }
                @Override public void exit(InputEvent e, float x, float y, int p, Actor t) { l.setColor(Color.WHITE); }
            });
            tOpc.add(l).width(anchoCaja * 0.85f).padBottom(10).row();
        }
        contenedorPrincipal.setActor(tOpc);
    }

    private void seleccionarOpcion(OpcionDTO s) {
        repository.guardarDecision(game.idUsuarioLogueado, s.idOpcion);
        if (s.idLogro != null) {
            if (repository.registrarLogro(game.idUsuarioLogueado, s.idLogro)) {
                mostrarNotificacionLogro(repository.obtenerNombreLogro(s.idLogro));
            }
        }
        idNarracionActual = s.idDestino;
        cargarEscena(idNarracionActual);
    }

    private void gestionarMusica(String m) {
        if (m == null || m.isEmpty() || m.equals(nombreMusicaActual)) return;
        if (musicaActual != null) { musicaActual.stop(); musicaActual.dispose(); }
        musicaActual = Gdx.audio.newMusic(Gdx.files.internal("musica/" + m));
        musicaActual.setLooping(true); musicaActual.setVolume(0.4f); musicaActual.play();
        nombreMusicaActual = m;
    }

    private void mostrarNotificacionLogro(String t) {
        if (sonidoLogro != null) sonidoLogro.play(0.6f);

        Texture texPatita = new Texture(Gdx.files.internal("patita.png"));
        Image imgIzquierda = new Image(texPatita);
        Image imgDerecha = new Image(texPatita);

        final Table tl = new Table();
        tl.setBackground(skin.getDrawable("textfield"));
        tl.pad(15);

        Label lblAviso = new Label("¡LOGRO CONSEGUIDO!", skin, "titulo");
        lblAviso.setFontScale(0.6f);
        Label lblNombre = new Label(t, skin);

        tl.add(imgIzquierda).size(32, 32).padRight(10);
        tl.add(lblAviso);
        tl.add(imgDerecha).size(32, 32).padLeft(10);
        tl.row(); 
        tl.add(lblNombre).colspan(3).padTop(5);
        tl.pack();
        tl.setPosition(25, Gdx.graphics.getHeight() + 100);
        stage.addActor(tl);

        tl.addAction(Actions.sequence(
            Actions.moveTo(25, Gdx.graphics.getHeight() - tl.getHeight() - 25, 0.7f, Interpolation.bounceOut),
            Actions.delay(3.5f),
            Actions.fadeOut(0.5f),
            Actions.removeActor()
        ));
    }

    private void mostrarDialogoSalida() {
        Dialog dialogo = new Dialog("PAUSA", skin, "dialog") {
            @Override
            protected void result(Object object) {
                int opcion = (Integer) object;
                switch (opcion) {
                    case 1: game.setScreen(new PantallaMenu(game)); break;
                    case 2: game.setScreen(new PantallaLogin(game)); break;
                    case 3: Gdx.app.exit(); break;
                }
            }
        };

        dialogo.text("¿Que deseas hacer?");
        dialogo.button("Volver al Menu", 1);
        dialogo.button("Cerrar Sesion", 2);
        dialogo.button("Salir del Juego", 3);
        dialogo.button("Cancelar", 4);
        dialogo.show(stage);
    }

    private void comprobarFinal() { cargarEscena(repository.haElegidoOpcion(game.idUsuarioLogueado, 1) ? 7 : 8); }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1);
        
        actualizarMaquinaEscribir(delta);

        boolean hayDialogo = false;
        for (Actor actor : stage.getActors()) {
            if (actor instanceof Dialog) hayDialogo = true;
        }

        if (!hayDialogo) {
            if (mostrandoOpciones) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) manejarTeclado(0);
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) manejarTeclado(1);
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) manejarTeclado(2);
                
            } else if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                
                if (escribiendo) {
                    escribiendo = false;
                    etiquetaTexto.setText(textoCompleto);
                    imgFlecha.setVisible(true);
                } else {
                    if (idSiguienteNarracion != null) {
                        idNarracionActual = idSiguienteNarracion;
                        cargarEscena(idNarracionActual);
                    } else {
                        mostrarOpciones();
                    }
                }
            }
        }

        stage.act(delta);
        stage.draw();
    }

    private void manejarTeclado(int i) {
        List<OpcionDTO> l = repository.obtenerOpciones(idNarracionActual);
        if (i < l.size()) seleccionarOpcion(l.get(i));
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { if (musicaActual != null) musicaActual.stop(); }
    @Override public void dispose() {
        stage.dispose();
        if (musicaActual != null) musicaActual.dispose();
        if (sonidoLogro != null) sonidoLogro.dispose(); 
    }
}