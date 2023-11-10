package com.korea.style1.productreview;


import com.korea.style1.DataNotFoundException;
import com.korea.style1.product.Product;
import com.korea.style1.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductReviewService {
  private final ProductReviewRepository productReviewRepository;


  public ProductReview create(Product product, String content, int rating,SiteUser author) {
    ProductReview productReview = new ProductReview();
    productReview.setContent(content);
    productReview.setCreateDate(LocalDateTime.now());
    productReview.setProduct(product);
    productReview.setAuthor(author);
    productReview.setRating(rating);
    this.productReviewRepository.save(productReview);
    return productReview;
  }

  public ProductReview getProductReview(Long id) {
    Optional<ProductReview> productReview = this.productReviewRepository.findById(id);
    if (productReview.isPresent()) {
      return productReview.get();
    } else {
      throw new DataNotFoundException("productReview not found");
    }
  }
  public void modify(ProductReview productReview,String content,int rating){
    productReview.setContent(content);
    productReview.setModifyDate(LocalDateTime.now());
    productReview.setRating(rating);
    this.productReviewRepository.save(productReview);
  }
  public void delete(ProductReview productReview){
    this.productReviewRepository.delete(productReview);
  }
  public void vote(ProductReview productReview,SiteUser siteUser){
    productReview.getVoter().add(siteUser);
    this.productReviewRepository.save(productReview);
  }
}