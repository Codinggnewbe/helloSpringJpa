package kr.ac.hansung.cse.service;

import kr.ac.hansung.cse.model.Category;
import kr.ac.hansung.cse.model.Product;
import kr.ac.hansung.cse.repository.CategoryRepository;
import kr.ac.hansung.cse.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true) // 클래스 기본값: 읽기 전용 트랜잭션
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * 카테고리 이름(String) → Category 엔티티 변환
     * 폼에서 받은 카테고리 이름을 DB에서 조회하여 엔티티로 변환합니다.
     * 비즈니스 로직이므로 Service 계층에 위치합니다.
     */
    public Category resolveCategory(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) return null;
        return categoryRepository.findByName(categoryName).orElse(null);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

}
