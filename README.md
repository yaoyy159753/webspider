# webspider

#### 介绍

一个简单的java爬虫

#### 软件架构

软件架构说明

#### 安装教程

1.无

#### 简单使用

```java
Spider spider=Spider.builder()
        .start();
        spider.addSimpleTask("https://www.baidu.com/");
        Thread.sleep(5000);
        spider.shutdown();
```
#### 多层深入的爬虫实例
参考test目录下的百度新闻获取热点新闻的内容
1、入口地址http://news.baidu.com/，指定解析器为newParser
2、由newParser进行解析根据class名称获取热点新闻列表，提取url加入下一轮爬取，指定解析器为hotNewsParser
3、hotNewsParser解析完成网页内容，指定下一步为NewsPipeline

#### 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request

#### 特技

1. 使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2. Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3. 你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4. [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5. Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6. Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
