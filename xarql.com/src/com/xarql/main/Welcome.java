/*
 * MIT License http://g.xarql.com Copyright (c) 2018 Bryan Christopher Johnson
 */
package com.xarql.main;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xarql.auth.AuthTable;
import com.xarql.util.BuildTimer;
import com.xarql.util.ServletUtilities;

/**
 * Servlet implementation class Welcome
 */
@WebServlet ("/Welcome")
public class Welcome extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    private static final String CONTEXT = DeveloperOptions.getContext();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Welcome()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        BuildTimer bt = new BuildTimer(request);
        ServletUtilities.standardSetup(request);
        request.setAttribute("auth_sessions", AuthTable.size());

        int activeSessions = 0;
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName;
        try
        {
            objectName = new ObjectName("Catalina:type=Manager,context=/" + CONTEXT + ",host=localhost");
            activeSessions = (Integer) mBeanServer.getAttribute(objectName, "activeSessions");
        }
        catch(Exception e)
        {
            // Do nothing
        }

        request.setAttribute("total_sessions", activeSessions);
        request.getRequestDispatcher("/src/welcome/welcome.jsp").forward(request, response);
    } // doGet()

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // TODO Auto-generated method stub
        doGet(request, response);
    } // doPost()

} // Welcome
