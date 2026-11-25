package controller;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "MatriculaController", urlPatterns = {"/matricula"})
public class MatriculaController extends ControladorAcoes {

    public MatriculaController() {
        super("matricula");
    }
}
