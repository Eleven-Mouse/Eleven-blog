package blog.service;

/**
 * 邮件通知服务接口
 */
public interface MailService {

    /**
     * 发送评论回复通知邮件
     *
     * @param toEmail      被回复者的邮箱
     * @param toNickname   被回复者的昵称
     * @param fromNickname 回复者昵称
     * @param replyContent 回复内容
     * @param articleTitle 文章标题
     */
    void sendReplyNotification(String toEmail, String toNickname,
                               String fromNickname, String replyContent,
                               String articleTitle);

    /**
     * 通知博主有新评论
     *
     * @param visitorNickname 评论者昵称
     * @param commentContent  评论内容
     * @param articleTitle    文章标题
     */
    void sendNewCommentNotification(String visitorNickname, String commentContent,
                                    String articleTitle);
}
