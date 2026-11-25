package controller;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "InstrutorController", urlPatterns = {"/instrutor"})
public class InstrutorController extends ControladorAcoes {

    public InstrutorController() {
        super("instrutor");
    }
}
