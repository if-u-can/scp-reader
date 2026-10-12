package com.foundation.scpreader.playback

import kotlinx.serialization.Serializable

/** A time range to auto-skip during playback (SponsorBlock), in milliseconds. */
@Serializable
data class SkipSegment(val category: String, val startMs: Long, val endMs: Long)

/** Which concrete source the currently-playing audio came from (for the debug badge). */
enum class PlaybackOrigin { LOCAL, YOUTUBE, PODCAST }

/** SponsorBlock segment categories and their user-facing labels/defaults. */
object SponsorCategory {
    const val SPONSOR = "sponsor"
    const val SELF_PROMO = "selfpromo"
    const val INTERACTION = "interaction"
    const val INTRO = "intro"
    const val OUTRO = "outro"
    const val PREVIEW = "preview"
    const val MUSIC_OFFTOPIC = "music_offtopic"
    const val FILLER = "filler"

    /** All categories, in display order. */
    val ALL = listOf(SPONSOR, SELF_PROMO, INTERACTION, INTRO, OUTRO, PREVIEW, MUSIC_OFFTOPIC, FILLER)

    /** Skipped by default. */
    val DEFAULT_ENABLED = ALL.toSet()

    fun label(category: String): String = when (category) {
        SPONSOR -> "赞助广告"
        SELF_PROMO -> "自我推广"
        INTERACTION -> "互动提醒"
        INTRO -> "片头或中场"
        OUTRO -> "片尾和结束画面"
        PREVIEW -> "预告与回顾"
        MUSIC_OFFTOPIC -> "非朗读片段"
        FILLER -> "闲聊与无关内容"
        else -> category
    }
}
