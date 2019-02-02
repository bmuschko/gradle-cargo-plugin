package com.bmuschko.gradle.cargo.util.fixture

import groovy.transform.InheritConstructors

@InheritConstructors
class TextResourceLoaderServletWarFixture extends AbstractWarFixture {

    String getServletClassName() {
        "TextResourceLoader"
    }

    String getServletClassSource() {
        """
            import javax.naming.Context;
            import javax.naming.InitialContext;
            import javax.naming.NamingException;
            import javax.servlet.http.HttpServlet;
            import javax.servlet.http.HttpServletRequest;
            import javax.servlet.http.HttpServletResponse;
            import java.io.IOException;
            
            public class $servletClassName extends HttpServlet {
            
                @Override
                protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
                    String resourceName = request.getParameter("resourceName");
            
                    response.setContentType("text/plain");
                    response.getWriter().print(lookupTextResource(resourceName));
                }
            
                private String lookupTextResource(String name) {
                    try {
                        Context context = (Context) new InitialContext().lookup("java:comp/env");
                        return (String) context.lookup(name);
                    } catch (NamingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        """
    }

}
