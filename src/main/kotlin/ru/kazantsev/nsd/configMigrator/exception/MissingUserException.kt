package ru.kazantsev.nsd.configMigrator.exception

class MissingUserException(override val message: String) : RuntimeException(message) {
}