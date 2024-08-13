package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.constants.ArticleConstants;
import com.heima.file.service.FileStorageService;
import com.heima.model.pojo.article.ApArticle;
import com.heima.model.pojo.article.ApArticleContent;
import com.heima.model.vo.search.SearchArticleVo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {



    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleService articleService;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Override
    @Async
    public void buildArticleToMinio(ApArticle apArticle, String content) {

        if (StringUtils.isNotBlank(content)) {
            //通过freemarker生成html文件
            Template template = null;
            StringWriter stringWriter = new StringWriter();
            try {
                template = configuration.getTemplate("article.ftl");
                Map<String,Object> dataModel = new HashMap<>();
                dataModel.put("content", JSONArray.parseArray(content));
                stringWriter = new StringWriter();

                template.process(dataModel, stringWriter);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            //将html上传到minio中
            InputStream byteArrayInputStream = new ByteArrayInputStream(stringWriter.toString().getBytes());
            String path = fileStorageService.uploadHtmlFile("", apArticle.getId() + ".html", byteArrayInputStream);

            //修改ap_article表，保存url字段
            articleService.update(Wrappers.<ApArticle>lambdaUpdate().eq(ApArticle::getId,apArticle.getId()).set(ApArticle::getStaticUrl,path));

            //发送消息，创建索引
            createArticleESIndex(apArticle,content,path);

        }


    }

    /**
     * 发送消息，创建索引
     * @param apArticle
     * @param content
     * @param path
     */
    private void createArticleESIndex(ApArticle apArticle, String content, String path) {

        SearchArticleVo searchArticleVo = new SearchArticleVo();

        BeanUtils.copyProperties(apArticle,searchArticleVo);
        searchArticleVo.setContent(content);
        searchArticleVo.setStaticUrl(path);
        kafkaTemplate.send(ArticleConstants.ARTICLE_ES_SYNC_TOPIC, JSON.toJSONString(searchArticleVo));

    }
}
