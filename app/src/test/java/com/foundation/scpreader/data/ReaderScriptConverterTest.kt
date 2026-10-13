package com.foundation.scpreader.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Test

class ReaderScriptConverterTest {
    @Test fun simplifiesTraditionalProseWithoutOverwritingOriginal() {
        val source = listOf(ContentBlock.Paragraph("特殊收容措施：該項目具有異常現象。"))
        val converted = ReaderScriptConverter.blocks(source, ReaderScriptMode.Simplified)
        assertEquals("特殊收容措施：该项目具有异常现象。", (converted.first() as ContentBlock.Paragraph).text)
        assertEquals("特殊收容措施：該項目具有異常現象。", (source.first() as ContentBlock.Paragraph).text)
        assertSame(source, ReaderScriptConverter.blocks(source, ReaderScriptMode.Original))
    }

    @Test fun keepsLinksAndFootnoteMarkersWhileConvertingVisibleText() {
        val source = listOf(ContentBlock.Paragraph(
            text = "來自基金會",
            spans = listOf(
                InlineSpan("來自基金會", link = "http://scp-wiki-cn.wikidot.com/scp-173"),
                InlineSpan("1", footnote = "觀察記錄"),
            ),
        ))
        val result = (ReaderScriptConverter.blocks(source, ReaderScriptMode.Simplified).first() as ContentBlock.Paragraph)
        assertEquals("来自基金会", result.text)
        assertEquals("来自基金会", result.spans.first().text)
        assertEquals("http://scp-wiki-cn.wikidot.com/scp-173", result.spans.first().link)
        assertEquals("1", result.spans[1].text)
        assertEquals("观察记录", result.spans[1].footnote)
    }

    @Test fun convertsNestedContentAndSupportsTraditional() {
        val nested = ContentBlock.Collapsible(
            "展開檔案",
            listOf(ContentBlock.Table(listOf(TableRow(listOf(TableCell("記憶刪除")))))),
        )
        val simplified = ReaderScriptConverter.blocks(listOf(nested), ReaderScriptMode.Simplified)
        assertEquals("展开档案", (simplified.first() as ContentBlock.Collapsible).title)
        assertEquals(
            "记忆删除",
            (((simplified.first() as ContentBlock.Collapsible).blocks.first() as ContentBlock.Table)
                .rows.first().cells.first().text),
        )
        assertEquals("記憶刪除", ReaderScriptConverter.text("记忆删除", ReaderScriptMode.Traditional))
    }
}
