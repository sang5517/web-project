package com.korea.style1.product;


import com.korea.style1.DataNotFoundException;
import com.korea.style1.productreview.ProductReview;
import com.korea.style1.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class ProductService {

  private final ProductRepository productRepository;

  private Specification<Product> search(String kw) {
    return new Specification<>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<Product> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
        query.distinct(true);  // 중복을 제거
        Join<Product, SiteUser> u1 = q.join("author", JoinType.LEFT);
        Join<Product, ProductReview> a = q.join("productReviewList", JoinType.LEFT);
        Join<ProductReview, SiteUser> u2 = a.join("author", JoinType.LEFT);
        return cb.or(cb.like(q.get("name"), "%" + kw + "%"), // 제목
                cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
      }
    };
  }

  public List<Product> getList() {
    return this.productRepository.findAll();
  }

  public Product getProduct(Long id) {
    Optional<Product> product = this.productRepository.findById(id);
    if (product.isPresent()) {
      return product.get();
    } else {
      throw new DataNotFoundException("product not found");
    }
  }

  public void create(String name, String content, String file, SiteUser user, String kww, String category) {
    Product p = new Product();
    p.setName(name);
    p.setImgPath(file);
    p.setContent(content);
    p.setCreateDate(LocalDateTime.now());
    p.setAuthor(user);
    p.setKww(kww);
    p.setCategory(category);
    this.productRepository.save(p);

  }

  public Page<Product> getList(int page, String kw, String category) {
    List<Sort.Order> sorts = new ArrayList<>();
    sorts.add(Sort.Order.desc("createDate"));
    Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
    return this.productRepository.findAllByKeyword(kw, pageable, category);
//    return this.productRepository.findAllByKeyword2(pageable);
  }

  public void modify(Long id, String name, String content) {
    Optional<Product> optionalProduct = productRepository.findById(id);

    if (optionalProduct.isPresent()) {
      Product product = optionalProduct.get();
      product.setName(name);
      product.setContent(content);
      product.setModifyDate(LocalDateTime.now());

      productRepository.save(product);
    }
  }

  public void delete(Product product) {
    this.productRepository.delete(product);
  }

  public void vote(Product product, SiteUser siteUser) {
    product.getVoter().add(siteUser);
    this.productRepository.save(product);
  }

}
