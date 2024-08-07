package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hankcs.hanlp.corpus.io.ByteArrayFileStream;
import com.hankcs.hanlp.corpus.io.ByteArrayStream;
import com.heima.apis.article.IArticleClient;
import com.heima.common.tess4j.Tess4jClient;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.article.ArticleDto;
import com.heima.model.dto.wemedia.WmNewsDto;
import com.heima.model.pojo.wemedia.WmChannel;
import com.heima.model.pojo.wemedia.WmNews;
import com.heima.model.pojo.wemedia.WmSensitive;
import com.heima.model.pojo.wemedia.WmUser;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {

    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Autowired
    private IArticleClient articleClient;

    @Autowired
    private WmChannelMapper wmChannelMapper;

    @Autowired
    private WmUserMapper wmUserMapper;

    @Autowired
    private WmSensitiveMapper wmSensitiveMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private Tess4jClient tess4jClient;

    @Override
    @Async //表明当前方法为异步方法
    public void autoScanWmNews(Integer id) {

        WmNews wmNews = wmNewsMapper.selectById(id);
        if (wmNews == null) {
            throw new RuntimeException("WmNewsAutoScanServiceImpl -文章不存在");
        }

        if (wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {
            //从内容中提取纯文本和图片
            Map<String,Object> textAndImages = handleTextAndImages(wmNews);

            //自管理敏感词过滤 审核通过为true
            boolean isSensitive = handleSensitiveScan((String)textAndImages.get("content"),wmNews);
            if(!isSensitive) return;

            //审核文本
            boolean isTextScan = handleTextScan((String)textAndImages.get("content"),wmNews);
            if(!isTextScan) return;
            //审核图片

            boolean isImagesScan = handleImagesScan((List<String>) textAndImages.get("images"),wmNews);
            if(!isImagesScan) return;



            //审核成功，保存app端的相关文章数据
            ResponseResult responseResult = saveArticle(wmNews);
            if (!responseResult.getCode().equals(200)) {
                throw new RuntimeException("WmNewsAutoScanServiceImpl 文章审核，保存app端相关数据失败");
            }

            //回填id

            wmNews.setArticleId((Long) responseResult.getData());
            updateWmNews(wmNews,(short) 9,"审核成功");

        }


    }

    private boolean handleSensitiveScan(String content, WmNews wmNews) {

        boolean flag = true;

        List<WmSensitive> wmSensitives = wmSensitiveMapper.selectList(Wrappers.<WmSensitive>lambdaQuery().select(WmSensitive::getSensitives));
        List<String> stringList = wmSensitives.stream().map(WmSensitive::getSensitives).collect(Collectors.toList());

        //初始化敏感词库
        SensitiveWordUtil.initMap(stringList);

        //查询文章是否包含敏感词
        Map<String, Integer> map = SensitiveWordUtil.matchWords(content);
        if (!map.isEmpty()) {
            updateWmNews(wmNews,(short) 2,"文章包含敏感词" + map);
            flag = false;
        }

        return flag;

    }

    /**
     * 保存文章
     * @param wmNews
     * @return
     */
    private ResponseResult saveArticle(WmNews wmNews) {
        ArticleDto articleDto = new ArticleDto();
        BeanUtils.copyProperties(wmNews,articleDto);
        articleDto.setLayout(wmNews.getType());

        //频道
        WmChannel wmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
        if (wmChannel != null) {
            articleDto.setChannelName(wmChannel.getName());
        }

        //作者
        articleDto.setAuthorId(wmNews.getUserId().longValue());
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if (wmUser != null) {
            articleDto.setAuthorName(wmUser.getName());
        }

        //设置文章Id
        if (wmNews.getArticleId() != null) {
            articleDto.setId(wmNews.getArticleId());
        }
        articleDto.setCreatedTime(new Date());

        ResponseResult responseResult = articleClient.saveArticle(articleDto);
        return responseResult;
    }

    private boolean handleImagesScan(List<String> images, WmNews wmNews) {

        boolean flag = true;
        //图片去重
        images = images.stream().distinct().collect(Collectors.toList());

        //从minio中下载图片
        List<byte[]> imageList = new ArrayList<>();



            try {
                for (String image : images) {
                    byte[] bytes = fileStorageService.downLoadFile(image);

                    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                    BufferedImage bufferedImage = ImageIO.read(in);
                    //图片识别
                    String result = tess4jClient.doOCR(bufferedImage);

                    //过滤敏感词
                    boolean isSensitive = handleSensitiveScan(result, wmNews);
                    if(!isSensitive) return false;
                    imageList.add(bytes);

                }
            } catch (IOException | TesseractException e) {
                throw new RuntimeException(e);
            }

        if (wmNews.getImages().isEmpty()) {
            return flag;
        }

        //TODO 文章图片审核,这里直接表示审核通过

        return flag;


    }

    private boolean handleTextScan(String content, WmNews wmNews) {
        boolean flag = true;
        if ((wmNews.getTitle() + content).isEmpty()) {
            return flag;
        }

        //TODO 文章文本审核,这里直接表示审核通过

        return flag;
    }

    /**
     * 修改文章内容
     * @param wmNews
     * @param status
     * @param reason
     */
    private void updateWmNews(WmNews wmNews, short status, String reason) {
        wmNews.setStatus(status);
        wmNews.setReason(reason);
        wmNewsMapper.updateById(wmNews);
    }

    /**
     * 1.从自媒体文章内容中提取文本和图片
     * 2.提取文章封面的图片
     * @param wmNews
     * @return
     */
    private Map<String, Object> handleTextAndImages(WmNews wmNews) {

        //存储纯文本内容
        StringBuilder stringBuilder = new StringBuilder();
        //存储图片url
        List<String> images = new ArrayList<>();

        if (StringUtils.isNoneBlank(wmNews.getContent())) {
            List<Map> maps = JSONArray.parseArray(wmNews.getContent(), Map.class);
            for (Map map : maps) {
                if (map.get("type").equals("text")) {
                    stringBuilder.append(map.get("value"));
                }
                if (map.get("type").equals("image")) {
                    images.add((String) map.get("value"));
                }

            }
        }

        //提取文章封面图片
        if (StringUtils.isNoneBlank(wmNews.getImages())) {
            String[] split = wmNews.getImages().split(",");
            images.addAll(Arrays.asList(split));

        }

        Map<String, Object> result = new HashMap<>();
        result.put("content",stringBuilder.toString());
        result.put("images",images);

        return result;

    }
}
