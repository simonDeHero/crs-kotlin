package com.deliveryhero.services.crs.error

import java.lang.RuntimeException

class AuthenticationException(message: String?) : RuntimeException(message)