package org.example.testang

import jakarta.validation.constraints.Pattern
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.random.Random

data class StudentAuthRequest(
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

    @GetMapping("/")
    fun home(): String {
        return "hello, world!"
    }

    // Perform input validation to prevent any XSS
    @GetMapping("/greet/{name}")
    fun greetUser(@PathVariable @Pattern(regexp = "[a-zA-Z0-9\\s]+") name: String): String {
        return "hello, $name! hope you have a good day."
    }

    @GetMapping("/randomDiceRoll")
    fun randomDiceRoll(): String {

        val diceRoll = Random.nextInt(1, 6)

        return """
            <p>${diceRoll} was rolled!</p>
            <img src="/dice-${diceRoll}.png" alt="Image of a dice with ${diceRoll} on the face" width="128" height="128">
        """
    }

    @PostMapping("/addUser")
    fun addUser(@RequestBody request: StudentAuthRequest): String {

        if (studentRepository.findByName(request.name) == null) {
            return "User already exists."
        }

        // Fully initialized, contains ID
        val savedStudent = studentRepository.save(
            Student(
                name = request.name,
                password = passwordEncoder.encode(request.password)
            )
        )

        return "Created Student ${savedStudent.name} with ID ${savedStudent.id}"
    }

    @GetMapping("/getUser")
    fun getUser(@RequestBody request: GetStudentRequest): String {

        // What happens when the user doesn't exist?
        val savedStudent = studentRepository.findByName(request.name)
        return if (savedStudent == null) {
            "Student not found."
        } else {
            "ID: ${savedStudent.id}, Name: ${savedStudent.name}"
        }
    }

    @GetMapping("/tryAuthentication")
    fun tryAuthentication(): String {
        return "Welcome ${SecurityContextHolder.getContext().authentication.name}!"
    }

    @PostMapping("/deleteUser")
    fun deleteUser(@RequestBody request: GetStudentRequest): String {

        // TODO: Move this logic over to roles
        if (SecurityContextHolder.getContext().authentication.name != "admin") {
            return "Only admin can delete users"
        } else {
            val student = studentRepository.findByName(request.name)
            if (student == null) {
                return "Student not found."
            } else {
                studentRepository.delete(student)
                return "Deleted student ${request.name}"
            }
        }
    }

}

fun main(args: Array<String>) {
    runApplication<TestangApplication>(*args)
}
