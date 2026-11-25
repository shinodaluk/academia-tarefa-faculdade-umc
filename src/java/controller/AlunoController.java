package controller;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "AlunoController", urlPatterns = {"/aluno"})
public class AlunoController extends ControladorAcoes {

    public AlunoController() {
        super("aluno");
    }
}
