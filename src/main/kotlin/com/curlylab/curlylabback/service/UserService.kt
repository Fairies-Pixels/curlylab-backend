package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.model.User
import com.curlylab.curlylabback.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    userRepository: UserRepository
) : BaseServiceImpl<User>(userRepository)