package com.app.randompick

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import androidx.appcompat.widget.AppCompatEditText

class InterceptEditText @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : AppCompatEditText(context, attributeSet) {

    /*var commitTextEvent: ICommitTextEvent? = null

    interface ICommitTextEvent {
        fun onCommitText(text: CharSequence?, newCursorPosition: Int): Boolean
    }

    inner class Ic(target: InputConnection?, mutable: Boolean) : InputConnectionWrapper(
        target,
        mutable
    ) {
        override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
            if (commitTextEvent?.onCommitText(text, newCursorPosition) == true) {
                super.commitText(text, newCursorPosition)
                return true
            } else {
                return false
            }
            return super.commitText(text, newCursorPosition)
        }
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        return Ic(super.onCreateInputConnection(outAttrs), false)
    }*/
}