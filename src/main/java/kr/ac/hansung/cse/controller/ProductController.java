package kr.ac.hansung.cse.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.cse.exception.ProductNotFoundException;
import kr.ac.hansung.cse.model.Product;
import kr.ac.hansung.cse.model.ProductForm;
import kr.ac.hansung.cse.service.CategoryService;
import kr.ac.hansung.cse.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }


    // ─────────────────────────────────────────────────────────────────
    // GET /products - 상품 목록 조회
    // ─────────────────────────────────────────────────────────────────

    @GetMapping
    public String listProducts(@RequestParam(required = false) String keyword, // GET ?keyword=노트북
                               @RequestParam(required = false) Long categoryId,  // GET ?categoryId=1
                               Model model) {

        List<Product> products;

        if(keyword != null && !keyword.isBlank()){
            products = productService.searchByName(keyword);
        }
        else if(categoryId != null){
            products = productService.searchByCategory(categoryId);
        }
        else {
            products = productService.getAllProducts();
        }

        // 상품 자체가 없는 것과 검색 결과가 없는 메시지 구분
        boolean isSearch = (keyword != null && !keyword.isBlank()) || categoryId != null;

        // 카테고리 드롭다운 목록 + 현재 검색 조건 유지
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("isSearch", isSearch);

        return "productList";
    }

    // ─────────────────────────────────────────────────────────────────
    // GET /products/{id} - 상품 상세 조회
    // ─────────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public String showProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        model.addAttribute("product", product);
        return "productView";
    }

    // ─────────────────────────────────────────────────────────────────
    // GET /products/create - 상품 등록 폼 표시
    // ─────────────────────────────────────────────────────────────────

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("productForm", new ProductForm());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "productForm";
    }

    // ─────────────────────────────────────────────────────────────────
    // POST /products/create - 상품 등록 처리
    // ─────────────────────────────────────────────────────────────────

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("productForm") ProductForm productForm,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        // 검증 오류가 있으면 폼을 다시 표시
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "productForm"; // 오류가 있는 채로 폼 뷰 재표시
        }

        // 검증 통과: ProductForm → Product 엔티티 변환 후 저장
        // category 이름으로 Category 엔티티를 조회하여 연결합니다.
        Product product = productForm.toEntity();
        product.setCategory(productService.resolveCategory(productForm.getCategory()));
        Product savedProduct = productService.createProduct(product);

        // Flash 속성: 리다이렉트 후 한 번만 표시되는 메시지
        redirectAttributes.addFlashAttribute("successMessage",
                "'" + savedProduct.getName() + "' 상품이 성공적으로 등록되었습니다.");

        // PRG 패턴: POST → Redirect → GET (중복 제출 방지)
        return "redirect:/products";
    }

    // ─────────────────────────────────────────────────────────────────
    // GET /products/{id}/edit - 상품 수정 폼 표시
    // ─────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // 엔티티 → DTO 변환 (기존 데이터로 폼 초기화)
        model.addAttribute("productForm", ProductForm.from(product));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "productEditForm";
    }

    // ─────────────────────────────────────────────────────────────────
    // POST /products/{id}/edit - 상품 수정 처리
    // ─────────────────────────────────────────────────────────────────

    @PostMapping("/{id}/edit")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("productForm") ProductForm productForm,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "productEditForm"; // 오류가 있는 채로 수정 폼 재표시
        }

        // 기존 엔티티 조회 (준영속 상태로 반환됨)
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // 폼 데이터로 엔티티 필드를 업데이트합니다.
        product.setName(productForm.getName());
        product.setCategory(productService.resolveCategory(productForm.getCategory()));
        product.setPrice(productForm.getPrice());
        product.setDescription(productForm.getDescription());

        // merge()를 통해 준영속 엔티티의 변경사항을 DB에 반영합니다.
        productService.updateProduct(product);

        redirectAttributes.addFlashAttribute("successMessage",
                "'" + product.getName() + "' 상품 정보가 수정되었습니다.");
        return "redirect:/products/" + id;
    }

    // ─────────────────────────────────────────────────────────────────
    // POST /products/{id}/delete - 상품 삭제 처리
    // ─────────────────────────────────────────────────────────────────

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {

        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        String productName = product.getName(); // 삭제 전 이름 저장
        productService.deleteProduct(id);

        redirectAttributes.addFlashAttribute("successMessage",
                "'" + productName + "' 상품이 삭제되었습니다.");
        return "redirect:/products";
    }
}
