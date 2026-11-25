package controller;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "FichaController", urlPatterns = {"/ficha"})
public class FichaController extends ControladorAcoes {

    public FichaController() {
        super("ficha");
    }
}
