package com.deliveryhero.services.crs

import com.deliveryhero.services.crs.api.Person
import com.deliveryhero.services.crs.api.PersonController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

// TODO remove as it is not part of CRS, only for showcase of POST
@RestController
@RequestMapping(PersonController.PATH)
class PersonControllerImpl: PersonController {

    override fun create(@RequestBody @Valid person: Person) = person
}