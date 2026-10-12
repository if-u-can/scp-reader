# SCP Reader 中文版：固定 APK 签名与长期更新

> **不要把私钥文件、私钥 Base64 或密码提交到公开仓库。**

## 状态

- `v1.4.1-zh.1` 是已经公开的**临时签名预览版**。这份 APK 的签名不是长期固定签名。
- 从 `v1.4.1-zh.2` 起，计划使用**一把固定的 RSA 3072 位 PKCS12 私钥**。这把私钥需要由维护者**离线保管并备份**。
- 首次正式版签名尚需配置仓库 Actions Secrets。没有 Secrets，正式发布会**直接失败**，绝对不会使用临时 debug 签名代替。

## 一次性的配置

维护者已经另行收到私密的签名备份压缩包，内含：

- `scp-reader-zh-release.p12`：**不可公开的长期签名私钥**；
- `github-secrets.env`：签名私钥的 Base64 及密码；
- `setup-github-signing.ps1`：Windows 本地批量设置 GitHub Secrets 的脚本；
- `README_PRIVATE_请妥善保存.txt`：具体操作说明和证书 SHA-256 指纹。

Windows 解压后，以 PowerShell 运行 `setup-github-signing.ps1`。需要 GitHub CLI：

```powershell
winget install --id GitHub.cli --exact
```

之后关闭并重新打开 PowerShell，在解压文件夹运行：

```powershell
powershell -ExecutionPolicy Bypass -File .\setup-github-signing.ps1
```

如需登录 GitHub，浏览器授权账号 `if-u-can`。脚本会向该仓库的 GitHub Actions **Secrets** 安全上传四项：`KEYSTORE_BASE64`、`KEYSTORE_PASSWORD`、`KEY_ALIAS`、`KEY_PASSWORD`。**脚本不会把密钥上传到 GitHub 源码。**

也可以自行进入 [仓库 Actions Secrets](https://github.com/if-u-can/scp-reader/settings/secrets/actions) 手动设置这四项。

## 发布与版本管理

进入 [Publish signed Chinese release](https://github.com/if-u-can/scp-reader/actions/workflows/release.yml) 点击 **Run workflow**，分支选 `zh-CN`，版本号如 `1.4.1-zh.2`。系统自动执行：

1. 检查版本号且拒绝覆盖已有 tag。
2. 验证四项固定签名 Secrets，并在缺少时拒绝发布。
3. 使用同一私钥构建独立包名 `com.foundation.scpreader.zh` 的正式 APK。
4. 检验 APK 签名并生成 SHA-256 文件。
5. 在 GitHub Releases 公开发布非预览版本。
6. App 内检查更新会指向此 Fork 的最新正式 Release。

后续版本**务必继续使用同一份 `.p12` 和密码**，否则 Android 无法覆盖升级。不要重新生成新的签名密钥。

## 已经安装了旧预览版怎么办？

旧公开预览版与稳定版的签名不同，**不能直接覆盖安装**，请先备份数据，然后卸载旧预览版，再安装首次稳定版。后续所有稳定版只要使用同一签名密钥，正常情况下均可覆盖升级。

注意先前 Actions 的 Debug APK 使用 `com.foundation.scpreader.zh.debug` 包名，与正式版 `com.foundation.scpreader.zh` 可以共存，但数据互不共享。

## 上游同步

本仓库每日检查 [ThePixelPro366/scp-reader](https://github.com/ThePixelPro366/scp-reader) 的最新 Release，并在发现新版本时创建 GitHub Issue 提醒维护。**不会直接把原作者的代码自动合并到汉化分支**，避免覆盖汉化及版本调整；需核对、合并代码、完成汉化并编译测试，再以新版本号发布。

## 许可

本项目软件代码遵循 [GPL-3.0](LICENSE)；维基文本和相应资源的许可仍分别按其原始授权执行。
