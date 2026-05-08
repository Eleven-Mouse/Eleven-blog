package blog.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * GitHub 仓库扫描器
 * 扫描指定仓库目录下的所有 .md 文件，返回文件名和下载地址
 */
public interface GithubRepoScanner {

    /**
     * 扫描结果
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class MdFileInfo {
        private String name;
        private String title;
        private String downloadUrl;
        private String path;
        private String sha;
    }

    /**
     * 扫描 GitHub 仓库目录，发现所有 Markdown 文件
     *
     * @param owner  仓库所有者
     * @param repo   仓库名
     * @param branch 分支名
     * @param path   目录路径（如 "docs" 或 "" 表示根目录）
     * @return Markdown 文件列表
     */
    List<MdFileInfo> scanMarkdownFiles(String owner, String repo, String branch, String path);
}
