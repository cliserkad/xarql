/*
 * MIT License http://g.xarql.com Copyright (c) 2018 Bryan Christopher Johnson
 */

package com.xarql.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.NoSuchElementException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.xarql.auth.AuthSession;
import com.xarql.auth.AuthTable;
import com.xarql.polr.Post;
import com.xarql.user.Account;

/**
 * Helper class for servlets.
 *
 * @author Bryan Johnson
 */
public class ServletUtilities
{
    private static final String  DOMAIN              = DeveloperOptions.getDomain();
    private static final String  GOOGLE_ANALYTICS_ID = DeveloperOptions.getGoogleAnalyticsID();
    private static final String  RECAPTCHA_KEY       = DeveloperOptions.getRecaptchaKey();
    private static final boolean TESTING             = DeveloperOptions.getTesting();

    private static final int    NORMAL_FONT_WEIGHT = 400;
    private static final int    LIGHT_FONT_WEIGHT  = 200;
    private static final String DEFAULT_FONT_SIZE  = "1rem";
    private static final String DEFAULT_THEME      = "dark";

    private static final int     DEFAULT_INT     = 0;
    private static final boolean DEFAULT_BOOLEAN = false;

    private final HttpServletRequest request;

    /**
     * Allows for using static methods in an object to reduce typing
     *
     * @param request The request to send to static methods
     * @throws UnsupportedEncodingException No idea what this is. Ignore it
     */
    public ServletUtilities(HttpServletRequest request) throws UnsupportedEncodingException
    {
        this.request = request;
        standardSetup();
    }

    /**
     * Tries to get a String from a parameter and add the parameter to the request
     * as an attribute
     *
     * @param param The parameter from the user
     * @return The parameter's String
     */
    public String useParam(String param)
    {
        request.setAttribute(param, request.getParameter(param));
        return request.getParameter(param);
    }

    /**
     * Tries to get a String from a parameter and add the parameter to the request
     * as an attribute. Uses the fallback if the request doesn't have the parameter.
     *
     * @param param The name of the parameter
     * @param fallback A default
     * @return The String held by the parameter
     */
    public String useParam(String param, String fallback)
    {
        if(hasParam(param))
            return useParam(param);
        else
        {
            request.setAttribute(param, fallback);
            return fallback;
        }
    }

    public int useInt(String name)
    {
        return useInt(name, DEFAULT_INT);
    }

    public int useInt(String name, int fallback)
    {
        int output;
        try
        {
            output = Integer.parseInt(request.getParameter(name));
        }
        catch(NumberFormatException | NullPointerException e)
        {
            output = fallback;
        }
        request.setAttribute(name, output);
        return output;
    }

    public boolean useBoolean(String name)
    {
        return useBoolean(name, DEFAULT_BOOLEAN);
    }

    public boolean useBoolean(String name, boolean fallback)
    {
        boolean output;
        if(hasParam(name))
            output = Boolean.parseBoolean(request.getParameter(name));
        else
            output = fallback;
        request.setAttribute(name, output);
        return output;
    }

    /**
     * Sets several attributes to their universal defaults, others to cookies and
     * others to session details. Sets the Character Encoding to UTF-8.
     *
     * @throws UnsupportedEncodingException Shouldn't happen. Occurs if the server
     *         or client can't use UTF-8. Although that's extremely rare.
     */
    protected void standardSetup() throws UnsupportedEncodingException
    {
        request.setAttribute("domain", DOMAIN);
        request.setAttribute("google_analytics_id", GOOGLE_ANALYTICS_ID);
        request.setAttribute("recaptcha_key", RECAPTCHA_KEY);
        request.setAttribute("auth", userIsAuth());
        setTheme();
        setFontWeight();
        setFontSize();
        request.setCharacterEncoding("UTF-8");
        request.setAttribute("testing", TESTING);
        if(userHasAccount())
            request.setAttribute("account_name", getAccount().getUsername());
        else
            request.setAttribute("account_name", Post.DEFAULT_AUTHOR);
    }

    // FIXME I think this is broken lol
    private void setFontWeight()
    {
        // TODO: make this use setAttributeByCookie()
        // setAttributeByCookie(request, "font-weight", NORMAL_FONT_WEIGHT);
        int fontWeight = 0;
        Cookie[] cookies = request.getCookies();
        if(cookies != null)
        {
            for(Cookie item : cookies)
                if(item.getName().equals("font-weight"))
                {
                    if(item.getValue().equals("normal"))
                        fontWeight = NORMAL_FONT_WEIGHT;
                    else if(item.getValue().equals("light"))
                        fontWeight = LIGHT_FONT_WEIGHT;
                    request.setAttribute("font_weight", fontWeight);
                    return;
                }
            request.setAttribute("font_weight", NORMAL_FONT_WEIGHT); // default
        }
        else
            request.setAttribute("font_weight", NORMAL_FONT_WEIGHT);
    }

    public void setFontSize()
    {
        setAttributeByCookie("font-size", DEFAULT_FONT_SIZE);
    }

    public void setAttributeByCookie(String cookie, Object fallback)
    {
        String insertableName = cookie.replace('-', '_');
        // Sort through all of the cookies
        Cookie[] cookies = request.getCookies();
        if(cookies != null)
        {
            for(Cookie item : cookies)
                if(item.getName().equals(cookie))
                {
                    request.setAttribute(insertableName, item.getValue());
                    return;
                }
            request.setAttribute(insertableName, fallback); // default
        }
        else
            request.setAttribute(insertableName, fallback);
    }

    /**
     * Determines if the user that made a request is a moderator. Checks the
     * AuthTable for the Tomcat session ID cookie's status.
     *
     * @return <code>true</code> if the user is a moderator, <code>false</code>
     *         otherwise.
     */
    public boolean userIsMod()
    {
        return AuthTable.get(request.getRequestedSessionId()) != null && AuthTable.get(request.getRequestedSessionId()).isMod();
    }

    /**
     * Determines if the user that made a request is an authorized user believed to
     * be human. Checks the AuthTable for the Tomcat session ID cookie's status.
     *
     * @return <code>true</code> if the user is authorized, <code>false</code>
     *         otherwise.
     */
    public boolean userIsAuth()
    {
        return request.getRequestedSessionId() != null && AuthTable.contains(request.getRequestedSessionId());
    }

    public AuthSession getAuthSession()
    {
        if(request.getRequestedSessionId() != null && AuthTable.contains(request.getRequestedSessionId()))
            return AuthTable.get(request.getRequestedSessionId());
        else
            return null;
    }

    public boolean userHasAccount()
    {
        return AuthTable.get(request.getRequestedSessionId()) != null && AuthTable.get(request.getRequestedSessionId()).getAccount() != null;
    }

    public Account getAccount()
    {
        return AuthTable.get(request.getRequestedSessionId()).getAccount();
    }

    /**
     * Adds a "theme" attribute to the request. This is used to chose a style sheet
     * in a .jsp file.
     */
    public void setTheme()
    {
        setAttributeByCookie("theme", DEFAULT_THEME);
    }

    /**
     * Used to prevent get methods on endpoints meant for submitting content
     *
     * @param response Response to use for the error
     * @throws IOException Something went wrong with http
     */
    public static void rejectGetMethod(HttpServletResponse response) throws IOException
    {
        response.sendError(405, "Can't use HTTP GET method for this URI");
    }

    public static void rejectPostMethod(HttpServletResponse response) throws IOException
    {
        response.sendError(405, "Can't use HTTP POST method for this URI");
    }

    /**
     * Checks to see if all of the given parameters are not null and not empty
     *
     * @param parameters The parameters from the client, often represented in the
     *        URL
     * @return true if the parameters are usable, false otherwise
     */
    public boolean hasParams(String... parameters)
    {
        for(String param : parameters)
            if(!hasParam(param))
                return false;
        return true;
    }

    /**
     * Checks to see if the given parameter is not null and not empty
     *
     * @param parameter The parameter from the client, often represented in the URL
     * @return true if the parameter is usable, false otherwise
     */
    public boolean hasParam(String parameter)
    {
        return !(request.getParameter(parameter) == null || request.getParameter(parameter).equals(""));
    }

    public String require(String parameter)
    {
        if(hasParam(parameter) && !request.getParameter(parameter).trim().equals(""))
            return request.getParameter(parameter).trim();
        else
            throw new NoSuchElementException("The desired parameter (" + parameter + ") wasn't included");
    }

    public int requireInt(String parameter)
    {
        if(hasParam(parameter))
            try
            {
                int tmp = Integer.parseInt(request.getParameter(parameter).toString());
                request.setAttribute(parameter, tmp);
                return tmp;
            }
            catch(NumberFormatException nfe)
            {
                throw new NoSuchElementException("The desired parameter (" + parameter + ") was not an int");
            }
        else
            throw new NoSuchElementException("The desired parameter (" + parameter + ") wasn't included");
    }

    protected final HttpServletRequest getRequest()
    {
        return request;
    }

}
