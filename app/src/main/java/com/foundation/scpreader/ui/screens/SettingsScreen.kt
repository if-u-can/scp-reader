package com.foundation.scpreader.ui.screens

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import com.foundation.scpreader.AppState
import com.foundation.scpreader.BuildConfig
import com.foundation.scpreader.DownloadPref
import com.foundation.scpreader.HeroMode
import com.foundation.scpreader.ThemeMode
import com.foundation.scpreader.ui.components.AppIcons
import com.foundation.scpreader.ui.components.Divider1
import com.foundation.scpreader.ui.components.PillSwitch
import com.foundation.scpreader.ui.components.ScpSpinner
import com.foundation.scpreader.ui.theme.LocalScpScheme
import com.foundation.scpreader.ui.theme.SeedKey
import com.foundation.scpreader.update.UpdateCheckResult
import com.foundation.scpreader.update.UpdateDownloadState
import com.foundation.scpreader.update.installApk
import kotlinx.coroutines.flow.first
import kotlin.math.roundToInt

private val modeDefs = listOf(
    Triple(ThemeMode.Light, "浅色", AppIcons.LightMode),
    Triple(ThemeMode.Auto, "自动", AppIcons.BrightnessAuto),
    Triple(ThemeMode.Dark, "深色", AppIcons.DarkMode),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(app: AppState) {
    val c = LocalScpScheme.current
    val scrollState = rememberScrollState()
    // Content-relative top of the Wikidot account section, captured on layout (see its Modifier below).
    var wikidotAnchorY by remember { mutableStateOf(0) }
    // Arriving from the update banner: jump to the Updates section at the bottom.
    androidx.compose.runtime.LaunchedEffect(app.scrollSettingsToUpdates) {
        if (app.scrollSettingsToUpdates) {
            // maxValue is 0 until the content is measured, so wait for it before scrolling.
            androidx.compose.runtime.snapshotFlow { scrollState.maxValue }
                .first { it > 0 }
            scrollState.animateScrollTo(scrollState.maxValue)
            app.scrollSettingsToUpdates = false
        }
    }
    // Arriving from the reader's "登录后评分" prompt: scroll to the Wikidot account section.
    androidx.compose.runtime.LaunchedEffect(app.scrollSettingsToWikidot) {
        if (app.scrollSettingsToWikidot) {
            androidx.compose.runtime.snapshotFlow { wikidotAnchorY }.first { it > 0 }
            scrollState.animateScrollTo(wikidotAnchorY.coerceAtMost(scrollState.maxValue))
            app.scrollSettingsToWikidot = false
        }
    }
    Column(Modifier.fillMaxWidth().verticalScroll(scrollState).padding(bottom = 108.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(start = 8.dp, end = 22.dp, top = 12.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                Modifier.clip(CircleShape).clickable { app.go(com.foundation.scpreader.Screen.Home) }.padding(8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(AppIcons.ArrowBack, "返回", Modifier.size(24.dp), tint = c.onSurface)
            }
            Text("设置", fontSize = 28.sp, color = c.onSurface)
        }

        // ---- Appearance ----
        GroupLabel("外观")
        SettingsCard {
            Text("主题", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = c.onSurface, modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 6.dp))
            Row(Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                modeDefs.forEach { (mode, label, icon) ->
                    val active = app.themeMode == mode
                    Row(
                        Modifier.weight(1f).height(44.dp).clip(RoundedCornerShape(12.dp))
                            .background(if (active) c.primary else Color.Transparent)
                            .then(if (active) Modifier else Modifier.border(1.dp, c.outline, RoundedCornerShape(12.dp)))
                            .clickable { app.themeMode = mode },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(icon, null, Modifier.size(19.dp), tint = if (active) c.onPrimary else c.onSurfaceVariant)
                        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (active) c.onPrimary else c.onSurfaceVariant, modifier = Modifier.padding(start = 7.dp))
                    }
                }
            }
            // Dynamic color only does anything on Android 12+; hide the toggle otherwise so the
            // accent picker (which does work) is always reachable.
            val supportsDynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            if (supportsDynamic) {
                Divider1()
                Row(
                    Modifier.fillMaxWidth().clickable { app.toggleDynamic() }.padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Icon(AppIcons.Palette, null, Modifier.size(24.dp), tint = c.onSurfaceVariant)
                    Column(Modifier.weight(1f)) {
                        Text("动态取色", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = c.onSurface)
                        Text("跟随壁纸提取主题色", fontSize = 13.sp, color = c.onSurfaceVariant)
                    }
                    PillSwitch(app.dynamicColor)
                }
            }
            Divider1()
            Row(
                Modifier.fillMaxWidth().clickable { app.toggleAmoled() }.padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Icon(AppIcons.DarkMode, null, Modifier.size(24.dp), tint = c.onSurfaceVariant)
                Column(Modifier.weight(1f)) {
                    Text("AMOLED 纯黑", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = c.onSurface)
                    Text("深色模式下使用纯黑背景", fontSize = 13.sp, color = c.onSurfaceVariant)
                }
                PillSwitch(app.amoled)
            }
            if (!supportsDynamic || !app.dynamicColor) {
                Divider1()
                Column(Modifier.padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 18.dp)) {
                    Text("强调色", fontSize = 13.sp, color = c.onSurfaceVariant, modifier = Modifier.padding(bottom = 12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        SeedKey.entries.forEach { sk ->
                            val active = app.seed == sk
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    Modifier.size(52.dp).clip(RoundedCornerShape(16.dp)).background(sk.swatch)
                                        .then(if (active) Modifier.border(3.dp, c.primary, RoundedCornerShape(16.dp)) else Modifier)
                                        .clickable { app.selectSeed(sk) },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (active) Icon(AppIcons.Check, null, Modifier.size(24.dp), tint = Color.White)
                                }
                                Text(sk.label, fontSize = 11.sp, color = c.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // ---- Discover ----
        GroupLabel("档案发现")
        SettingsCard {
            Text("首页重点展示", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = c.onSurface, modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 4.dp))
            Text("选择首页顶部卡片展示的内容", fontSize = 13.sp, color = c.onSurfaceVariant, modifier = Modifier.padding(start = 18.dp, end = 18.dp, bottom = 10.dp))
            val heroDefs = listOf(
                HeroMode.ContinueReading to "继续阅读",
                HeroMode.ScpOfTheDay to "今日异常项目",
                HeroMode.Trending to "热门档案",
                HeroMode.RecentlyViewed to "最近阅览",
                HeroMode.FriendRecommendation to "好友推荐",
            )
            FlowRow(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                heroDefs.forEach { (m, label) ->
                    val active = app.heroMode == m
                    Box(
                        Modifier.height(38.dp).clip(RoundedCornerShape(10.dp))
                            .background(if (active) c.secondaryContainer else Color.Transparent)
                            .then(if (active) Modifier else Modifier.border(1.dp, c.outlineVariant, RoundedCornerShape(10.dp)))
                            .clickable { app.selectHeroMode(m) }.padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (active) c.onSecondaryContainer else c.onSurfaceVariant)
                    }
                }
            }
            Divider1()
            Text("排除项目等级", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = c.onSurface, modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 4.dp))
            Text("随机推荐与随机档案中不显示所选等级", fontSize = 13.sp, color = c.onSurfaceVariant, modifier = Modifier.padding(start = 18.dp, end = 18.dp, bottom = 10.dp))
            FlowRow(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Safe", "Euclid", "Keter", "Thaumiel", "Neutralized").forEach { cls ->
                    val excluded = app.excludedClasses.contains(cls)
                    Row(
                        Modifier.height(36.dp).clip(RoundedCornerShape(10.dp))
                            .background(if (excluded) c.primary else Color.Transparent)
                            .then(if (excluded) Modifier else Modifier.border(1.dp, c.outlineVariant, RoundedCornerShape(10.dp)))
                            .clickable { app.toggleExcludedClass(cls) }.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        if (excluded) Icon(AppIcons.Close, null, Modifier.size(15.dp), tint = c.onPrimary)
                        Text(cls, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = if (excluded) c.onPrimary else c.onSurfaceVariant)
                    }
                }
            }
        }

        // ---- Content branches ----
        GroupLabel("维基分部")
        SettingsCard {
            Text("语言分部", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = c.onSurface, modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 4.dp))
            Text("随机档案与每日推荐来自所选语言分部。若当前档案存在其他分部的对应版本，阅读时会提供跳转入口。", fontSize = 13.sp, lineHeight = 18.sp, color = c.onSurfaceVariant, modifier = Modifier.padding(start = 18.dp, end = 18.dp, bottom = 10.dp))
            FlowRow(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                com.foundation.scpreader.data.Branch.entries.forEach { branch ->
                    val on = app.selectedBranches.contains(branch.code)
                    Row(
                        Modifier.height(36.dp).clip(RoundedCornerShape(10.dp))
                            .background(if (on) c.primary else Color.Transparent)
                            .then(if (on) Modifier else Modifier.border(1.dp, c.outlineVariant, RoundedCornerShape(10.dp)))
                            .clickable { app.toggleBranch(branch.code) }.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        if (on) Icon(AppIcons.Check, null, Modifier.size(15.dp), tint = c.onPrimary)
                        Text(branch.displayName, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = if (on) c.onPrimary else c.onSurfaceVariant)
                    }
                }
            }
        }

        // ---- Reading ----
        GroupLabel("阅读")
        SettingsCard {
            Row(Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 6.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Text("文字大小", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = c.onSurface)
                Text("${(app.fontScale * 100).roundToInt()}%", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = c.primary)
            }
            Row(Modifier.padding(start = 18.dp, end = 18.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(AppIcons.TextFields, null, Modifier.size(18.dp), tint = c.onSurfaceVariant)
                Slider(
                    value = app.fontScale,
                    onValueChange = { app.fontScale = (it * 20).roundToInt() / 20f },
                    valueRange = 0.85f..1.4f,
                    colors = SliderDefaults.colors(thumbColor = c.primary, activeTrackColor = c.primary),
                    modifier = Modifier.weight(1f),
                )
                Icon(AppIcons.TextFields, null, Modifier.size(26.dp), tint = c.onSurfaceVariant)
            }
            Text(
                "该项目在标准收容措施下保持休眠状态。",
                fontSize = (16 * app.fontScale).sp, lineHeight = (16 * app.fontScale * 1.62f).sp, color = c.onSurfaceVariant,
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 4.dp, bottom = 16.dp),
            )
            Divider1()
            ToggleRow(AppIcons.Image, "加载图片", "在档案正文中显示插图", app.loadImages) { app.toggleImages() }
        }

        // ---- Downloads & audio ----
        GroupLabel("下载与音频")
        SettingsCard {
            Text("下载内容", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = c.onSurface, modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 4.dp))
            Text("仅在存在朗读音频时可下载音频", fontSize = 13.sp, color = c.onSurfaceVariant, modifier = Modifier.padding(start = 18.dp, end = 18.dp, bottom = 10.dp))
            val prefs = listOf(
                DownloadPref.TextOnly to "仅文本",
                DownloadPref.TextAudio to "文本及音频",
                DownloadPref.AudioOnly to "音频",
                DownloadPref.Ask to "每次询问",
            )
            FlowRow(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                prefs.forEach { (p, label) ->
                    val active = app.downloadPref == p
                    Box(
                        Modifier.height(38.dp).clip(RoundedCornerShape(10.dp))
                            .background(if (active) c.secondaryContainer else Color.Transparent)
                            .then(if (active) Modifier else Modifier.border(1.dp, c.outlineVariant, RoundedCornerShape(10.dp)))
                            .clickable { app.selectDownloadPref(p) }.padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (active) c.onSecondaryContainer else c.onSurfaceVariant)
                    }
                }
            }
            Divider1()
            ToggleRow(AppIcons.Bookmark, "自动下载收藏", "自动将已收藏的档案保存到本地", app.autoDownloadBookmarks) { app.toggleAutoDownloadBookmarks() }
            Divider1()
            ToggleRow(AppIcons.Wifi, "仅通过 Wi-Fi 下载", null, app.wifiOnly) { app.toggleWifi() }
        }

        // ---- SponsorBlock ----
        GroupLabel("SponsorBlock")
        SettingsCard {
            Text("自动跳过片段", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = c.onSurface, modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 4.dp))
            Text("自动跳过 YouTube 朗读中由社区标注的片段", fontSize = 13.sp, color = c.onSurfaceVariant, modifier = Modifier.padding(start = 18.dp, end = 18.dp, bottom = 6.dp))
            com.foundation.scpreader.playback.SponsorCategory.ALL.forEach { cat ->
                Divider1()
                ToggleRow(AppIcons.FastForward, com.foundation.scpreader.playback.SponsorCategory.label(cat), null, app.sponsorCategories.contains(cat)) {
                    app.toggleSponsorCategory(cat)
                }
            }
        }

        // ---- Friends ----
        // ---- Wikidot account (for in-app voting) ----
        // Zero-height anchor: records this section's content-relative top so the "登录后评分"
        // prompt can scroll straight here.
        Box(Modifier.fillMaxWidth().onGloballyPositioned { wikidotAnchorY = it.positionInParent().y.toInt() })
        GroupLabel("Wikidot 账号")
        SettingsCard {
            val wikiCtx = androidx.compose.ui.platform.LocalContext.current
            if (app.wikidotLoggedIn) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Icon(AppIcons.Group, null, Modifier.size(24.dp), tint = c.primary)
                    Column(Modifier.weight(1f)) {
                        Text("已登录", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = c.onSurface)
                        Text(app.wikidotUser, fontSize = 13.sp, color = c.onSurfaceVariant)
                    }
                }
                Divider1()
                Row(
                    Modifier.fillMaxWidth().clickable { app.wikidotLogout() }.padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Icon(AppIcons.Close, null, Modifier.size(22.dp), tint = c.onSurfaceVariant)
                    Text("退出登录", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = c.onSurface)
                }
            } else {
                var user by remember { mutableStateOf("") }
                var pass by remember { mutableStateOf("") }
                Text("使用 Wikidot 账号登录后可为档案评分。应用不保存密码，仅在本机保留登录会话。", fontSize = 13.sp, color = c.onSurfaceVariant, modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 12.dp))
                SettingsInput(user, "用户名", onChange = { user = it })
                Spacer(Modifier.height(10.dp))
                SettingsInput(pass, "密码", password = true, onChange = { pass = it })
                app.wikidotLoginError?.let { err ->
                    Text(err, fontSize = 13.sp, color = Color(0xFFB3261E), modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 8.dp))
                }
                Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(12.dp)).background(c.primary)
                            .clickable(enabled = !app.wikidotLoggingIn) { app.wikidotLogin(user, pass) },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (app.wikidotLoggingIn) ScpSpinner(size = 20)
                        else Text("登录", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = c.onPrimary)
                    }
                    Box(
                        Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(12.dp)).border(1.dp, c.outline, RoundedCornerShape(12.dp))
                            .clickable { openUrl(wikiCtx, "https://www.wikidot.com/default--flow/login__CreateAccountScreen") },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("注册账号", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = c.onSurface)
                    }
                }
            }
        }

        GroupLabel("好友")
        SettingsCard {
            Text("服务器地址", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = c.onSurface, modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 2.dp))
            Text("好友和推荐功能的后端服务地址。", fontSize = 13.sp, color = c.onSurfaceVariant, modifier = Modifier.padding(start = 18.dp, end = 18.dp, bottom = 10.dp))
            Row(
                Modifier.padding(start = 16.dp, end = 16.dp, bottom = 14.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(12.dp)).background(c.surface)
                        .border(1.dp, c.outlineVariant, RoundedCornerShape(12.dp)).padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (app.serverUrl.isEmpty()) Text("https://your-domain/scp/", fontSize = 14.sp, color = c.onSurfaceVariant)
                    androidx.compose.foundation.text.BasicTextField(
                        value = app.serverUrl,
                        onValueChange = { app.updateServerUrl(it) },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = c.onSurface),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(c.primary),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Uri,
                            imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                        ),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = { app.saveServerUrl() }),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Box(
                    Modifier.height(48.dp).clip(RoundedCornerShape(12.dp)).background(c.primary).clickable { app.saveServerUrl() }.padding(horizontal = 18.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("保存", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = c.onPrimary)
                }
            }
            app.friendCode?.let { code ->
                Divider1()
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Icon(AppIcons.Group, null, Modifier.size(24.dp), tint = c.onSurfaceVariant)
                    Column(Modifier.weight(1f)) {
                        Text("你的好友代码", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = c.onSurface)
                        Text(code, fontSize = 13.sp, color = c.onSurfaceVariant)
                    }
                }
            }
        }

        // ---- Updates ----
        GroupLabel("更新")
        SettingsCard { UpdatesSection(app) }

        // ---- About ----
        val ctx = androidx.compose.ui.platform.LocalContext.current
        GroupLabel("关于")
        SettingsCard {
            LinkRow(AppIcons.Public, "SCP 基金会维基", "scp-wiki.wikidot.com") {
                openUrl(ctx, "https://scp-wiki.wikidot.com")
            }
            Divider1()
            LinkRow(AppIcons.SmartDisplay, "YouTube 音频来源", "SCP Archives · @SCParchives") {
                openUrl(ctx, "https://www.youtube.com/@SCParchives")
            }
            Divider1()
            LinkRow(AppIcons.Headphones, "播客来源", "SCP Archives · 在 Apple Podcasts 打开") {
                openUrl(ctx, "https://podcasts.apple.com/podcast/id1453436915")
            }
            Divider1()
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Icon(AppIcons.Info, null, Modifier.size(24.dp), tint = c.onSurfaceVariant)
                Column(Modifier.weight(1f)) {
                    Text("内容许可协议", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = c.onSurface)
                    Text("内容依据 CC BY-SA 3.0 许可协议使用 · scp-wiki.wikidot.com", fontSize = 13.sp, color = c.onSurfaceVariant, lineHeight = 18.sp)
                }
            }
        }
    }
}

@Composable
private fun LinkRow(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    val c = LocalScpScheme.current
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Icon(icon, null, Modifier.size(24.dp), tint = c.onSurfaceVariant)
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = c.onSurface)
            Text(subtitle, fontSize = 13.sp, color = c.onSurfaceVariant)
        }
        Icon(AppIcons.NorthEast, null, Modifier.size(20.dp), tint = c.onSurfaceVariant)
    }
}

@Composable
private fun GroupLabel(text: String) {
    val c = LocalScpScheme.current
    Text(text.uppercase(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.6.sp, color = c.primary,
        modifier = Modifier.padding(start = 22.dp, end = 22.dp, top = 18.dp, bottom = 8.dp))
}

/** A single-line boxed text input matching the Friends "服务器地址" field styling. */
@Composable
private fun SettingsInput(value: String, placeholder: String, password: Boolean = false, onChange: (String) -> Unit) {
    val c = LocalScpScheme.current
    Box(
        Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(48.dp).clip(RoundedCornerShape(12.dp))
            .background(c.surface).border(1.dp, c.outlineVariant, RoundedCornerShape(12.dp)).padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (value.isEmpty()) Text(placeholder, fontSize = 14.sp, color = c.onSurfaceVariant)
        androidx.compose.foundation.text.BasicTextField(
            value = value, onValueChange = onChange, singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = c.onSurface),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(c.primary),
            visualTransformation = if (password) androidx.compose.ui.text.input.PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = if (password) androidx.compose.ui.text.input.KeyboardType.Password else androidx.compose.ui.text.input.KeyboardType.Text,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SettingsCard(content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    val c = LocalScpScheme.current
    Column(
        Modifier.padding(horizontal = 16.dp).fillMaxWidth().clip(RoundedCornerShape(24.dp))
            .background(c.surfaceCLow).border(1.dp, c.outlineVariant, RoundedCornerShape(24.dp)),
        content = content,
    )
}

@Composable
private fun ToggleRow(icon: ImageVector, title: String, subtitle: String?, on: Boolean, onClick: () -> Unit) {
    val c = LocalScpScheme.current
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Icon(icon, null, Modifier.size(24.dp), tint = c.onSurfaceVariant)
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = c.onSurface)
            if (subtitle != null) Text(subtitle, fontSize = 13.sp, color = c.onSurfaceVariant)
        }
        PillSwitch(on)
    }
}

/**
 * Update-check/download/install controls for ThePixelPro366/scp-reader's public-repo releases.
 * No credentials of any kind are needed - the GitHub Releases API and asset download both work
 * unauthenticated on a public repo.
 */
@Composable
private fun UpdatesSection(app: AppState) {
    val c = LocalScpScheme.current
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val errorColor = Color(0xFFB3261E)

    Row(
        Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Icon(AppIcons.SystemUpdate, null, Modifier.size(24.dp), tint = c.onSurfaceVariant)
        Column(Modifier.weight(1f)) {
            Text("应用版本", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = c.onSurface)
            Text("v${BuildConfig.VERSION_NAME}", fontSize = 13.sp, color = c.onSurfaceVariant)
        }
        Box(
            Modifier.size(40.dp).clip(CircleShape).clickable(enabled = app.updateStatus != UpdateCheckResult.Checking) { app.checkForUpdates() },
            contentAlignment = Alignment.Center,
        ) {
            if (app.updateStatus == UpdateCheckResult.Checking) ScpSpinner(size = 22)
            else Icon(AppIcons.Refresh, "检查更新", Modifier.size(20.dp), tint = c.primary)
        }
    }

    Divider1()
    Row(
        Modifier.fillMaxWidth().clickable { app.openWhatsNew() }.padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Icon(AppIcons.Article, null, Modifier.size(24.dp), tint = c.onSurfaceVariant)
        Column(Modifier.weight(1f)) {
            Text("更新说明", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = c.onSurface)
            Text("查看当前版本更新日志", fontSize = 13.sp, color = c.onSurfaceVariant)
        }
        Icon(AppIcons.NorthEast, null, Modifier.size(18.dp), tint = c.onSurfaceVariant)
    }

    Column(Modifier.padding(start = 18.dp, end = 18.dp, bottom = 16.dp)) {
        when (val status = app.updateStatus) {
            UpdateCheckResult.Idle, UpdateCheckResult.Checking -> {}
            UpdateCheckResult.NoReleases -> Text("尚未发布可用版本", fontSize = 13.sp, color = c.onSurfaceVariant)
            UpdateCheckResult.UpToDate -> StatusLine(AppIcons.Check, "已是最新版本", c.onSurfaceVariant)
            is UpdateCheckResult.Error -> StatusLine(AppIcons.ErrorOutline, "检查更新失败：${status.message}", errorColor)
            is UpdateCheckResult.Available -> Column {
                Text("发现新版本 ${status.release.tagName}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = c.primary, modifier = Modifier.padding(bottom = 10.dp))
                when (val dl = app.updateDownload) {
                    UpdateDownloadState.Idle -> ActionButton("下载并安装", filled = true) { app.downloadUpdate(ctx) }
                    is UpdateDownloadState.Downloading -> Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ScpSpinner(size = 20)
                        Text("正在下载… ${dl.percent}%", fontSize = 13.sp, color = c.onSurfaceVariant)
                    }
                    is UpdateDownloadState.ReadyToInstall -> ActionButton("安装更新", filled = true) { installApk(ctx, dl.file) }
                    is UpdateDownloadState.Failed -> Column {
                        StatusLine(AppIcons.ErrorOutline, "下载失败：${dl.message}", errorColor)
                        Box(Modifier.padding(top = 8.dp)) { ActionButton("重试", filled = false) { app.downloadUpdate(ctx) } }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusLine(icon: ImageVector, text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, null, Modifier.size(16.dp), tint = color)
        Text(text, fontSize = 13.sp, color = color)
    }
}

@Composable
private fun ActionButton(label: String, filled: Boolean, enabled: Boolean = true, onClick: () -> Unit) {
    val c = LocalScpScheme.current
    val bg = if (filled) c.primary else Color.Transparent
    val fg = if (filled) c.onPrimary else c.primary
    Box(
        Modifier.height(40.dp).clip(RoundedCornerShape(12.dp))
            .background(if (enabled) bg else c.surfaceCHighest)
            .then(if (filled) Modifier else Modifier.border(1.dp, if (enabled) c.primary else c.outlineVariant, RoundedCornerShape(12.dp)))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (enabled) fg else c.onSurfaceVariant)
    }
}
