package kr.ac.hansung.cse.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleProductNotFound(ProductNotFoundException ex, Model model) {
        model.addAttribute("errorCode", "404");
        model.addAttribute("errorTitle", "상품을 찾을 수 없습니다");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorDetail",
                "요청하신 상품 ID (" + ex.getProductId() + ")에 해당하는 상품이 존재하지 않습니다.");
        return "error"; // → /WEB-INF/views/error.html
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        model.addAttribute("errorCode", "400");
        model.addAttribute("errorTitle", "잘못된 요청");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorDetail", "입력하신 데이터를 확인하고 다시 시도해 주세요.");
        return "error";
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDataAccessException(DataAccessException ex, Model model) {
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorTitle", "데이터베이스 오류");
        model.addAttribute("errorMessage", "데이터베이스 처리 중 오류가 발생했습니다.");
        model.addAttribute("errorDetail", "잠시 후 다시 시도해 주세요. 문제가 지속되면 관리자에게 문의하세요.");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorTitle", "서버 오류");
        model.addAttribute("errorMessage", "예상치 못한 오류가 발생했습니다.");
        model.addAttribute("errorDetail", "잠시 후 다시 시도해 주세요. 문제가 지속되면 관리자에게 문의하세요.");
        return "error";
    }
}
