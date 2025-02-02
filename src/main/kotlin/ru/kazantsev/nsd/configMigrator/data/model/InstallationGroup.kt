package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.validation.constraints.NotBlank

@Entity
class InstallationGroup() : AbstractEntity() {

    @NotBlank
    @Column(unique = true)
    var title: String = ""

    /*
    @Pattern(
        regexp = "^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$",
        message = "Color must be a valid HEX code (e.g., #FFFFFF or #FFF)"
    )
     */
    var color: String? = null

    constructor(title: String, color: String? = null) : this() {
        this.color = color
        this.title = title
    }
}