package blog.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询结果类
 * 
 * 用于封装分页查询的响应数据，包含分页信息和数据列表
 * 
 * @param <T> 数据列表的元素类型
 * @author Eleven
 * @version 1.0
 */
@Data
@NoArgsConstructor

public class PageResult<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 当前页数据列表
     */
    private List<T> records;
    
    /**
     * 快捷构造方法
     * 
     * @param total 总记录数
     * @param records 当前页数据列表
     */
    public PageResult(Long total, List<T> records) {
        this.total = total;
        this.records = records;
    }
    
    /**
     * 快捷构造方法（支持int类型的total）
     * 
     * @param total 总记录数
     * @param records 当前页数据列表
     */
    public PageResult(int total, List<T> records) {
        this.total = (long) total;
        this.records = records;
    }
}
