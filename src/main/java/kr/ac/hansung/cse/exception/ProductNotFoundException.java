package kr.ac.hansung.cse.exception;

public class ProductNotFoundException extends RuntimeException {

    // 어떤 ID를 조회했을 때 발생했는지 기록
    private final Long productId;

    /**
     * @param id 존재하지 않는 상품 ID
     */
    public ProductNotFoundException(Long id) {
        // 부모 클래스(RuntimeException)에 오류 메시지 전달
        super("존재하지 않는 상품 ID: " + id);
        this.productId = id;
    }

    /**
     * 예외 핸들러에서 ID 정보를 활용할 때 사용합니다.
     * 예) 로그에 어떤 ID를 조회했다가 실패했는지 기록
     */
    public Long getProductId() {
        return productId;
    }
}
