# SCP Reader 简体中文汉化版

本汉化基于 [ThePixelPro366/scp-reader](https://github.com/ThePixelPro366/scp-reader) 修改，保留原版的布局、功能及 SCP 内容署名。

- 纯简体中文 UI；不添加语言切换功能。
- 初次安装默认选择 **SCP-CN** 作为文档来源，仍可在设置中选择其他地区分部的原文。
- 程序独立安装包名 `com.foundation.scpreader.zh`，可以和官方版本共存。
- 内置更新源指向本 Fork 的 Releases。
- 使用 SCP 基金会常用术语，例如「异常项目」「项目等级」「收容等级」「基金会档案」「相关组织」。
- 软件许可仍为 **GPL-3.0**；SCP 内容依原始 CC BY-SA 3.0 授权。
- 汉化属于非官方修改版，与原作者无隶属关系。

## 测试 APK

请在 [GitHub Actions](https://github.com/if-u-can/scp-reader/actions/workflows/zh-cn-build.yml) 中下载名为 `scp-reader-zh-cn-apk` 的构建产物。Fork 首次运行工作流可能需要在 Actions 页面手动启用。

测试 APK 使用 Actions Runner 的调试签名，不保证跨次构建直接覆盖更新。正式发布前需配置**固定签名密钥**（仅存储为 GitHub Secrets，不提交到源码），并使用发行工作流发布。
