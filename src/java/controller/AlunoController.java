/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import factory.ICommand;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author alunocmc
 */
@WebServlet(name = "AlunoController", urlPatterns = {"/aluno"})
public class AlunoController extends HttpServlet {
    
    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            try {
                String paramAction = request.getParameter("acao");
                
                if(paramAction == null) {
                    response.sendRedirect(request.getContextPath() + "/aluno?acao=listar");
                    return;
                }
                
                String nomeDaClasse = "factory.aluno." + (paramAction.substring(0, 1).toUpperCase() + paramAction.substring(1))+"Action";
                Class classAction = Class.forName(nomeDaClasse);                
                ICommand commandAction = (ICommand) classAction.getDeclaredConstructor().newInstance();
                
                commandAction.executar(request, response);
            } catch (Exception e) {
                out.println(e.getMessage());
                out.println(ExceptionUtils.getStackTrace(e));
            }
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
