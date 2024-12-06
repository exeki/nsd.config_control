package ru.kazantsev.sportiksmonitor.data.model

import jakarta.persistence.*

@MappedSuperclass
abstract class AbstractEntity {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "idgenerator"
    ) // The initial value is to account for data.sql demo data ids
    @SequenceGenerator(name = "idgenerator", initialValue = 1000)
    open var id: Long? = null

    @Version
    open val version: Int = 0

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
