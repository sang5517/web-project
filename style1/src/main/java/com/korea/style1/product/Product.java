package com.korea.style1.product;


import com.korea.style1.productreview.ProductReview;
import com.korea.style1.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 200)
  private String name;


  @Column(columnDefinition = "Text")
  private String content;


  private LocalDateTime createDate;

  @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
  private List<ProductReview> productReviewList;

  @ManyToOne
  private SiteUser author;

  private LocalDateTime modifyDate;

  @ManyToMany
  Set<SiteUser> voter;


  // 이미지 파일의 경로
  @Column(columnDefinition = "Text")
  String imgPath;
  @Column(columnDefinition = "Text")
  private String kww;



  @Column(length = 50)
  private String category;

//  @OneToMany
//  private


}
