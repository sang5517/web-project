package com.korea.style1.product;

import com.korea.style1.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
  Product findByContent(String content);
  Product findByContentAndName(String content, String name);
  List<Product> findByContentLike(String content);
  Page<Product> findAll(Pageable pageable);
  Page<Product> findAll(Specification<Product> spec,Pageable pageable);
  @Query("select "
          + "distinct p "
          + "from Product p "
          + "left outer join SiteUser u1 on p.author=u1 "
          + "left outer join ProductReview pr on pr.product=p "
          + "left outer join SiteUser u2 on pr.author=u2 "
          + "where "
          + " (:category is null or p.category = :category) "
          + "   and (p.name like %:kw% "
          + "   or p.content like %:kw% "
          + "   or u1.username like %:kw% "
          + "   or pr.content like %:kw% "
          + "   or u2.username like %:kw%) ")
  Page<Product> findAllByKeyword(@Param("kw") String kw, Pageable pageable, @Param("category") String category);

//  @Query("select distinct p from Product p where p.name like %:kw%")
//  Page<Product> findAllByKeyword2(@Param("kw") String kw, Pageable pageable);

}
