/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package factory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ptoledo
 */
public interface ICommand {
    
    /**
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public void executar(HttpServletRequest request, HttpServletResponse response) throws Exception;
    
}
