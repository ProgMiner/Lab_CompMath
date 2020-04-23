package ru.byprogminer.compmath.lab4.parser

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.misc.ParseCancellationException

class ThrowingErrorListener: BaseErrorListener() {

    @Throws(ParseCancellationException::class)
    override fun syntaxError(
            recognizer: Recognizer<*, *>?,
            offendingSymbol: Any,
            line: Int,
            charPositionInLine: Int,
            msg: String,
            e: RecognitionException
    ) {
        throw ParseCancellationException("$line:$charPositionInLine - $msg")
    }
}
