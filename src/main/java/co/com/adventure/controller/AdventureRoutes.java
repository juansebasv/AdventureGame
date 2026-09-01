package co.com.adventure.controller;

/** Rutas HTTP expuestas por la API, en un único sitio para evitar literales dispersos. */
public final class AdventureRoutes {

    public static final String BASE = "/app/adventure";
    public static final String OPTION_BY_ID = "/{id}";
    public static final String SAVE_SCORE = "/saveScore";
    public static final String SCORES = "/scores";

    private AdventureRoutes() {
        throw new AssertionError("No instanciable");
    }
}
