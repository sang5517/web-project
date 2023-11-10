package com.korea.style1.productreview;


import com.korea.style1.product.Product;
import com.korea.style1.product.ProductService;
import com.korea.style1.user.SiteUser;
import com.korea.style1.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;


@RequestMapping("/productReview")
@RequiredArgsConstructor
@Controller
public class ProductReviewController {
  private final ProductService productService;
  private final ProductReviewService productReviewService;
  private final UserService userService;


  @PreAuthorize("isAuthenticated()")
  @PostMapping("/create/{id}")
  public String createProductreview(Model model, @PathVariable("id") Long id,
                                    @Valid ProductReviewForm productReviewForm, BindingResult bindingResult,@RequestParam("rating") int rating, Principal principal){
    Product product = this.productService.getProduct(id);
    SiteUser siteUser = this.userService.getUser(principal.getName());
    if(bindingResult.hasErrors()){
      model.addAttribute("product",product);
      return "product_detail";
    }
    ProductReview productReview = this.productReviewService.create(product,productReviewForm.getContent(),rating,siteUser);
    return String.format("redirect:/product/detail/%s#productReview_%s",productReview.getProduct().getId(),productReview.getId());
  }
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/modify/{id}")
  public String productReviewModify(ProductReviewForm productReviewForm,@PathVariable("id")Long id,Principal principal){
    ProductReview productReview = this.productReviewService.getProductReview(id);
    if(!productReview.getAuthor().getUsername().equals(principal.getName())){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정권한이 없습니다.");
    }
    productReviewForm.setContent(productReview.getContent());
    return "productReview_form";
  }
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/modify/{id}")
  public String productReivewModify(@Valid ProductReviewForm productReviewForm, BindingResult bindingResult,
                             @PathVariable("id") Long id, Principal principal) {
    if (bindingResult.hasErrors()) {
      return "productReview_form";
    }
     ProductReview productReview = this.productReviewService.getProductReview(id);
    if (!productReview.getAuthor().getUsername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
    }
    this.productReviewService.modify(productReview, productReviewForm.getContent(), productReviewForm.getRating());
    return String.format("redirect:/product/detail/%s#productReview_%s", productReview.getProduct().getId(),productReview.getId());
  }
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/delete/{id}")
  public String productReivewDelete(Principal principal, @PathVariable("id") Long id) {
     ProductReview productReview = this.productReviewService.getProductReview(id);
    if (!productReview.getAuthor().getUsername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
    }
    this.productReviewService.delete(productReview);
    return String.format("redirect:/product/detail/%s", productReview.getProduct().getId());
  }
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/vote/{id}")
  public String productReviewVote(Principal principal,@PathVariable("id")Long id){
    ProductReview productReview = this.productReviewService.getProductReview(id);
    SiteUser siteUser = this.userService.getUser(principal.getName());
    this.productReviewService.vote(productReview,siteUser);
    return String.format("redirect:/product/detail/%s#productReview_%s",productReview.getProduct().getId(),productReview.getId());
  }
}
