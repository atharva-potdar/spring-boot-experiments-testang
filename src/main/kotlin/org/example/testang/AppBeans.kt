package org.example.testang

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Configuration
class ApplicationConfig {

    // Apparently, Beans are objects where major handling is done by Spring itself
    // We define it in a configuration class so Spring is aware of it

    // NOTE: Since this is the only PasswordEncoder bean Spring sees,
    //       it automatically uses this for the spring-security auth page...
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()
    }

    // Default password initialization
    @Bean
    fun init(studentRepository: StudentRepository, encoder: PasswordEncoder) = CommandLineRunner {
        if (studentRepository.findByName("admin") == null) {
            studentRepository.save(Student(
                name = "admin",
                password = encoder.encode("admin")
            ))
        }
    }

}

// We create a service for spring to find details about user
@Service
class StudentDetailsService(
    val studentRepository: StudentRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): User {
        val student = studentRepository.findByName(username)
            ?: throw UsernameNotFoundException("User $username not found")

        return User(
            student.name,
            student.password,
            emptyList() // for authorities/roles
        )
    }
}