package org.example.testang

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Entity
data class Student(
    @Id @GeneratedValue var id: Long? = null,
    var name: String,
    var password: String,
)

@Repository
interface StudentRepository : JpaRepository<Student, Long> {
    // Spring JPA plugin automatically populates this with methods!
    // BTW - JPA is "Java Persistence API" - basically for database shi

    // Make spring automatically figure out the implementation of this
    fun findByName(name: String): Student?
}