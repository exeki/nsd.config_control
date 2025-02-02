package ru.kazantsev.nsd.configMigrator.ui.views.`object`

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.Route
import com.vaadin.flow.spring.annotation.UIScope
import com.vaadin.flow.spring.annotation.VaadinSessionScope
import ru.kazantsev.nsd.configMigrator.data.model.Scheduler
import ru.kazantsev.nsd.configMigrator.data.model.User
import ru.kazantsev.nsd.configMigrator.data.repo.SchedulerRepo
import ru.kazantsev.nsd.configMigrator.data.repo.UserRepo
import ru.kazantsev.nsd.configMigrator.ui.MainLayout
import ru.kazantsev.nsd.configMigrator.ui.views.error.Error400
import ru.kazantsev.nsd.configMigrator.ui.views.error.Error404

@UIScope
@VaadinSessionScope
@Route(value = "user", layout = MainLayout::class)
class UserView(
    private val userRepo: UserRepo
) : VerticalLayout(), HasUrlParameter<Long> {

    private lateinit var user: User

    override fun setParameter(event: BeforeEvent?, id: Long?) {
        if (id == null) UI.getCurrent().navigate(Error400::class.java)
        else userRepo.findById(id).ifPresentOrElse(
            { value -> renderObjectCard(value) },
            { UI.getCurrent().navigate(Error404::class.java) }
        )
    }

    private fun renderObjectCard(obj: User) {
        this.user = obj
        this.removeAll()
        //TODO
    }
}