package com.deliveryhero.services.crs.model

import com.deliveryhero.services.crs.api.Person
import com.deliveryhero.services.crs.api.PersonController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping(PersonController.PATH)
class PersonControllerImpl: PersonController {

    override fun create(@RequestBody @Valid person: Person) = person
}