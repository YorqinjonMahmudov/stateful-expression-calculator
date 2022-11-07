import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;

@WebFilter(filterName = "MyFilter",urlPatterns = "/*")
public class MyFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (request.getRequestURI().startsWith("/calc/")) {
            filterChain.doFilter(request, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
