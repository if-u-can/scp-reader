package com.foundation.scpreader.data

import com.github.houbb.opencc4j.util.ZhConverterUtil

/**
 * Reading preference, independent of the wiki division selected for browsing.
 * Changes only rendered text; the original parsed article and cached download remain untouched.
 */
enum class ReaderScriptMode(val shortLabel: String, val displayLabel: String) {
    Simplified("简", "简体中文"),
    Traditional("繁", "繁體中文"),
    Original("原", "原文"),
}

object ReaderScriptConverter {
    fun text(value: String, mode: ReaderScriptMode): String = when (mode) {
        ReaderScriptMode.Original -> value
        ReaderScriptMode.Simplified -> if (value.isEmpty()) value else ZhConverterUtil.toSimple(value)
        ReaderScriptMode.Traditional -> if (value.isEmpty()) value else ZhConverterUtil.toTraditional(value)
    }

    fun blocks(source: List<ContentBlock>, mode: ReaderScriptMode): List<ContentBlock> {
        if (mode == ReaderScriptMode.Original) return source
        return source.map { block(it, mode) }
    }

    private fun spans(source: List<InlineSpan>, mode: ReaderScriptMode): List<InlineSpan> =
        source.map { span ->
            span.copy(
                // A footnote marker may contain an ID; keep it untouched. Convert its body.
                text = if (span.footnote != null) span.text else text(span.text, mode),
                footnote = span.footnote?.let { text(it, mode) },
                // span.link remains an unchanged URL; never convert link destinations.
            )
        }

    private fun block(source: ContentBlock, mode: ReaderScriptMode): ContentBlock = when (source) {
        is ContentBlock.Heading -> source.copy(
            text = text(source.text, mode), spans = spans(source.spans, mode))
        is ContentBlock.Paragraph -> source.copy(
            text = text(source.text, mode), spans = spans(source.spans, mode))
        is ContentBlock.Quote -> source.copy(
            text = text(source.text, mode), spans = spans(source.spans, mode))
        is ContentBlock.Image -> source.copy(
            caption = text(source.caption, mode)) // url/localPath are immutable source references.
        is ContentBlock.Collapsible -> source.copy(
            title = text(source.title, mode), blocks = blocks(source.blocks, mode))
        is ContentBlock.Tabs -> source.copy(
            panes = source.panes.map { pane ->
                pane.copy(label = text(pane.label, mode), blocks = blocks(pane.blocks, mode))
            })
        is ContentBlock.Table -> source.copy(
            rows = source.rows.map { row ->
                row.copy(cells = row.cells.map { cell ->
                    cell.copy(text = text(cell.text, mode), spans = spans(cell.spans, mode))
                })
            })
        is ContentBlock.Acs -> source.copy(
            containment = source.containment?.let { text(it, mode) },
            disruption = source.disruption?.let { text(it, mode) },
            risk = source.risk?.let { text(it, mode) },
            secondary = source.secondary?.let { text(it, mode) })
    }
}
