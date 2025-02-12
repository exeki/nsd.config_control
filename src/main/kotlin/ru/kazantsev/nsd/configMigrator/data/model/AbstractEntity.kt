package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime


@MappedSuperclass
abstract class AbstractEntity {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "idgenerator"
    )
    @SequenceGenerator(name = "idgenerator", initialValue = 1000)
    var id: Long? = null

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var lastModifiedDate: LocalDateTime = LocalDateTime.now()

    @Version
    val version: Int = 0

    var archived: Boolean = false

    override fun hashCode(): Int {
        if (id != null) {
            return id.hashCode()
        }
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AbstractEntity) {
            return false // null or not an AbstractEntity class
        }
        if (id != null) {
            return id == other.id
        }
        return super.equals(other)
    }
}
