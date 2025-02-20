package ru.kazantsev.nsd.configMigrator.exception

class NoSuitableAccessKeyException(override val message: String) : RuntimeException(message)