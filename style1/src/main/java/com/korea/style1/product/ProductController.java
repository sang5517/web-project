package com.korea.style1.product;


import com.korea.style1.productreview.ProductReviewForm;
import com.korea.style1.user.SiteUser;
import com.korea.style1.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;



@RequestMapping("/product")
@RequiredArgsConstructor
@Controller
public class ProductController {

  private final ProductService productService;
  private final UserService userService;


  @GetMapping("/list")
  public String list(Model model,@RequestParam(value = "page",defaultValue = "0")int page,
                     @RequestParam(value = "kw",defaultValue = "")String kw,@RequestParam(value = "category", required = false) String category){
    Page<Product> paging;
    if(category != null && !category.isEmpty()){
    paging = this.productService.getList(page,kw,category);
    }else {
      paging = this.productService.getList(page,kw,category);
    }

    model.addAttribute("paging", paging);
    model.addAttribute("kw", kw);
    model.addAttribute("category", category);
    return "product_list";
  }

  @GetMapping("/detail/{id}")
  public String detail(Model model, @PathVariable("id")Long id, ProductReviewForm productReviewForm){
    Product product = this.productService.getProduct(id);
    model.addAttribute("product",product);
    return "product_detail";
  }
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/create")
  public String productCreate(ProductForm productForm){
    return "product_form";
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PostMapping("/create")
  public String productCreate(@Valid ProductForm productForm, BindingResult bindingResult, Principal principal,
                              @RequestParam("file") MultipartFile file, @RequestParam("category")String category, RedirectAttributes redirectAttributes){
    if(bindingResult.hasErrors()){
      return "product_form";
    }

    if (file.isEmpty()) {
      // 파일이 비어있을 때 처리
      redirectAttributes.addFlashAttribute("message", "파일을 선택하세요.");
    } else {
      try {

        // 파일 저장 경로 설정
        String uploadDir = "C:\\fashion\\style1\\src\\main\\resources\\static";
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
          uploadPath.mkdirs(); // 폴더가 없으면 만들어라
        }



        // 파일 저장
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.write(filePath, file.getBytes());

        // 이미지 경로(리스트에서 보여줄 때 불러올 것임)
        String url = "/" + fileName;

        SiteUser user = userService.getUser(principal.getName()); // 등록 유저 정보

        productService.create(productForm.getName(), productForm.getContent(), url, user,productForm.getKww(),category); // 상품 정보를 (이미지 정보 포함) 등록

        // 파일 업로드 성공 메시지
        redirectAttributes.addFlashAttribute("message", "파일 업로드에 성공했습니다.");
      } catch (IOException e) {
        // 파일 업로드 실패 메시지
        redirectAttributes.addFlashAttribute("message", "파일 업로드에 실패했습니다.");
      }


    }

//    SiteUser siteUser = this.userService.getUser(principal.getName());
//    productForm.setImageUrl("https://search.pstatic.net/common/?src=https%3A%2F%2Fshopping-phinf.pstatic.net%2Fmain_8678212%2F86782120072.jpg&type=f372_372");
//    this.productService.create(productForm.getName(),productForm.getContent(),productForm.getImageUrl(), siteUser);
    return "redirect:/product/list";  // 리스트 화면으로 가자
  }
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/modify/{id}")
  public String productModifyForm(@Valid ProductForm productForm, BindingResult bindingResult, Principal principal,
                                  @PathVariable("id") Long id, Model model) {
    if (bindingResult.hasErrors()) {
      return "product_form";
    }

    Product product = this.productService.getProduct(id);
    if (!product.getAuthor().getUsername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
    }

    model.addAttribute("productForm", productForm);
    return "product_form";
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/modify/{id}")
  public String productModify(@Valid ProductForm productForm, BindingResult bindingResult,
                              Principal principal, @PathVariable("id") Long id) {
    if (bindingResult.hasErrors()) {
      return "product_form";
    }

    Product product = this.productService.getProduct(id);
    if (!product.getAuthor().getUsername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
    }

    this.productService.modify(id, productForm.getName(), productForm.getContent());
    return String.format("redirect:/product/detail/%s", id);
  }
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/delete/{id}")
  public String productDelete(Principal principal,@PathVariable("id") Long id){
    Product product = this.productService.getProduct(id);
    if(!product.getAuthor().getUsername().equals(principal.getName())){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"삭제권한이 없습니다.");
    }
    this.productService.delete(product);
    return "redirect:/";
  }
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/vote/{id}")
  public String productVote(Principal principal,@PathVariable("id")Long id){
    Product product =this.productService.getProduct(id);
    SiteUser siteUser = this.userService.getUser(principal.getName());
    this.productService.vote(product,siteUser);
    return String.format("redirect:/product/detail/%s",id);
  }
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/image")
  public String productImage(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "kw", defaultValue = "") String kw, String category){
    Page<Product> paging = this.productService.getList(page, kw, category);
    model.addAttribute("paging", paging);
    model.addAttribute("kw", kw);
    return "product_image";
  }
  @GetMapping("/search")
  public String search(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
    if (kw == null || kw.trim().isEmpty()) {
      // 검색어가 비어 있거나 공백만 포함된 경우 리스트 페이지로 이동
      return "redirect:/product/list";
    }

    Page<Product> paging = productService.getList(page, kw, null);
    model.addAttribute("paging", paging);
    model.addAttribute("kw", kw);
    return "search_result";
  }
}
