package com.bmuschko.gradle.cargo.util.fixture

import groovy.transform.InheritConstructors

@InheritConstructors
class HelloWorldServletWarFixture extends AbstractWarFixture {

    public final static String RESPONSE_TEXT = 'Hello World!'

    String getServletClassName() {
        "HelloWorld"
    }

    String getServletClassSource() {
        """
            import javax.servlet.http.HttpServlet;
            import javax.servlet.http.HttpServletRequest;
            import javax.servlet.http.HttpServletResponse;
            import java.io.IOException;
            
            public class $servletClassName extends HttpServlet {
                @Override
                protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
                    response.setContentType("text/plain");
                    response.getWriter().print("$RESPONSE_TEXT");
                }
            }
        """
    }
}
