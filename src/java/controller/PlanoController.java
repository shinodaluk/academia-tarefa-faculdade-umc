package controller;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "PlanoController", urlPatterns = {"/plano"})
public class PlanoController extends ControladorAcoes {

    public PlanoController() {
        super("plano");
    }
}
