package blog.result;

/**
 * 统一响应结果类
 * 
 * 用于封装API接口的响应数据，提供统一的返回格式
 * 包含状态码、消息和数据等字段
 * 
 * @author Eleven
 * @version 1.0
 */
public class Result<T>
{
    /**
     * 响应状态码
     * 1: 成功
     * 0: 失败
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String msg;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 无参构造函数
     */
    public Result() {}
    
    /**
     * 全参构造函数
     * 
     * @param code 状态码
     * @param msg 消息
     * @param data 数据
     */
    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    
    /**
     * 成功响应（无数据）
     * 
     * @return 成功结果
     */
    public static <T> Result<T> success() {
        return new Result<>(1, "操作成功", null);
    }
    
    /**
     * 成功响应（带数据）
     * 
     * @param data 响应数据
     * @return 成功结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(1, "操作成功", data);
    }
    
    /**
     * 成功响应（自定义消息）
     * 
     * @param msg 自定义消息
     * @return 成功结果
     */
    public static <T> Result<T> success(String msg) {
        return new Result<>(1, msg, null);
    }
    
    /**
     * 成功响应（自定义消息和数据）
     * 
     * @param msg 自定义消息
     * @param data 响应数据
     * @return 成功结果
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(1, msg, data);
    }
    
    /**
     * 失败响应（默认消息）
     * 
     * @return 失败结果
     */
    public static <T> Result<T> error() {
        return new Result<>(0, "操作失败", null);
    }
    
    /**
     * 失败响应（自定义消息）
     * 
     * @param msg 错误消息
     * @return 失败结果
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(0, msg, null);
    }
    
    /**
     * 失败响应（自定义状态码和消息）
     * 
     * @param code 状态码
     * @param msg 错误消息
     * @return 失败结果
     */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }
    
    // Getter和Setter方法
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
}
