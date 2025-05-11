package com.example.filter;

import com.example.utils.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@Order(Const.ORDER_CORS)
public class CorsFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.addCorsHeader(response,request);
        chain.doFilter(request, response);

    }
    private void addCorsHeader(HttpServletResponse response,HttpServletRequest request) {
         response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
         response.setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE,OPTIONS,HEAD,PATCH,TRACE");
         response.setHeader("Access-Control-Allow-Headers", "Authorization,Content-Type");
    }
}
