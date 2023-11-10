package com.korea.style1.product;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {

  @NotEmpty(message="이름은 필수항목입니다.")
  @Size(max=200)
  private String name;

  @NotEmpty(message = "내용은 필수항목입니다.")
  private String content;

  @NotEmpty(message = "가격은 필수항목입니다.")
  private String kww;

  @NotEmpty(message = "의류종류분류는 필수입니다.")
  private String category;

}
