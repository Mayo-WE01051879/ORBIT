package io.infinite.orbit.entities

import com.fasterxml.jackson.annotation.JsonFormat
import groovy.transform.ToString

import javax.persistence.*

@Entity
@Table(name = "users")
@ToString(includeNames = true, includeFields = true)
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    Long id

    String namespace

    @Column(unique = true)
    UUID guid = UUID.randomUUID()

    String phone

    @JsonFormat(timezone = "UTC", pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    Date creationDate = new Date()

}
