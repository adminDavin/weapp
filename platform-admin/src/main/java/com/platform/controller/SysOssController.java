package com.platform.controller;

import com.alibaba.fastjson.JSON;
import com.platform.annotation.SysLog;
import com.platform.common.service.FileMangeService;
import com.platform.entity.SysOssEntity;
import com.platform.oss.CloudStorageConfig;
import com.platform.oss.OSSFactory;
import com.platform.service.SysConfigService;
import com.platform.service.SysOssService;
import com.platform.utils.*;
import com.platform.validator.ValidatorUtils;
import com.platform.validator.group.AliyunGroup;
import com.platform.validator.group.QcloudGroup;
import com.platform.validator.group.QiniuGroup;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.alibaba.druid.sql.parser.Token.LOCK;

/**
 * 文件上传Controller
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-03-25 12:13:26
 */
@RestController
@RequestMapping("sys/oss")
public class SysOssController {
    @Autowired
    private SysOssService sysOssService;
    @Autowired
    private SysConfigService sysConfigService;

    private final static String KEY = Constant.CLOUD_STORAGE_CONFIG_KEY;

    /**
     * 列表
     *
     * @param params 请求参数
     * @return R
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:oss:all")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        List<SysOssEntity> sysOssList = sysOssService.queryList(query);
        int total = sysOssService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(sysOssList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }


//    /**
//     * 获取云存储配置信息
//     *
//     * @return R
//     */
//    @RequestMapping("/config")
//    @RequiresPermissions("sys:oss:all")
//    public R config() {
//        CloudStorageConfig config = sysConfigService.getConfigObject(KEY, CloudStorageConfig.class);
//
//        return R.ok().put("config", config);
//    }
//
//
//    /**
//     * 保存云存储配置信息
//     *
//     * @param config 配置信息
//     * @return R
//     */
//    @SysLog("保存云存储配置信息")
//    @RequestMapping("/saveConfig")
//    @RequiresPermissions("sys:oss:all")
//    public R saveConfig(@RequestBody CloudStorageConfig config) {
//        //校验类型
//        ValidatorUtils.validateEntity(config);
//
//        if (config.getType() == Constant.CloudService.QINIU.getValue()) {
//            //校验七牛数据
//            ValidatorUtils.validateEntity(config, QiniuGroup.class);
//        } else if (config.getType() == Constant.CloudService.ALIYUN.getValue()) {
//            //校验阿里云数据
//            ValidatorUtils.validateEntity(config, AliyunGroup.class);
//        } else if (config.getType() == Constant.CloudService.QCLOUD.getValue()) {
//            //校验腾讯云数据
//            ValidatorUtils.validateEntity(config, QcloudGroup.class);
//        }
//
//        sysConfigService.updateValueByKey(KEY, JSON.toJSONString(config));
//
//        return R.ok();
//    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return R
     * @throws Exception 异常
     */
//    @RequestMapping("/upload")
//    public R upload(@RequestParam("file") MultipartFile file) throws Exception {
//        if (file.isEmpty()) {
//            throw new RRException("上传文件不能为空");
//        }
//        //上传文件
//        String url = OSSFactory.build().upload(file);
//
//        //保存文件信息
//        SysOssEntity ossEntity = new SysOssEntity();
//        ossEntity.setUrl(url);
//        ossEntity.setCreateDate(new Date());
//        sysOssService.save(ossEntity);
//
//        R r = new R();
//        r.put("url", url);
//        r.put("link", url);
//        return r;
//    }

    @RequestMapping("/upload")
    public R upload(@RequestParam("file") MultipartFile file) throws Exception {
        System.out.println("000000");
        if (file.isEmpty()) {
            throw new RRException("上传文件不能为空");
        }
        FileMangeService fileMangeService = new FileMangeService();
        String arr[];
        System.out.println(file.getBytes());
        arr = fileMangeService.uploadFile(file.getBytes(), "-1");
        System.out.println("aaaaaa");
        SysOssEntity ossEntity = new SysOssEntity();
        ossEntity.setUrl("http://39.100.237.144:3000/platform-framework/picture.jpg?getRemoteFilename="+arr[1]);
        ossEntity.setCreateDate(new Date());
        ossEntity.setGroupName(arr[0]);
        sysOssService.save(ossEntity);
        System.out.println("cccccc");
        R r = new R();
        r.put("url", "http://39.100.237.144:3000/platform-framework/picture.jpg?getRemoteFilename="+arr[1]);
        r.put("link", arr[0]);
        System.out.println("bbbbbb");
        return r;
    }
    /**
     * 删除图片
     *
     * @param ids 主键集
     * @return R
     */
    @SysLog("删除图片")
    @RequestMapping("/delete")
    @RequiresPermissions("sys:oss:all")
    public R delete(@RequestBody Long[] ids) {
        sysOssService.deleteBatch(ids);

        return R.ok();
    }

    /**
     * 查询所有列表
     *
     * @param params 请求参数
     * @return R
     */
    @RequestMapping("/queryAll")
    public List<String> queryAll(@RequestParam Map<String, Object> params) {
        //查询列表数据
        List<SysOssEntity> sysOssList = sysOssService.queryList(params);

        List<String> list = new ArrayList<>();
        if (null != sysOssList && sysOssList.size() > 0) {
            for (SysOssEntity item : sysOssList) {
                list.add(item.getUrl());
            }
        }
        return list;
    }


}
