package ru.kazantsev.nsd.configMigrator.services

import org.apache.tomcat.util.http.fileupload.InvalidFileNameException
import org.springframework.stereotype.Service

@Service
@Deprecated(message = "Оно мб и не нужно")
class DBFileService {
    val FORBIDDEN_CHARS = listOf(
        "/", "\\", ":", "*", "?", "\"", "<", ">", "|", "\u0000"
    )

    fun checkFileName(str : String) {
        for (char in FORBIDDEN_CHARS) {
            if (str.contains(char)) {
                throw InvalidFileNameException(str, "Имя файла содержит запрещенный символ: '$char'")
            }
        }
    }
}