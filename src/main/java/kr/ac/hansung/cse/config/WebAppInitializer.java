package kr.ac.hansung.cse.config;

import jakarta.servlet.Filter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebAppInitializer
        extends AbstractAnnotationConfigDispatcherServletInitializer {

    // DB · Service · Repository 관련 빈 → Root Context에 등록
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{ DbConfig.class };
    }

    // Controller · ViewResolver 관련 빈 → Servlet Context에 등록
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{ WebConfig.class };
    }

    // DispatcherServlet이 처리할 URL 패턴 ("/" = 모든 요청)
    @Override
    protected String[] getServletMappings() {
        return new String[]{ "/" };
    }

    // 한글 인코딩 필터: 모든 요청/응답을 UTF-8로 강제 설정
    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return new Filter[]{ filter };
    }
}
