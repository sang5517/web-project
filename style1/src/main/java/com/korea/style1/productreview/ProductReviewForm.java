package com.korea.style1.productreview;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductReviewForm {
  @NotEmpty(message = "후기내용은 필수항목입니다.")
  private String content;

  private int rating;




}
