import {QuillDeltaToHtmlConverter} from "quill-delta-to-html";

/**
 * 将 Quill Delta 格式的内容转换为 HTML
 * @param content - JSON 字符串格式的 Quill Delta 内容
 * @returns HTML 字符串
 */
export function convertToHtml(content: string | null | undefined): string {
    // 处理空内容
    if (!content) {
        return '<p style="color: grey;">暂无内容</p>'
    }

    try {
        // 解析 JSON 内容
        const parsed = JSON.parse(content)
        const ops = parsed.ops

        // 检查 ops 数组
        if (!ops || ops.length === 0) {
            return '<p style="color: grey;">内容为空</p>'
        }

        // 转换为 HTML
        const converter = new QuillDeltaToHtmlConverter(ops, {inlineStyles: true})
        return converter.convert()
    } catch (e) {
        // 错误处理
        console.error('内容转换失败:', e, content)
        return '<p style="color: red;">内容格式错误</p>'
    }
}

/**
 * 获取纯文本内容（去除 HTML 标签）
 * @param content - JSON 字符串格式的 Quill Delta 内容
 * @returns 纯文本字符串
 */
export function deltaToText(content: string | null | undefined): string {
    if (!content) return ''

    try {
        const parsed = JSON.parse(content)
        const ops = parsed.ops

        if (!ops) return ''

        let text = ''
        for (const op of ops) {
            if (typeof op.insert === 'string') {
                text += op.insert
            }
        }
        return text
    } catch (e) {
        console.error('文本提取失败:', e)
        return ''
    }
}

/**
 * 获取内容的字符长度（用于字数统计）
 * @param content - JSON 字符串格式的 Quill Delta 内容
 * @returns 字符数
 */
export function getContentLength(content: string | null | undefined): number {
    return deltaToText(content).length
}