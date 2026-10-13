# SCP Reader · 简体中文汉化版

**非官方汉化项目** · 基于 [ThePixelPro366/scp-reader](https://github.com/ThePixelPro366/scp-reader) 修改。

> **控制，收容，保护。** 本项目为社区自行维护的阅读器汉化版本，既非 SCP 基金会官方应用，也不代表原项目作者立场。

## 获取 APK

- [公开发行页面（Releases）](https://github.com/if-u-can/scp-reader/releases)：面向普通用户的安装包。
- [zh-CN 分支](https://github.com/if-u-can/scp-reader/tree/zh-CN)：汉化源代码。
- [自动构建](https://github.com/if-u-can/scp-reader/actions/workflows/zh-cn-build.yml)：开发测试用 APK。

**当前正式版为 `v1.4.1-zh.4`**，已使用固定签名，后续使用相同签名发布的正式版可直接覆盖升级。旧版 `v1.4.1-zh.1` 预览版使用临时签名，转入正式版前请先备份数据并重新安装。

## 汉化内容

阅读器支持「简体 / 繁體 / 原文」切换，首次使用默认简体，可在阅读页顶栏通过「简」按钮选择。仅调整屏幕显示文字，原文、链接、离线档案与分部来源不变。繁简字词存在少量多义情况，遇到特殊专有名词可切回「原文」核对。

繁简转换使用 [OpenCC4J](https://github.com/houbb/opencc4j)，按 Apache-2.0 许可使用。



- 保留 Material 3 设计，界面固定使用简体中文，无 UI 语言切换。
- 基于 SCP 基金会用语汉化阅读器、收藏、下载、播放器、设置及通知。
- 文档分部保留原版多选功能；只选 SCP-CN 时，普通浏览、搜索、故事、相关组织与系列从中文分部读取。
- 搜索页的评分总榜和热门推荐：以英语主站评分排名，在已选分部中展示有对应译文的档案；不存在的译文不凭空编造，也不会自动混入未选择的分部。
- 保留原版离线阅读、音频、字体与主题、批量下载等功能。

## 固定签名与后续更新

从 `v1.4.1-zh.2` 起已启用固定签名，后续同签名版本可以覆盖升级。`v1.4.1-zh.1` 属于**临时签名预览版**，与正式版签名不兼容，升级前需备份数据并重新安装。

签名配置、长期备份和发布工作流参见 [SIGNING.md](SIGNING.md)。上游版本发现后会创建 GitHub Issue，待核对翻译及变更后再发布新版。

## 安装说明

从 Releases 下载 `*.apk` 并在 Android 手机上安装。Android 最低版本为 7.0（API 24）。

开发构建包名为 `com.foundation.scpreader.zh.debug`，发行构建包名为 `com.foundation.scpreader.zh`，两者可以并存。**不要把测试版的临时签名视作正式升级签名。**

## 声明与署名

- 原应用：[ThePixelPro366/scp-reader](https://github.com/ThePixelPro366/scp-reader)。感谢原作者的开发工作。
- 汉化源代码采用与原项目一致的 [GPL-3.0](LICENSE) 授权；公开修改源码见本分支。
- SCP 维基文章及适用图片依据 [CC BY-SA 3.0](https://creativecommons.org/licenses/by-sa/3.0/) 授权，并保留页面源站入口。文章作者及具体署名详见对应 SCP 维基页面。
- SCP 徽标来自 [Wikimedia Commons](https://commons.wikimedia.org/)，遵循适用的 CC BY-SA 3.0 要求。
- 朗读音频来源于 [SCP Archives](https://www.youtube.com/@SCParchives) 的 YouTube 或播客内容，应用会标注来源；这些音频不因本项目开源而自动获得重新分发授权。

本项目为独立社区汉化版本，不隶属于原应用作者或 SCP 基金会。
