package kr.ac.hansung.cse.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.cse.exception.DuplicateCategoryException;
import kr.ac.hansung.cse.exception.ProductNotFoundException;
import kr.ac.hansung.cse.model.Category;
import kr.ac.hansung.cse.model.CategoryForm;
import kr.ac.hansung.cse.model.Product;
import kr.ac.hansung.cse.model.ProductForm;
import kr.ac.hansung.cse.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categoryList";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model){
        model.addAttribute("categoryForm", new CategoryForm());
        return "categoryForm";
    }

    @PostMapping("/create")
    public String createCategory(@Valid @ModelAttribute CategoryForm categoryForm,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "categoryForm"; // 오류가 있는 채로 폼 뷰 재표시
        }

        try{
            categoryService.createCategory(categoryForm.getName());
            redirectAttributes.addFlashAttribute("successMessage", "등록 완료");
        }
        catch(DuplicateCategoryException e){
            // 중복 예외
            bindingResult.rejectValue("name", "duplicate", e.getMessage());
            return "categoryForm";
        }
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes){
        try{
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "삭제 완료");
        }
        catch(IllegalStateException e){
            // 연결된 상품이 있을 때
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/categories";
    }
//
//    // ─────────────────────────────────────────────────────────────────
//    // GET /products/{id}/edit - 상품 수정 폼 표시
//    // ─────────────────────────────────────────────────────────────────
//
//    /**
//     * 기존 상품 데이터를 조회하여 수정 폼에 채워 표시합니다.
//     * ProductForm.from(product)으로 엔티티 → DTO 변환합니다.
//     */
//    @GetMapping("/{id}/edit")
//    public String showEditForm(@PathVariable Long id, Model model) {
//        Product product = productService.getProductById(id)
//                .orElseThrow(() -> new ProductNotFoundException(id));
//
//        // 엔티티 → DTO 변환 (기존 데이터로 폼 초기화)
//        model.addAttribute("productForm", ProductForm.from(product));
//        return "productEditForm";
//    }
//
//    // ─────────────────────────────────────────────────────────────────
//    // POST /products/{id}/edit - 상품 수정 처리
//    // ─────────────────────────────────────────────────────────────────
//
//    /**
//     * [JPA 엔티티 수정 방식 설명]
//     *
//     * getProductById()는 readOnly 트랜잭션에서 실행됩니다.
//     * 반환된 Product 엔티티는 트랜잭션 종료 후 "준영속(Detached) 상태"가 됩니다.
//     *
//     * 준영속 상태의 특징:
//     *   - 영속성 컨텍스트가 관리하지 않습니다.
//     *   - setter를 호출해도 DB에 반영되지 않습니다.
//     *   - merge()를 통해 다시 영속 상태로 만들 수 있습니다.
//     *
//     * updateProduct()에서 EntityManager.merge(product)를 호출하면:
//     *   - 새 트랜잭션이 시작됩니다.
//     *   - Hibernate가 동일 ID의 레코드를 DB에서 SELECT합니다.
//     *   - 준영속 엔티티의 변경된 값을 관리 엔티티에 복사합니다.
//     *   - 트랜잭션 커밋 시 UPDATE SQL이 자동 실행됩니다.
//     */
//    @PostMapping("/{id}/edit")
//    public String updateProduct(@PathVariable Long id,
//                                @Valid @ModelAttribute("productForm") ProductForm productForm,
//                                BindingResult bindingResult,
//                                RedirectAttributes redirectAttributes) {
//
//        if (bindingResult.hasErrors()) {
//            return "productEditForm"; // 오류가 있는 채로 수정 폼 재표시
//        }
//
//        // 기존 엔티티 조회 (준영속 상태로 반환됨)
//        Product product = productService.getProductById(id)
//                .orElseThrow(() -> new ProductNotFoundException(id));
//
//        // 폼 데이터로 엔티티 필드를 업데이트합니다.
//        product.setName(productForm.getName());
//        product.setCategory(productService.resolveCategory(productForm.getCategory()));
//        product.setPrice(productForm.getPrice());
//        product.setDescription(productForm.getDescription());
//
//        // merge()를 통해 준영속 엔티티의 변경사항을 DB에 반영합니다.
//        productService.updateProduct(product);
//
//        redirectAttributes.addFlashAttribute("successMessage",
//                "'" + product.getName() + "' 상품 정보가 수정되었습니다.");
//        return "redirect:/products/" + id;
//    }
//
//    // ─────────────────────────────────────────────────────────────────
//    // POST /products/{id}/delete - 상품 삭제 처리
//    // ─────────────────────────────────────────────────────────────────
//
//    /**
//     * HTML 폼은 GET/POST만 지원하므로 DELETE 대신 POST를 사용합니다.
//     * (REST API에서는 HTTP DELETE 메서드를 사용하는 것이 표준입니다.)
//     *
//     * 삭제 전 상품 이름을 미리 조회하여 성공 메시지에 포함합니다.
//     * 삭제 후에는 상세 페이지로 돌아갈 수 없으므로 목록으로 리다이렉트합니다.
//     */
//    @PostMapping("/{id}/delete")
//    public String deleteProduct(@PathVariable Long id,
//                                RedirectAttributes redirectAttributes) {
//
//        Product product = productService.getProductById(id)
//                .orElseThrow(() -> new ProductNotFoundException(id));
//
//        String productName = product.getName(); // 삭제 전 이름 저장
//        productService.deleteProduct(id);
//
//        redirectAttributes.addFlashAttribute("successMessage",
//                "'" + productName + "' 상품이 삭제되었습니다.");
//        return "redirect:/products";
//    }
}
