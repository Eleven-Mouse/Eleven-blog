package blog.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class TagsDTO implements Serializable
{
    private Long id;

    private String name;

    private String articleCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
