package ru.kazantsev.nsd.configMigrator.ui.components

import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.function.ValueProvider
import java.time.LocalDateTime

@Deprecated("Не знаю как сделать и лень узнавать")
class MyGrid<T> : Grid<T> {
    constructor() : super()
    constructor(beanType: Class<T>) : super(beanType)
    constructor(pageSize: Int) : super(pageSize)

    fun addDateColumn(valueProvider: ValueProvider<T, LocalDateTime>){
        return
    }
}