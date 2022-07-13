package pub.zgq.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.buf.CharChunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author 孑然
 */
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    /**
     * 替换符号
     */
    private static final String REPLACEMENT = "***";

    /**
     * 初始化根节点
     */
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init() {
        try(
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加到前缀树
                addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败: " + e.getMessage());
        }
    }

    /**
     * 添加 一个敏感词 到 前缀树中
     *
     * 依次将每个字符挂入树中
     * @param keyword
     */
    private void addKeyword(String keyword) {

        TrieNode tempNode = rootNode;

        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            // 先判断当前字符是否已存在子节点中
            TrieNode subNode = tempNode.getSubNode(c);

            if (subNode == null) {
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            // 指向子节点，进入下一轮循环
            tempNode = subNode;

            // 设置结束的标识
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词方法
     * @param text
     * @return
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        // 指针1
        TrieNode tempNode = rootNode;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        // 存放结果
        StringBuilder sb = new StringBuilder();

        while (begin < text.length()) {
            if (position < text.length()) {
                char c = text.charAt(position);

                // 当前字符为 特殊符号
                if (isSymbol(c)) {
                    // 若指针1处于根节点，将此符号计入结果，让指针向下走一步(跳过该字符)
                    if (tempNode == rootNode) {
                        sb.append(c);
                        begin++;
                    }
                    position++;
                    continue;
                }
                // 检查前缀树中 的下级节点 是否包含字符c
                tempNode = tempNode.getSubNode(c);
                if (tempNode == null) {
                    // 以begin开头的字符串不是敏感词
                    sb.append(text.charAt(begin));
                    // 进入下一个位置
                    position = ++begin;
                    // 重新指向根节点
                    tempNode = rootNode;
                } else if (tempNode.isKeywordEnd()) {
                    // 发现了敏感词(也就是当前节点是叶子结点)
                    // 将begin~position范围的字符串替换掉
                    sb.append(REPLACEMENT);
                    // 进入下一个位置(3指针往后移动1位，2指针跟随移到相同位置)
                    begin = ++position;
                    // 重新指向根节点
                    tempNode = rootNode;
                } else {
                    // 检查下一个字符
                    position++;
                }
            }
            // position遍历越界仍未匹配到敏感词
            else {
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = rootNode;
            }
        }

        return sb.toString();
    }

    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    /**
     * 前缀树节点
     */
    private class TrieNode {

        // 关键词结束标记
        private boolean isKeywordEnd = false;

        // 子节点(key是下级字符, value是下级节点)
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 添加子节点的方法
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }

}
