package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.*
import java.nio.charset.Charset

@Entity
class DBFile() : AbstractEntity() {

    lateinit var fileName: String
    lateinit  var fileType: String
    @Lob
    @Basic(fetch = FetchType.LAZY)
    lateinit var content: ByteArray

    constructor(fileName : String, fileType : String, content : ByteArray) : this() {
        this.fileName = fileName
        this.fileType = fileType
        this.content = content
    }

    fun getContentAsString(charset: Charset = Charset.defaultCharset()): String {
        return String(content, charset)
    }
}