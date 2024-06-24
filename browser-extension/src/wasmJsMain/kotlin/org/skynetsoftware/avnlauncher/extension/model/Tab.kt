package org.skynetsoftware.avnlauncher.extension.model

public external interface Tab : JsAny {
    /**
     * The last committed URL of the main frame of the tab. This property is only present if the
     * extension's manifest includes the <code>"tabs"</code> permission and may be an empty string if the
     * tab has not yet committed. See also $(ref:org.skynetsoftware.avnlauncher.extension.model.Tab.pendingUrl).
     */
    public var url: String?
}
