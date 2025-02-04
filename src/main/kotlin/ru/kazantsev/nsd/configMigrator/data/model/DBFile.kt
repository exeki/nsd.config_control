package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import java.nio.charset.Charset

@Entity
class DBFile() : AbstractEntity() {

    @NotBlank
    lateinit var fileName: String
    @NotBlank
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