package blog.entity;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Tags
{
    private Long id;

    private String name;

    private String articleCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
