package org.example.testang

import jakarta.validation.constraints.Pattern
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.random.Random

data class AddStudentRequest(
    val name: String,
    val password: String
)

data class GetStudentRequest(
    val name: String,
)

@RestController
@Validated
@SpringBootApplication
class TestangApplication(
    // Automatically detects and provides instance of repository
    private val studentRepository: StudentRepository,

    // Similarly for passwordEncoder
    private val passwordEncoder: PasswordEncoder,
) {

    @RequestMapping("/")
    fun home(): String {
        return "hello, world!"
    }

    // Perform input validation to prevent any XSS
    @RequestMapping("/greet/{name}")
    fun greetUser(@PathVariable @Pattern(regexp = "[a-zA-Z0-9\\s]+") name: String): String {
        return "hello, $name! hope you have a good day."
    }

    @RequestMapping("/randomDiceRoll")
    fun randomDiceRoll(): String {
        return "${Random.nextInt(1, 6)} was rolled!"
    }

    @PostMapping("/addUser")
    fun addUser(@RequestBody request: AddStudentRequest): String {

        // Fully initialized, contains ID
        val savedStudent = studentRepository.save(
            Student(
                name = request.name,
                password = passwordEncoder.encode(request.password)
            )
        )

        return "Created Student ${savedStudent.name} with ID ${savedStudent.id}"
    }

    @RequestMapping("/getUser")
    fun getUser(@RequestBody request: GetStudentRequest): String {

        // What happens when the user doesn't exist?
        val savedStudent = studentRepository.findByName(request.name)
        return if (savedStudent == null) {
            "Student not found."
        } else {
            "ID: ${savedStudent.id}, Name: ${savedStudent.name}"
        }
    }

    @RequestMapping("/tryAuthentication")
    fun tryAuthentication(): String {
        return "Welcome ${SecurityContextHolder.getContext().authentication.name}!"
    }

}

fun main(args: Array<String>) {
    runApplication<TestangApplication>(*args)
}
