package my.example.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
public class Comment {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Size(max = 40)
    private String content;

}
