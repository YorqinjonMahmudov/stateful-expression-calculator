

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

@WebServlet(name = "Calculator", urlPatterns = {"/calc/{variable}", "/calc/result", "/calc/expression", "/calc/*"})
public class Calculator extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Writer writer = resp.getWriter();
        HttpSession session = req.getSession();
        String expression = session.getAttribute("expression").toString().replace(" ", "").trim();
        StringBuilder builder = new StringBuilder(2 * expression.length());
        char c;
        Object val;
        for (int i = 0; i < expression.length(); i++) {
            c = expression.charAt(i);
            if (c >= 'a' && c <= 'z') {
                val = session.getAttribute(String.valueOf(c));
                if(val == null){
                    resp.setStatus(409);
                    writer.write("invalid " + c + "    argument");
                    return;
                }
                if ((val.toString().charAt(0) >= '0' && val.toString().charAt(0) <= '9' || val.toString().charAt(0) <= '-'))
                    builder.append(Integer.parseInt(val.toString()));
                else {
                    val = session.getAttribute(val.toString());
                    builder.append(Integer.parseInt((String) val));
                }
            } else builder.append(c);
        }
        resp.setStatus(200);
        writer.write(String.valueOf(calc(builder.toString())));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        HttpSession session = req.getSession();
        if (req.getRequestURI().endsWith("/expression")) {
            if (session.getAttribute("expression") != null)
                resp.setStatus(200);
            else resp.setStatus(201);
            String exp = reader.readLine();
            if (exp.equals("bad format")) {
                resp.setStatus(400);
                return;
            }
            session.setAttribute("expression", exp);
        } else if (req.getRequestURI().startsWith("/calc/")) {
            String variable = req.getRequestURI().split("/")[2], value = reader.readLine();
            if(value != null
                    && ( (value.charAt(0) >= '0' &&
                    value.charAt(0) <= '9') || value.charAt(0) == '-')
                    && (Integer.parseInt(value) >10000 || Integer.parseInt(value) < -10000)){
                resp.setStatus(403);
                return;
            }
            if (session.getAttribute(variable) != null)
                resp.setStatus(200);
            else
                resp.setStatus(201);
            session.setAttribute(variable, value);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String part = req.getRequestURI().split("/")[2];
        HttpSession session = req.getSession();
        if(session.getAttribute(part) == null){
            resp.setStatus(409);
            resp.getWriter().write(part + "   element not valid");
            return;
        }
        session.setAttribute(part, null);
        resp.setStatus(204);
    }

    public static int calc(String expression) {
        String[] containerArr = new String[]{expression};
        int leftVal = getNextOperand(containerArr);
        expression = containerArr[0];
        if (expression.length() == 0) {
            return leftVal;
        }
        char operator = expression.charAt(0);
        expression = expression.substring(1);

        while (operator == '*' || operator == '/') {
            containerArr[0] = expression;
            int rightVal = getNextOperand(containerArr);
            expression = containerArr[0];
            if (operator == '*') {
                leftVal = leftVal * rightVal;
            } else {
                leftVal = leftVal / rightVal;
            }
            if (expression.length() > 0) {
                operator = expression.charAt(0);
                expression = expression.substring(1);
            } else {
                return leftVal;
            }
        }
        if (operator == '+') {
            return leftVal + calc(expression);
        } else {
            return leftVal - calc(expression);
        }

    }

    private static int getNextOperand(String[] exp) {
        int res;
        if (exp[0].startsWith("(")) {
            int open = 1;
            int i = 1;
            while (open != 0) {
                if (exp[0].charAt(i) == '(') {
                    open++;
                } else if (exp[0].charAt(i) == ')') {
                    open--;
                }
                i++;
            }
            res = calc(exp[0].substring(1, i - 1));
            exp[0] = exp[0].substring(i);
        } else {
            int i = 1;
            if (exp[0].charAt(0) == '-') {
                i++;
            }
            while (exp[0].length() > i && isNumber(exp[0].charAt(i))) {
                i++;
            }
            res = Integer.parseInt(exp[0].substring(0, i));
            exp[0] = exp[0].substring(i);
        }
        return res;
    }


    private static boolean isNumber(int c) {
        int zero = '0';
        int nine = '9';
        return c >= zero && c <= nine;
    }
}
