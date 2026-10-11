package com.foundation.scpreader.data

/**
 * An SCP Foundation regional/language division ("branch"). Each branch is a separate Wikidot wiki
 * with its own canon - its own translations of international SCPs plus original stories written
 * only there. [host] is the wiki's base URL as indexed by CROM (the `url` prefix used to filter
 * pages and to build number slugs like `<host>/scp-173`), confirmed against api.crom.avn.sh.
 *
 * The number slug (`scp-173`) is shared across every branch, so a branch's random/of-the-day feed
 * is just the same number lookups pointed at a different host, and CROM's `translations` links let
 * us map any article to its counterpart in another branch.
 */
enum class Branch(val code: String, val displayName: String, val host: String) {
    EN("en", "英语（主站）", "http://scp-wiki.wikidot.com"),
    RU("ru", "俄语 · SCP-RU", "http://scp-ru.wikidot.com"),
    KO("ko", "韩语 · SCP-KO", "http://scpko.wikidot.com"),
    CN("cn", "简体中文 · SCP-CN", "http://scp-wiki-cn.wikidot.com"),
    FR("fr", "法语 · SCP-FR", "http://fondationscp.wikidot.com"),
    PL("pl", "波兰语 · SCP-PL", "http://scp-pl.wikidot.com"),
    ES("es", "西班牙语 · SCP-ES", "http://lafundacionscp.wikidot.com"),
    TH("th", "泰语 · SCP-TH", "http://scp-th.wikidot.com"),
    JP("jp", "日语 · SCP-JP", "http://scp-jp.wikidot.com"),
    DE("de", "德语 · SCP-DE", "http://scp-wiki-de.wikidot.com"),
    IT("it", "意大利语 · SCP-IT", "http://fondazionescp.wikidot.com"),
    UA("ua", "乌克兰语 · SCP-UA", "http://scp-ukrainian.wikidot.com"),
    PTBR("pt-br", "葡萄牙语 · SCP-PT/BR", "http://scp-pt-br.wikidot.com"),
    CS("cs", "捷克语/斯洛伐克语 · SCP-CS", "http://scp-cs.wikidot.com"),
    ZHTR("zh-tr", "繁体中文 · SCP-ZH-TR", "http://scp-zh-tr.wikidot.com"),
    VN("vn", "越南语 · SCP-VN", "http://scp-vn.wikidot.com"),
    EL("el", "希腊语 · SCP-EL", "http://scp-el.wikidot.com"),
    ID("id", "印尼语 · SCP-ID", "http://scp-id.wikidot.com"),
    INT("int", "国际分部 · SCP-INT", "http://scp-int.wikidot.com");

    /** Host without the URL scheme, for matching against a page URL regardless of http/https. */
    val hostKey: String get() = host.substringAfter("://")

    companion object {
        fun byCode(code: String): Branch? = entries.firstOrNull { it.code == code }

        /** The branch that owns [url], matched by host; null for a host we don't model. */
        fun ofUrl(url: String): Branch? {
            val hostAndPath = url.substringAfter("://")
            return entries.firstOrNull { hostAndPath.startsWith(it.hostKey + "/") || hostAndPath == it.hostKey }
        }

        /** Resolve a set of persisted branch [codes] to branches, always non-empty (falls back to EN). */
        fun fromCodes(codes: Set<String>): List<Branch> =
            codes.mapNotNull { byCode(it) }.ifEmpty { listOf(CN) }
    }
}
