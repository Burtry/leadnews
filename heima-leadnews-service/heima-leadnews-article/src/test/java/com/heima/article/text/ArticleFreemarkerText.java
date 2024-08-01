package com.heima.article.text;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.ArticleApplication;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.file.service.FileStorageService;
import com.heima.model.pojo.article.ApArticle;
import com.heima.model.pojo.article.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import javafx.application.Application;
import org.apache.commons.net.nntp.Article;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)

public class ArticleFreemarkerText {


    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleService articleService;

    @Test
    public void create() throws IOException, TemplateException {
        ApArticleContent articleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, "1383827787629252610L"));

        //通过freemarker生成html文件
        Template template = configuration.getTemplate("article.ftl");

        Map<String,Object> content = new HashMap<>();
        content.put("content", JSONArray.parseArray(articleContent.getContent()));
        StringWriter stringWriter = new StringWriter();

        template.process(content, stringWriter);

        //将html上传到minio中
        InputStream byteArrayInputStream = new ByteArrayInputStream(stringWriter.toString().getBytes());
        String path = fileStorageService.uploadHtmlFile("", articleContent.getArticleId() + ".html", byteArrayInputStream);

        //修改ap_article表，保存url字段
        articleService.update(Wrappers.<ApArticle>lambdaUpdate().eq(ApArticle::getId,articleContent.getArticleId()).set(ApArticle::getStaticUrl,path));
    }
}
