package io.agar.controllers;

import io.agar.Agar;

public abstract class Controller {
    protected final Agar agar;

    public Controller(Agar agar) {
        this.agar = agar;
    }

    public abstract void tick();
}
