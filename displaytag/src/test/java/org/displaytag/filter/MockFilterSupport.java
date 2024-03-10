package org.displaytag.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Simulates the behaviour of a filter using a simple servlet. The servlet must be mapped to the "*.filtered" extension;
 * request include this extension after the name of the tested jsp. Since servletunit doesn't support filter testing, we
 * are passing the request to this servlet which calls the filter and then forward the request to the given path without
 * ".filtered". <strong>Replaced by native filter support in ServletUnit 1.6. </strong>
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */
public class MockFilterSupport extends HttpServlet
{

    /**
     * extension mapped to this servlet.
     */
    public static final String FILTERED_EXTENSION = ".filtered";

    /**
     * logger.
     */
    protected static Log log = LogFactory.getLog(MockFilterSupport.class);

    /**
     * D1597A17A6.
     */
    private static final long serialVersionUID = 899149338534L;

    /**
     * @see jakarta.servlet.http.HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        log.debug("Mock servlet called, simulating filter");
        Filter filter = new ResponseOverrideFilter();
        filter.init(null);
        filter.doFilter(request, response, new MockFilterChain());
    }

    /**
     * Simple FilterChain used to test Filters.
     */
    public static class MockFilterChain implements FilterChain
    {

        /**
         * @see jakarta.servlet.FilterChain#doFilter(jakarta.servlet.ServletRequest, jakarta.servlet.ServletResponse)
         */
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException
        {
            String uri = ((HttpServletRequest) request).getRequestURI();
            String requestContext = ((HttpServletRequest) request).getContextPath();

            if (StringUtils.isNotEmpty(requestContext) && uri.startsWith(requestContext))
            {
                uri = uri.substring(requestContext.length());
            }

            uri = StringUtils.replace(uri, FILTERED_EXTENSION, "");

            if (log.isDebugEnabled())
            {
                log.debug("Redirecting to [" + uri + "]");
            }
            RequestDispatcher dispatcher = request.getRequestDispatcher(uri);
            dispatcher.forward(request, response);
        }

    }

}